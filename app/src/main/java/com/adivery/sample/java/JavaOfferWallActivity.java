package com.adivery.sample.java;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.adivery.sample.AdEventLog;
import com.adivery.sample.ProfileStore;
import com.adivery.sample.R;
import com.adivery.sample.SystemBars;
import com.adivery.sample.Wallet;
import com.adivery.sample.databinding.ActivityOfferWallBinding;
import com.adivery.sdk.Adivery;
import com.adivery.sdk.AdiveryListener;

/**
 * Offer wall in Java.
 *
 * <p>Unlike the other full screen formats, the offer wall is never loaded ahead of time. Its
 * missions, step states and deadlines are fetched when it loads, so a wall loaded early would show
 * stale ones. Call {@link Adivery#prepareOfferWallAd} only when the user asks for the wall, and
 * show it as soon as {@code onOfferWallAdLoaded} arrives. Adivery does not reload a closed wall
 * either, so every tap on the button loads it again.
 *
 * <p>Loading can take a few seconds. {@link #waitingForOfferWall} records whether the user is still
 * waiting, and the wall is only shown if they are, so it never opens when they no longer expect it.
 */
public class JavaOfferWallActivity extends AppCompatActivity {

    /** How long the user is kept waiting before the load is given up on. */
    private static final long LOAD_TIMEOUT_MS = 10_000L;

    private ActivityOfferWallBinding binding;
    private AdEventLog eventLog;
    private Wallet wallet;
    private String placementId;

    /** Set from the tap on the button until the wall is shown or the wait is given up. */
    private boolean waitingForOfferWall = false;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable loadTimeout = () -> {
        eventLog.log(getString(R.string.offer_wall_timed_out));
        stopWaiting();
    };

    private final AdiveryListener listener = new AdiveryListener() {

        @Override
        public void onOfferWallAdLoaded(@NonNull String placementId) {
            eventLog.log("onOfferWallAdLoaded");
            if (waitingForOfferWall) {
                stopWaiting();
                Adivery.showAd(placementId);
            } else {
                eventLog.log(getString(R.string.offer_wall_not_waiting));
            }
        }

        @Override
        public void onOfferWallAdShown(@NonNull String placementId) {
            eventLog.log("onOfferWallAdShown");
        }

        /**
         * Fires each time the user claims the reward of a mission step, so possibly several times
         * per wall, and while the wall is still on screen.
         *
         * <p>This screen's listener is removed in {@code onDestroy}, so a reward claimed after the
         * system destroyed this screen behind the wall would be lost. To rule that out, credit it
         * from a global listener instead (see {@code GlobalAdListener}), but never from both or
         * every step is credited twice.
         */
        @Override
        public void onOfferWallMissionRewarded(
                @NonNull String placementId, @NonNull String currency, int rewardAmount) {
            eventLog.log("onOfferWallMissionRewarded(currency = " + currency
                    + ", rewardAmount = " + rewardAmount + ")");
            wallet.add(rewardAmount);
        }

        @Override
        public void onOfferWallAdClosed(@NonNull String placementId) {
            eventLog.log("onOfferWallAdClosed");
            showBalance();
        }

        /** Loading failures are reported here. Stop waiting so the user can try again. */
        @Override
        public void log(@NonNull String placementId, String message) {
            eventLog.log(message);
            stopWaiting();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityOfferWallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SystemBars.applySystemBarInsets(binding.getRoot());

        placementId = ProfileStore.get(this).getActiveProfile().getOfferWallPlacementId();
        eventLog = new AdEventLog(binding.log);
        wallet = new Wallet(this);

        binding.title.setText(R.string.sample_offer_wall_java);
        binding.placementId.setText(getString(R.string.placement_id, placementId));
        showBalance();

        if (placementId.isEmpty()) {
            binding.noPlacement.setVisibility(View.VISIBLE);
            binding.open.setEnabled(false);
            return;
        }

        // A placement listener only receives callbacks for this placement id. Use
        // Adivery.addGlobalListener to receive them for every placement instead.
        Adivery.addPlacementListener(placementId, listener);

        binding.open.setOnClickListener(view -> openOfferWall());
    }

    @Override
    protected void onStop() {
        // The user has left the screen, so they no longer expect the wall to open. Showing the wall
        // stops this screen too, but the wait has already ended by then.
        if (waitingForOfferWall) {
            eventLog.log(getString(R.string.offer_wall_left_screen));
            stopWaiting();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // Anonymous listeners hold a reference to this activity, so always detach them.
        Adivery.removePlacementListener(placementId);
        super.onDestroy();
    }

    private void openOfferWall() {
        if (waitingForOfferWall) {
            return;
        }
        waitingForOfferWall = true;
        setLoading(true);
        handler.postDelayed(loadTimeout, LOAD_TIMEOUT_MS);

        eventLog.log("prepareOfferWallAd");
        Adivery.prepareOfferWallAd(this, placementId);
    }

    private void stopWaiting() {
        waitingForOfferWall = false;
        handler.removeCallbacks(loadTimeout);
        setLoading(false);
    }

    private void setLoading(boolean loading) {
        binding.progress.setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
        binding.open.setEnabled(!loading);
    }

    private void showBalance() {
        binding.balance.setText(getString(R.string.offer_wall_balance, wallet.getBalance()));
    }
}

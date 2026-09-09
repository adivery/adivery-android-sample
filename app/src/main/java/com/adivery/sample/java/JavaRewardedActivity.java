package com.adivery.sample.java;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.adivery.sample.AdEventLog;
import com.adivery.sample.ProfileStore;
import com.adivery.sample.R;
import com.adivery.sample.SystemBars;
import com.adivery.sample.databinding.ActivityFullScreenAdBinding;
import com.adivery.sdk.Adivery;
import com.adivery.sdk.AdiveryListener;
import com.google.android.material.snackbar.Snackbar;

/**
 * Rewarded ads in Java.
 *
 * <p>Identical to the interstitial flow apart from {@code onRewardedAdClosed}, whose {@code
 * isRewarded} flag tells you whether the user watched enough of the ad to earn the reward. Grant the
 * reward there and nowhere else.
 */
public class JavaRewardedActivity extends AppCompatActivity {

    private ActivityFullScreenAdBinding binding;
    private AdEventLog eventLog;
    private String placementId;

    private final AdiveryListener listener = new AdiveryListener() {

        @Override
        public void onRewardedAdLoaded(@NonNull String placementId) {
            eventLog.log("onRewardedAdLoaded");
            setShowEnabled(true);
        }

        @Override
        public void onRewardedAdShown(@NonNull String placementId) {
            eventLog.log("onRewardedAdShown");
            setShowEnabled(false);
        }

        @Override
        public void onRewardedAdClicked(@NonNull String placementId) {
            eventLog.log("onRewardedAdClicked");
        }

        @Override
        public void onRewardedAdClosed(@NonNull String placementId, boolean isRewarded) {
            eventLog.log("onRewardedAdClosed(isRewarded = " + isRewarded + ")");
            if (isRewarded) {
                grantReward();
            }
        }

        @Override
        public void log(@NonNull String placementId, String message) {
            eventLog.log(message);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityFullScreenAdBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SystemBars.applySystemBarInsets(binding.getRoot());

        placementId = ProfileStore.get(this).getActiveProfile().getRewardedPlacementId();
        eventLog = new AdEventLog(binding.log);

        binding.title.setText(R.string.sample_rewarded_java);
        binding.placementId.setText(getString(R.string.placement_id, placementId));
        binding.show.setEnabled(Adivery.isLoaded(placementId));

        // A placement listener only receives callbacks for this placement id. Use
        // Adivery.addGlobalListener to receive them for every placement instead.
        Adivery.addPlacementListener(placementId, listener);

        binding.load.setOnClickListener(view -> {
            eventLog.log("prepareRewardedAd");
            Adivery.prepareRewardedAd(this, placementId);
        });
        binding.show.setOnClickListener(view -> {
            if (Adivery.isLoaded(placementId)) {
                Adivery.showAd(placementId);
            } else {
                eventLog.log(getString(R.string.ad_not_ready));
            }
        });
    }

    @Override
    protected void onDestroy() {
        // Anonymous listeners hold a reference to this activity, so always detach them.
        Adivery.removePlacementListener(placementId);
        super.onDestroy();
    }

    private void grantReward() {
        Snackbar.make(binding.getRoot(), R.string.reward_granted, Snackbar.LENGTH_LONG).show();
    }

    private void setShowEnabled(boolean enabled) {
        binding.show.setEnabled(enabled);
    }
}

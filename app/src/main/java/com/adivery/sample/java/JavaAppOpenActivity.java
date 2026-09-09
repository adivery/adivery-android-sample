package com.adivery.sample.java;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.adivery.sample.AdEventLog;
import com.adivery.sample.AppOpenAdManager;
import com.adivery.sample.ProfileStore;
import com.adivery.sample.R;
import com.adivery.sample.SystemBars;
import com.adivery.sample.databinding.ActivityFullScreenAdBinding;
import com.adivery.sdk.Adivery;
import com.adivery.sdk.AdiveryListener;

/**
 * App open ads in Java.
 *
 * <p>Note that {@link Adivery#prepareAppOpenAd} and {@link Adivery#showAppOpenAd} both take an
 * {@code Activity} rather than a {@code Context}, and that showing uses {@code showAppOpenAd}
 * instead of the generic {@code showAd}.
 *
 * <p>The switch on this screen enables {@link AppOpenAdManager}, which is the pattern Adivery
 * recommends: show the ad when the user returns to the app after being away for a few seconds.
 */
public class JavaAppOpenActivity extends AppCompatActivity {

    private ActivityFullScreenAdBinding binding;
    private AdEventLog eventLog;
    private String placementId;

    private final AdiveryListener listener = new AdiveryListener() {

        @Override
        public void onAppOpenAdLoaded(@NonNull String placementId) {
            eventLog.log("onAppOpenAdLoaded");
            setShowEnabled(true);
        }

        @Override
        public void onAppOpenAdShown(@NonNull String placementId) {
            eventLog.log("onAppOpenAdShown");
            setShowEnabled(false);
        }

        @Override
        public void onAppOpenAdClicked(@NonNull String placementId) {
            eventLog.log("onAppOpenAdClicked");
        }

        @Override
        public void onAppOpenAdClosed(@NonNull String placementId) {
            eventLog.log("onAppOpenAdClosed");
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

        placementId = ProfileStore.get(this).getActiveProfile().getAppOpenPlacementId();
        eventLog = new AdEventLog(binding.log);

        binding.title.setText(R.string.sample_app_open_java);
        binding.placementId.setText(getString(R.string.placement_id, placementId));
        binding.show.setEnabled(Adivery.isLoaded(placementId));

        // A placement listener only receives callbacks for this placement id. Use
        // Adivery.addGlobalListener to receive them for every placement instead.
        Adivery.addPlacementListener(placementId, listener);

        binding.load.setOnClickListener(view -> {
            eventLog.log("prepareAppOpenAd");
            Adivery.prepareAppOpenAd(this, placementId);
        });
        binding.show.setOnClickListener(view -> {
            if (Adivery.isLoaded(placementId)) {
                Adivery.showAppOpenAd(this, placementId);
            } else {
                eventLog.log(getString(R.string.ad_not_ready));
            }
        });

        binding.autoShow.setVisibility(View.VISIBLE);
        binding.autoShow.setChecked(AppOpenAdManager.getAutoShowEnabled());
        binding.autoShow.setOnCheckedChangeListener((button, isChecked) -> {
            AppOpenAdManager.setAutoShowEnabled(isChecked);
            eventLog.log(getString(isChecked ? R.string.auto_show_on : R.string.auto_show_off));
            // Nothing is shown on return unless an ad has been prepared first.
            if (isChecked && !Adivery.isLoaded(placementId)) {
                Adivery.prepareAppOpenAd(this, placementId);
            }
        });
    }

    @Override
    protected void onDestroy() {
        // Anonymous listeners hold a reference to this activity, so always detach them.
        Adivery.removePlacementListener(placementId);
        super.onDestroy();
    }

    private void setShowEnabled(boolean enabled) {
        binding.show.setEnabled(enabled);
    }
}

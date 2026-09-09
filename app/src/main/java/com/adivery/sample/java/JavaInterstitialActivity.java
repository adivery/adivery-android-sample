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

/**
 * Interstitial ads in Java.
 *
 * <p>Ask for the ad with {@link Adivery#prepareInterstitialAd}, wait for {@code
 * onInterstitialAdLoaded}, then show it with {@link Adivery#showAd}. Adivery prepares the next ad by
 * itself once one has been shown, so {@code prepareInterstitialAd} does not have to be called again.
 */
public class JavaInterstitialActivity extends AppCompatActivity {

    private ActivityFullScreenAdBinding binding;
    private AdEventLog eventLog;
    private String placementId;

    private final AdiveryListener listener = new AdiveryListener() {

        @Override
        public void onInterstitialAdLoaded(@NonNull String placementId) {
            eventLog.log("onInterstitialAdLoaded");
            setShowEnabled(true);
        }

        @Override
        public void onInterstitialAdShown(@NonNull String placementId) {
            eventLog.log("onInterstitialAdShown");
            // The shown ad is consumed; Adivery starts loading the next one automatically.
            setShowEnabled(false);
        }

        @Override
        public void onInterstitialAdClicked(@NonNull String placementId) {
            eventLog.log("onInterstitialAdClicked");
        }

        @Override
        public void onInterstitialAdClosed(@NonNull String placementId) {
            eventLog.log("onInterstitialAdClosed");
        }

        /** Loading failures and other SDK diagnostics are reported here. */
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

        placementId = ProfileStore.get(this).getActiveProfile().getInterstitialPlacementId();
        eventLog = new AdEventLog(binding.log);

        binding.title.setText(R.string.sample_interstitial_java);
        binding.placementId.setText(getString(R.string.placement_id, placementId));
        binding.show.setEnabled(Adivery.isLoaded(placementId));

        // A placement listener only receives callbacks for this placement id. Use
        // Adivery.addGlobalListener to receive them for every placement instead.
        Adivery.addPlacementListener(placementId, listener);

        binding.load.setOnClickListener(view -> {
            eventLog.log("prepareInterstitialAd");
            Adivery.prepareInterstitialAd(this, placementId);
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

    private void setShowEnabled(boolean enabled) {
        binding.show.setEnabled(enabled);
    }
}

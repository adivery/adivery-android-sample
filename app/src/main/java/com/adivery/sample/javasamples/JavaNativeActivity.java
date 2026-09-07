package com.adivery.sample.javasamples;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.adivery.sample.AdEventLog;
import com.adivery.sample.ProfileStore;
import com.adivery.sample.R;
import com.adivery.sample.SystemBars;
import com.adivery.sample.databinding.ActivityNativeAdBinding;
import com.adivery.sdk.AdiveryAdListener;

/**
 * Native ads in Java.
 *
 * <p>{@code AdiveryNativeAdView} inflates the template given by {@code app:adivery_native_ad_layout}
 * and fills the views it finds by id: {@code adivery_wrapper}, {@code adivery_headline}, {@code
 * adivery_description}, {@code adivery_advertiser}, {@code adivery_call_to_action}, {@code
 * adivery_image} and {@code adivery_icon}. Only the headline and the call to action are mandatory.
 * See {@code res/layout/view_native_ad.xml}.
 */
public class JavaNativeActivity extends AppCompatActivity {

    private ActivityNativeAdBinding binding;
    private AdEventLog eventLog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityNativeAdBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SystemBars.applySystemBarInsets(binding.getRoot());

        String placementId = ProfileStore.get(this).getActiveProfile().getNativePlacementId();
        eventLog = new AdEventLog(binding.log);

        binding.title.setText(R.string.sample_native_java);
        binding.placementId.setText(getString(R.string.placement_id, placementId));

        binding.nativeAdView.setPlacementId(placementId);
        // This listener belongs to the ad view, not to a global registry, so it is released
        // with the view and needs no detaching in onDestroy.
        binding.nativeAdView.setListener(new AdiveryAdListener() {
            @Override
            public void onAdLoaded() {
                eventLog.log("onAdLoaded");
            }

            @Override
            public void onAdShown() {
                eventLog.log("onAdShown");
            }

            @Override
            public void onAdClicked() {
                eventLog.log("onAdClicked");
            }

            @Override
            public void onError(@NonNull String reason) {
                eventLog.log("onError: " + reason);
            }
        });

        binding.load.setOnClickListener(view -> {
            eventLog.log("loadAd");
            binding.nativeAdView.loadAd();
        });

        // Load once the view has been measured, so the ad is rendered into a laid out template.
        binding.nativeAdView.post(() -> binding.nativeAdView.loadAd());
    }
}

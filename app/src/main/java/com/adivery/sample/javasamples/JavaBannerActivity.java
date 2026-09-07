package com.adivery.sample.javasamples;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.adivery.sample.AdEventLog;
import com.adivery.sample.ProfileStore;
import com.adivery.sample.R;
import com.adivery.sample.SystemBars;
import com.adivery.sample.databinding.ActivityBannerAdBinding;
import com.adivery.sdk.AdiveryAdListener;
import com.adivery.sdk.AdiveryBannerAdView;
import com.adivery.sdk.BannerSize;

/**
 * Banner ads in Java.
 *
 * <p>A banner can be declared straight in XML:
 *
 * <pre>{@code
 * <com.adivery.sdk.AdiveryBannerAdView
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     app:placement_id="YOUR_PLACEMENT_ID"
 *     app:banner_size="banner" />
 * }</pre>
 *
 * <p>This screen builds it in code instead, so the four sizes can be swapped at runtime. A fresh
 * view is created per size because {@link AdiveryBannerAdView} reserves space for the size it was
 * loaded with.
 */
public class JavaBannerActivity extends AppCompatActivity {

    private ActivityBannerAdBinding binding;
    private AdEventLog eventLog;
    private String placementId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityBannerAdBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SystemBars.applySystemBarInsets(binding.getRoot());

        placementId = ProfileStore.get(this).getActiveProfile().getBannerPlacementId();
        eventLog = new AdEventLog(binding.log);

        binding.title.setText(R.string.sample_banner_java);
        binding.placementId.setText(getString(R.string.placement_id, placementId));

        binding.sizeBanner.setOnClickListener(v -> showBanner(BannerSize.BANNER, "BANNER"));
        binding.sizeLargeBanner.setOnClickListener(v -> showBanner(BannerSize.LARGE_BANNER, "LARGE_BANNER"));
        binding.sizeMediumRectangle.setOnClickListener(v -> showBanner(BannerSize.MEDIUM_RECTANGLE, "MEDIUM_RECTANGLE"));
        binding.sizeSmartBanner.setOnClickListener(v -> showBanner(BannerSize.SMART_BANNER, "SMART_BANNER"));
    }

    private void showBanner(BannerSize size, String sizeName) {
        eventLog.log(getString(R.string.loading_banner, sizeName));
        binding.bannerContainer.removeAllViews();

        AdiveryBannerAdView bannerView = new AdiveryBannerAdView(this);
        bannerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        bannerView.setPlacementId(placementId);
        bannerView.setBannerSize(size);
        // Loaded banners are displayed by the view itself; the listener is only for reacting.
        // It belongs to the ad view, not to a global registry, so it is released with the
        // view and needs no detaching in onDestroy.
        bannerView.setBannerAdListener(new AdiveryAdListener() {
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

        binding.bannerContainer.addView(bannerView);
        bannerView.loadAd();
    }
}

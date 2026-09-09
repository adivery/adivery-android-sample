package com.adivery.sample.java;

import android.os.Bundle;
import android.view.View;
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
import com.adivery.sample.databinding.ViewBannerCodeSectionBinding;
import com.adivery.sample.databinding.ViewBannerXmlSectionBinding;
import com.adivery.sdk.AdiveryAdListener;
import com.adivery.sdk.AdiveryBannerAdView;
import com.adivery.sdk.BannerSize;
import com.google.android.material.tabs.TabLayout;

import java.util.Arrays;
import java.util.List;

/**
 * Banner ads in Java.
 * Banners can be created in two ways, both in code or directly in XML.
 */
public class JavaBannerActivity extends AppCompatActivity {

    private static final int TAB_CODE = 0;
    private static final int TAB_XML = 1;

    private ActivityBannerAdBinding binding;
    private AdEventLog eventLog;
    private String placementId;

    private List<AdiveryBannerAdView> xmlBanners;

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

        binding.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
                binding.codeSection.getRoot().setVisibility(tab.getPosition() == TAB_CODE ? View.VISIBLE : View.GONE);
                binding.xmlSection.getRoot().setVisibility(tab.getPosition() == TAB_XML ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onTabUnselected(@NonNull TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(@NonNull TabLayout.Tab tab) {
            }
        });

        ViewBannerCodeSectionBinding code = binding.codeSection;
        code.sizeBanner.setOnClickListener(v -> showBanner(BannerSize.BANNER, "BANNER"));
        code.sizeLargeBanner.setOnClickListener(v -> showBanner(BannerSize.LARGE_BANNER, "LARGE_BANNER"));
        code.sizeMediumRectangle.setOnClickListener(v -> showBanner(BannerSize.MEDIUM_RECTANGLE, "MEDIUM_RECTANGLE"));
        code.sizeSmartBanner.setOnClickListener(v -> showBanner(BannerSize.SMART_BANNER, "SMART_BANNER"));

        ViewBannerXmlSectionBinding xml = binding.xmlSection;
        xmlBanners = Arrays.asList(xml.xmlBanner, xml.xmlLargeBanner, xml.xmlMediumRectangle, xml.xmlSmartBanner);
        for (AdiveryBannerAdView banner : xmlBanners) {
            banner.setPlacementId(placementId);
            banner.setBannerAdListener(listener("xml"));
        }

        xml.xmlSizeBanner.setOnClickListener(v -> showXmlBanner(xml.xmlBanner, "BANNER"));
        xml.xmlSizeLargeBanner.setOnClickListener(v -> showXmlBanner(xml.xmlLargeBanner, "LARGE_BANNER"));
        xml.xmlSizeMediumRectangle.setOnClickListener(v -> showXmlBanner(xml.xmlMediumRectangle, "MEDIUM_RECTANGLE"));
        xml.xmlSizeSmartBanner.setOnClickListener(v -> showXmlBanner(xml.xmlSmartBanner, "SMART_BANNER"));
    }

    private void showBanner(BannerSize size, String sizeName) {
        eventLog.log(getString(R.string.loading_banner, sizeName));
        binding.codeSection.bannerContainer.removeAllViews();

        AdiveryBannerAdView bannerView = new AdiveryBannerAdView(this);
        bannerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        bannerView.setPlacementId(placementId);
        bannerView.setBannerSize(size);
        bannerView.setBannerAdListener(listener("code"));

        binding.codeSection.bannerContainer.addView(bannerView);
        bannerView.loadAd();
    }

    private void showXmlBanner(AdiveryBannerAdView banner, String sizeName) {
        eventLog.log(getString(R.string.loading_banner, sizeName));
        for (AdiveryBannerAdView candidate : xmlBanners) {
            candidate.setVisibility(candidate == banner ? View.VISIBLE : View.GONE);
        }
        banner.loadAd();
    }

    private AdiveryAdListener listener(String source) {
        return new AdiveryAdListener() {
            @Override
            public void onAdLoaded() {
                eventLog.log(source + ": onAdLoaded");
            }

            @Override
            public void onAdShown() {
                eventLog.log(source + ": onAdShown");
            }

            @Override
            public void onAdClicked() {
                eventLog.log(source + ": onAdClicked");
            }

            @Override
            public void onError(@NonNull String reason) {
                eventLog.log(source + ": onError: " + reason);
            }
        };
    }
}

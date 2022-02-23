package com.adivery.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.adivery.sdk.AdiveryBannerAdView;
import com.adivery.sdk.BannerSize;

public class BannerActivity extends AppCompatActivity {
    private String placementId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        placementId = getIntent().getStringExtra("placement_id");
        if (placementId == null || placementId.isEmpty()){
            finish();
            return;
        }
        findViewById(R.id.banner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayBanner(BannerSize.BANNER);
            }
        });
        findViewById(R.id.large_banner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayBanner(BannerSize.LARGE_BANNER);
            }
        });
        findViewById(R.id.medium_rectangle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayBanner(BannerSize.MEDIUM_RECTANGLE);
            }
        });
        findViewById(R.id.smart_banner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayBanner(BannerSize.SMART_BANNER);
            }
        });
    }

    private void displayBanner(BannerSize bannerSize) {
        Log.d("BannerActivity", "displaying banner");
        AdiveryBannerAdView bannerView = findViewById(R.id.banner_view);
        bannerView.setPlacementId(placementId);
        bannerView.setBannerSize(bannerSize);
        bannerView.loadAd();
    }
}
package com.adivery.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.adivery.sdk.AdiveryNativeAdView;

public class NativeActivity extends AppCompatActivity {

    private String placementId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);
        placementId = getIntent().getStringExtra("placement_id");
        if (placementId == null || placementId.isEmpty()){
            finish();
            return;
        }

        findViewById(R.id.native_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.adivery_image).setVisibility(View.VISIBLE);
            }
        });

        final AdiveryNativeAdView nativeAd = findViewById(R.id.native_view);
        nativeAd.setPlacementId(placementId);
        nativeAd.post(new Runnable() {
            @Override
            public void run() {
                nativeAd.loadAd();
            }
        });
    }
}
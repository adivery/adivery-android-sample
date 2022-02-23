package com.adivery.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.UUID;

public class CreateProfileActivity extends AppCompatActivity {
    private ProfileManager profileManager;

    TextInputLayout profileNameLayout;
    TextInputEditText textProfileName;

    TextInputLayout appIdLayout;
    TextInputLayout rewardedLayout;
    TextInputLayout bannerLayout;
    TextInputLayout nativeLayout;
    TextInputLayout interstitialLayout;
    TextInputLayout appOpenLayout;

    TextInputEditText textRewarded;
    TextInputEditText textAppId;
    TextInputEditText textBanner;
    TextInputEditText textNative;
    TextInputEditText textInterstitial;
    TextInputEditText textAppOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        profileManager = new ProfileManager(this);

        profileNameLayout = findViewById(R.id.profile_layout);
        textProfileName = findViewById(R.id.text_profile);

        appIdLayout = findViewById(R.id.app_id_layout);
        rewardedLayout = findViewById(R.id.rewarded_layout);
        bannerLayout = findViewById(R.id.banner_layout);
        nativeLayout = findViewById(R.id.native_layout);
        interstitialLayout = findViewById(R.id.interstitial_layout);
        appOpenLayout = findViewById(R.id.app_open_layout);

        textRewarded = findViewById(R.id.text_rewarded);
        textAppId = findViewById(R.id.text_app_id);
        textBanner = findViewById(R.id.text_banner);
        textNative = findViewById(R.id.text_native);
        textInterstitial = findViewById(R.id.text_interstitial);
        textAppOpen = findViewById(R.id.text_app_open);


        findViewById(R.id.create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndCreateProfile();
            }
        });
    }

    private void validateAndCreateProfile() {
        if (validateInput()) {
            saveProfile();
        }
    }

    private void saveProfile() {
        String rewardedPlacementId = textRewarded.getText().toString();
        String appId = textAppId.getText().toString();
        String bannerPlacementId = textBanner.getText().toString();
        String nativePlacementId = textNative.getText().toString();
        String interstitialPlacementId = textInterstitial.getText().toString();
        String appOpenPlacementId = textAppOpen.getText().toString();
        String profile = textProfileName.getText().toString();
        profileManager.addProfile(profile);
        profileManager.setAppId(profile, appId);
        profileManager.setBannerPlacementId(profile, bannerPlacementId);
        profileManager.setNativePlacementId(profile, nativePlacementId);
        profileManager.setInterstitialPlacementId(profile, interstitialPlacementId);
        profileManager.setRewardedPlacementId(profile, rewardedPlacementId);
        profileManager.setAppOpenPlacementId(profile, appOpenPlacementId);

        Toast.makeText(this, R.string.message_profile_created, Toast.LENGTH_LONG).show();

        finish();
    }

    private boolean validateInput() {
        boolean isValid = true;

        String profileName = textProfileName.getText().toString();

        String rewardedPlacementId = textRewarded.getText().toString();
        String appId = textAppId.getText().toString();
        String bannerPlacementId = textBanner.getText().toString();
        String nativePlacementId = textNative.getText().toString();
        String interstitialPlacementId = textInterstitial.getText().toString();
        String appOpenPlacementId = textAppOpen.getText().toString();

        if (profileName.isEmpty()){
            profileNameLayout.setError(getString(R.string.error_empty));
            isValid = false;
        }

        if (!isValidUUID(rewardedPlacementId)) {
            rewardedLayout.setError(getString(R.string.error_invalid_uuid));
            isValid = false;
        }
        if (!isValidUUID(appId)) {
            appOpenLayout.setError(getString(R.string.error_invalid_uuid));
            isValid = false;
        }
        if (!isValidUUID(bannerPlacementId)) {
            bannerLayout.setError(getString(R.string.error_invalid_uuid));
            isValid = false;
        }
        if (!isValidUUID(nativePlacementId)) {
            nativeLayout.setError(getString(R.string.error_invalid_uuid));
            isValid = false;
        }
        if (!isValidUUID(interstitialPlacementId)) {
            interstitialLayout.setError(getString(R.string.error_invalid_uuid));
            isValid = false;
        }
        if (!isValidUUID(appOpenPlacementId)) {
            appOpenLayout.setError(getString(R.string.error_invalid_uuid));
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidUUID(String rewardedPlacementId) {
        try {
            UUID.fromString(rewardedPlacementId);
        } catch (RuntimeException e) {
            return false;
        }
        return true;
    }
}
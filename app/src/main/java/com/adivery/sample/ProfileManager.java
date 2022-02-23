package com.adivery.sample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProfileManager {
    private Context context;

    public ProfileManager(Context context) {
        this.context = context;
    }

    private SharedPreferences getSharedPrefs() {
        return context.getSharedPreferences("ad_profiles", Context.MODE_PRIVATE);
    }

    @SuppressLint("MutatingSharedPrefs")
    public void addProfile(String profile) {
        Set<String> profiles = getSharedPrefs().getStringSet("profiles", new HashSet<String>());
        profiles.add(profile);
        getSharedPrefs().edit().putStringSet("profiles", profiles).apply();
    }

    public List<String> getProfiles() {
        return new ArrayList<>(getSharedPrefs().getStringSet("profiles", new HashSet<String>()));
    }

    public String getAppId(String profile) {
        return getSharedPrefs().getString(profile + "_app_id", "");
    }

    public String getRewardedPlacementId(String profile) {
        return getSharedPrefs().getString(profile + "_rewarded", "");
    }

    public String getInterstitialPlacementId(String profile) {
        return getSharedPrefs().getString(profile + "_interstitial", "");
    }

    public String getBannerPlacementId(String profile) {
        return getSharedPrefs().getString(profile + "_banner", "");
    }

    public String getNativePlacementId(String profile) {
        return getSharedPrefs().getString(profile + "_native", "");
    }

    public String getAppOpenPlacementId(String profile){
        return getSharedPrefs().getString(profile + "_app_open", "");
    }

    public void setAppId(String profile, String appId) {
        getSharedPrefs().edit().putString(profile + "_app_id", appId).apply();
    }

    public void setRewardedPlacementId(String profile, String placementId) {
        getSharedPrefs().edit().putString(profile + "_rewarded", placementId).apply();
    }

    public void setInterstitialPlacementId(String profile, String placementId) {
        getSharedPrefs().edit().putString(profile + "_interstitial", placementId).apply();
    }

    public void setBannerPlacementId(String profile, String placementId) {
        getSharedPrefs().edit().putString(profile + "_banner", placementId).apply();
    }

    public void setNativePlacementId(String profile, String placementId) {
        getSharedPrefs().edit().putString(profile + "_native", placementId).apply();
    }
    public void setAppOpenPlacementId(String profile, String placementId){
        getSharedPrefs().edit().putString(profile + "_app_open", placementId).apply();
    }

}

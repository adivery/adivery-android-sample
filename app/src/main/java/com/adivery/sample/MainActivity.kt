package com.adivery.sample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.adivery.sdk.Adivery

class MainActivity : AppCompatActivity() {

    lateinit var profileManager: ProfileManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        profileManager = ProfileManager(this)

        addAdiveryProfile()

        val profileSpinner = findViewById<Spinner>(R.id.profile_spinner)

        findViewById<View>(R.id.configure).setOnClickListener {
            Adivery.setLoggingEnabled(true)
            val profile = profileSpinner.selectedItem.toString()
            Adivery.configure(application, profileManager.getAppId(profile))

            Adivery.prepareInterstitialAd(
                baseContext,
                profileManager.getInterstitialPlacementId(profile)
            )
            Adivery.prepareRewardedAd(baseContext, profileManager.getRewardedPlacementId(profile))
            Adivery.prepareAppOpenAd(this, profileManager.getAppOpenPlacementId(profile))
        }

        findViewById<View>(R.id.create_profile).setOnClickListener {
            startActivity(
                Intent(this@MainActivity, CreateProfileActivity::class.java)
            )
        }

        findViewById<View>(R.id.rewarded).setOnClickListener {
            val profile = profileSpinner.selectedItem.toString()
            Adivery.showAd(profileManager.getRewardedPlacementId(profile))
        }
        findViewById<View>(R.id.interstitial).setOnClickListener {
            val profile = profileSpinner.selectedItem.toString()
            Adivery.showAd(profileManager.getInterstitialPlacementId(profile))
        }

        findViewById<View>(R.id.app_open).setOnClickListener {
            val profile = profileSpinner.selectedItem.toString()
            Adivery.showAppOpenAd(this@MainActivity, profileManager.getAppOpenPlacementId(profile))
        }

        findViewById<View>(R.id.banner).setOnClickListener {
            val profile = profileSpinner.selectedItem.toString()
            val intent = Intent(this, BannerActivity::class.java).apply {
                putExtra("placement_id", profileManager.getBannerPlacementId(profile))
            }
            startActivity(intent)
        }

        findViewById<View>(R.id.native_ad).setOnClickListener {
            val profile = profileSpinner.selectedItem.toString()
            val intent = Intent(this, NativeActivity::class.java).apply {
                putExtra("placement_id", profileManager.getNativePlacementId(profile))
            }
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val profileSpinner = findViewById<Spinner>(R.id.profile_spinner)
        profileSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, profileManager.profiles)

    }

    fun addAdiveryProfile() {
        val appId = "7e27fb38-5aff-473a-998f-437b89426f66"
        val rewardedPlacementId = "2efedcaa-fcc0-4610-a025-109ff17594af"
        val bannerPlacementId = "5f2c4c86-a6ec-4735-9a44-f881fe40789f"
        val nativePlacementId = "25928bf1-d4f7-432c-aaf7-1780602796c3"
        val interstitialPlacementId = "de5db046-765d-478f-bb2e-30dc2eaf3f51"
        val appOpenPlacementId = "9e9dd375-a1fe-4c2b-8432-b5bf8a5095f6"
        val profile = "adivery"
        profileManager.addProfile(profile)
        profileManager.setAppId(profile, appId)
        profileManager.setBannerPlacementId(profile, bannerPlacementId)
        profileManager.setNativePlacementId(profile, nativePlacementId)
        profileManager.setInterstitialPlacementId(profile, interstitialPlacementId)
        profileManager.setRewardedPlacementId(profile, rewardedPlacementId)
        profileManager.setAppOpenPlacementId(profile, appOpenPlacementId)

    }
}
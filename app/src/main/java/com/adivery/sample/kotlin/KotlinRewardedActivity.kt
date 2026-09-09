package com.adivery.sample.kotlin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.adivery.sample.AdEventLog
import com.adivery.sample.ProfileStore
import com.adivery.sample.R
import com.adivery.sample.applySystemBarInsets
import com.adivery.sample.databinding.ActivityFullScreenAdBinding
import com.adivery.sdk.Adivery
import com.adivery.sdk.AdiveryListener
import com.google.android.material.snackbar.Snackbar

/**
 * Rewarded ads in Kotlin.
 *
 * Identical to the interstitial flow apart from `onRewardedAdClosed`, whose `isRewarded` flag tells
 * you whether the user watched enough of the ad to earn the reward. Grant the reward there and
 * nowhere else.
 */
class KotlinRewardedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullScreenAdBinding
    private lateinit var eventLog: AdEventLog
    private lateinit var placementId: String

    private val listener = object : AdiveryListener() {

        override fun onRewardedAdLoaded(placementId: String) {
            eventLog.log("onRewardedAdLoaded")
            setShowEnabled(true)
        }

        override fun onRewardedAdShown(placementId: String) {
            eventLog.log("onRewardedAdShown")
            setShowEnabled(false)
        }

        override fun onRewardedAdClicked(placementId: String) = eventLog.log("onRewardedAdClicked")

        override fun onRewardedAdClosed(placementId: String, isRewarded: Boolean) {
            eventLog.log("onRewardedAdClosed(isRewarded = $isRewarded)")
            if (isRewarded) grantReward()
        }

        override fun log(placementId: String, message: String) = eventLog.log(message)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFullScreenAdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applySystemBarInsets()

        placementId = ProfileStore.get(this).activeProfile.rewardedPlacementId
        eventLog = AdEventLog(binding.log)

        binding.title.setText(R.string.sample_rewarded_kotlin)
        binding.placementId.text = getString(R.string.placement_id, placementId)
        binding.show.isEnabled = Adivery.isLoaded(placementId)

        // A placement listener only receives callbacks for this placement id. Use
        // Adivery.addGlobalListener to receive them for every placement instead.
        Adivery.addPlacementListener(placementId, listener)

        binding.load.setOnClickListener {
            eventLog.log("prepareRewardedAd")
            Adivery.prepareRewardedAd(this, placementId)
        }
        binding.show.setOnClickListener {
            if (Adivery.isLoaded(placementId)) {
                Adivery.showAd(placementId)
            } else {
                eventLog.log(getString(R.string.ad_not_ready))
            }
        }
    }

    override fun onDestroy() {
        // Anonymous listeners hold a reference to this activity, so always detach them.
        Adivery.removePlacementListener(placementId)
        super.onDestroy()
    }

    private fun grantReward() {
        Snackbar.make(binding.root, R.string.reward_granted, Snackbar.LENGTH_LONG).show()
    }

    private fun setShowEnabled(enabled: Boolean) {
        binding.show.isEnabled = enabled
    }
}

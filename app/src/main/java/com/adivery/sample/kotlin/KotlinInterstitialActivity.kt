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

/**
 * Interstitial ads in Kotlin.
 *
 * Ask for the ad with [Adivery.prepareInterstitialAd], wait for `onInterstitialAdLoaded`, then show
 * it with [Adivery.showAd]. Adivery prepares the next ad by itself once one has been shown, so
 * `prepareInterstitialAd` does not have to be called again.
 */
class KotlinInterstitialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullScreenAdBinding
    private lateinit var eventLog: AdEventLog
    private lateinit var placementId: String

    /**
     * `AdiveryListener` is an open class, not an interface, so it is implemented with an object
     * expression rather than a lambda. Adivery delivers the callbacks on the main thread.
     */
    private val listener = object : AdiveryListener() {

        override fun onInterstitialAdLoaded(placementId: String) {
            eventLog.log("onInterstitialAdLoaded")
            setShowEnabled(true)
        }

        override fun onInterstitialAdShown(placementId: String) {
            eventLog.log("onInterstitialAdShown")
            // The shown ad is consumed; Adivery starts loading the next one automatically.
            setShowEnabled(false)
        }

        override fun onInterstitialAdClicked(placementId: String) = eventLog.log("onInterstitialAdClicked")

        override fun onInterstitialAdClosed(placementId: String) = eventLog.log("onInterstitialAdClosed")

        /** Loading failures and other SDK diagnostics are reported here. */
        override fun log(placementId: String, message: String) = eventLog.log(message)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFullScreenAdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applySystemBarInsets()

        placementId = ProfileStore.get(this).activeProfile.interstitialPlacementId
        eventLog = AdEventLog(binding.log)

        binding.title.setText(R.string.sample_interstitial_kotlin)
        binding.placementId.text = getString(R.string.placement_id, placementId)
        binding.show.isEnabled = Adivery.isLoaded(placementId)

        // A placement listener only receives callbacks for this placement id. Use
        // Adivery.addGlobalListener to receive them for every placement instead.
        Adivery.addPlacementListener(placementId, listener)

        binding.load.setOnClickListener {
            eventLog.log("prepareInterstitialAd")
            Adivery.prepareInterstitialAd(this, placementId)
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

    private fun setShowEnabled(enabled: Boolean) {
        binding.show.isEnabled = enabled
    }
}

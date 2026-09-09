package com.adivery.sample.kotlin

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.adivery.sample.AdEventLog
import com.adivery.sample.AppOpenAdManager
import com.adivery.sample.ProfileStore
import com.adivery.sample.R
import com.adivery.sample.applySystemBarInsets
import com.adivery.sample.databinding.ActivityFullScreenAdBinding
import com.adivery.sdk.Adivery
import com.adivery.sdk.AdiveryListener

/**
 * App open ads in Kotlin.
 *
 * Note that [Adivery.prepareAppOpenAd] and [Adivery.showAppOpenAd] both take an `Activity` rather
 * than a `Context`, and that showing uses `showAppOpenAd` instead of the generic `showAd`.
 *
 * The switch on this screen enables [AppOpenAdManager], which is the pattern Adivery recommends:
 * show the ad when the user returns to the app after being away for a few seconds.
 */
class KotlinAppOpenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullScreenAdBinding
    private lateinit var eventLog: AdEventLog
    private lateinit var placementId: String

    private val listener = object : AdiveryListener() {

        override fun onAppOpenAdLoaded(placementId: String) {
            eventLog.log("onAppOpenAdLoaded")
            setShowEnabled(true)
        }

        override fun onAppOpenAdShown(placementId: String) {
            eventLog.log("onAppOpenAdShown")
            setShowEnabled(false)
        }

        override fun onAppOpenAdClicked(placementId: String) = eventLog.log("onAppOpenAdClicked")

        override fun onAppOpenAdClosed(placementId: String) = eventLog.log("onAppOpenAdClosed")

        override fun log(placementId: String, message: String) = eventLog.log(message)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFullScreenAdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applySystemBarInsets()

        placementId = ProfileStore.get(this).activeProfile.appOpenPlacementId
        eventLog = AdEventLog(binding.log)

        binding.title.setText(R.string.sample_app_open_kotlin)
        binding.placementId.text = getString(R.string.placement_id, placementId)
        binding.show.isEnabled = Adivery.isLoaded(placementId)

        // A placement listener only receives callbacks for this placement id. Use
        // Adivery.addGlobalListener to receive them for every placement instead.
        Adivery.addPlacementListener(placementId, listener)

        binding.load.setOnClickListener {
            eventLog.log("prepareAppOpenAd")
            Adivery.prepareAppOpenAd(this, placementId)
        }
        binding.show.setOnClickListener {
            if (Adivery.isLoaded(placementId)) {
                Adivery.showAppOpenAd(this, placementId)
            } else {
                eventLog.log(getString(R.string.ad_not_ready))
            }
        }

        binding.autoShow.visibility = View.VISIBLE
        binding.autoShow.isChecked = AppOpenAdManager.autoShowEnabled
        binding.autoShow.setOnCheckedChangeListener { _, isChecked ->
            AppOpenAdManager.autoShowEnabled = isChecked
            eventLog.log(getString(if (isChecked) R.string.auto_show_on else R.string.auto_show_off))
            // Nothing is shown on return unless an ad has been prepared first.
            if (isChecked && !Adivery.isLoaded(placementId)) {
                Adivery.prepareAppOpenAd(this, placementId)
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

package com.adivery.sample.kotlinsamples

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.adivery.sample.AdEventLog
import com.adivery.sample.ProfileStore
import com.adivery.sample.R
import com.adivery.sample.applySystemBarInsets
import com.adivery.sample.databinding.ActivityNativeAdBinding
import com.adivery.sdk.AdiveryAdListener

/**
 * Native ads in Kotlin.
 *
 * `AdiveryNativeAdView` inflates the template given by `app:adivery_native_ad_layout` and fills the
 * views it finds by id: `adivery_wrapper`, `adivery_headline`, `adivery_description`,
 * `adivery_advertiser`, `adivery_call_to_action`, `adivery_image` and `adivery_icon`. Only the
 * headline and the call to action are mandatory. See `res/layout/view_native_ad.xml`.
 */
class KotlinNativeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNativeAdBinding
    private lateinit var eventLog: AdEventLog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNativeAdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applySystemBarInsets()

        val placementId = ProfileStore.get(this).activeProfile.nativePlacementId
        eventLog = AdEventLog(binding.log)

        binding.title.setText(R.string.sample_native_kotlin)
        binding.placementId.text = getString(R.string.placement_id, placementId)

        binding.nativeAdView.apply {
            setPlacementId(placementId)
            // This listener belongs to the ad view, not to a global registry, so it is released
            // with the view and needs no detaching in onDestroy.
            setListener(object : AdiveryAdListener() {
                override fun onAdLoaded() = eventLog.log("onAdLoaded")
                override fun onAdShown() = eventLog.log("onAdShown")
                override fun onAdClicked() = eventLog.log("onAdClicked")
                override fun onError(reason: String) = eventLog.log("onError: $reason")
            })
        }

        binding.load.setOnClickListener {
            eventLog.log("loadAd")
            binding.nativeAdView.loadAd()
        }

        // Load once the view has been measured, so the ad is rendered into a laid out template.
        binding.nativeAdView.post { binding.nativeAdView.loadAd() }
    }
}

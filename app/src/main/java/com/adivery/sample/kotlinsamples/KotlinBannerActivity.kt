package com.adivery.sample.kotlinsamples

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.adivery.sample.AdEventLog
import com.adivery.sample.ProfileStore
import com.adivery.sample.R
import com.adivery.sample.applySystemBarInsets
import com.adivery.sample.databinding.ActivityBannerAdBinding
import com.adivery.sdk.AdiveryAdListener
import com.adivery.sdk.AdiveryBannerAdView
import com.adivery.sdk.BannerSize

/**
 * Banner ads in Kotlin.
 *
 * A banner can be declared straight in XML:
 *
 * ```xml
 * <com.adivery.sdk.AdiveryBannerAdView
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     app:placement_id="YOUR_PLACEMENT_ID"
 *     app:banner_size="banner" />
 * ```
 *
 * This screen builds it in code instead, so the four sizes can be swapped at runtime. A fresh view
 * is created per size because [AdiveryBannerAdView] reserves space for the size it was loaded with.
 */
class KotlinBannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBannerAdBinding
    private lateinit var eventLog: AdEventLog
    private lateinit var placementId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBannerAdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applySystemBarInsets()

        placementId = ProfileStore.get(this).activeProfile.bannerPlacementId
        eventLog = AdEventLog(binding.log)

        binding.title.setText(R.string.sample_banner_kotlin)
        binding.placementId.text = getString(R.string.placement_id, placementId)

        binding.sizeBanner.setOnClickListener { showBanner(BannerSize.BANNER, "BANNER") }
        binding.sizeLargeBanner.setOnClickListener { showBanner(BannerSize.LARGE_BANNER, "LARGE_BANNER") }
        binding.sizeMediumRectangle.setOnClickListener { showBanner(BannerSize.MEDIUM_RECTANGLE, "MEDIUM_RECTANGLE") }
        binding.sizeSmartBanner.setOnClickListener { showBanner(BannerSize.SMART_BANNER, "SMART_BANNER") }
    }

    private fun showBanner(size: BannerSize, sizeName: String) {
        eventLog.log(getString(R.string.loading_banner, sizeName))
        binding.bannerContainer.removeAllViews()

        val bannerView = AdiveryBannerAdView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            setPlacementId(placementId)
            setBannerSize(size)
            // Loaded banners are displayed by the view itself; the listener is only for reacting.
            // It belongs to the ad view, not to a global registry, so it is released with the
            // view and needs no detaching in onDestroy.
            setBannerAdListener(object : AdiveryAdListener() {
                override fun onAdLoaded() = eventLog.log("onAdLoaded")
                override fun onAdShown() = eventLog.log("onAdShown")
                override fun onAdClicked() = eventLog.log("onAdClicked")
                override fun onError(reason: String) = eventLog.log("onError: $reason")
            })
        }

        binding.bannerContainer.addView(bannerView)
        bannerView.loadAd()
    }
}

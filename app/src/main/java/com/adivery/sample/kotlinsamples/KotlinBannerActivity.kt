package com.adivery.sample.kotlinsamples

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.adivery.sample.AdEventLog
import com.adivery.sample.ProfileStore
import com.adivery.sample.R
import com.adivery.sample.applySystemBarInsets
import com.adivery.sample.databinding.ActivityBannerAdBinding
import com.adivery.sdk.AdiveryAdListener
import com.adivery.sdk.AdiveryBannerAdView
import com.adivery.sdk.BannerSize
import com.google.android.material.tabs.TabLayout

/**
 * Banner ads in Kotlin.
 * Banners can be created in two ways, both in code or directly in XML.
 */
class KotlinBannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBannerAdBinding
    private lateinit var eventLog: AdEventLog
    private lateinit var placementId: String

    private lateinit var xmlBanners: List<AdiveryBannerAdView>

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

        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.codeSection.root.isVisible = tab.position == TAB_CODE
                binding.xmlSection.root.isVisible = tab.position == TAB_XML
            }

            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })

        val code = binding.codeSection
        code.sizeBanner.setOnClickListener { showBanner(BannerSize.BANNER, "BANNER") }
        code.sizeLargeBanner.setOnClickListener { showBanner(BannerSize.LARGE_BANNER, "LARGE_BANNER") }
        code.sizeMediumRectangle.setOnClickListener { showBanner(BannerSize.MEDIUM_RECTANGLE, "MEDIUM_RECTANGLE") }
        code.sizeSmartBanner.setOnClickListener { showBanner(BannerSize.SMART_BANNER, "SMART_BANNER") }

        val xml = binding.xmlSection
        xmlBanners = listOf(xml.xmlBanner, xml.xmlLargeBanner, xml.xmlMediumRectangle, xml.xmlSmartBanner)
        xmlBanners.forEach { banner ->
            banner.setPlacementId(placementId)
            banner.setBannerAdListener(listener("xml"))
        }

        xml.xmlSizeBanner.setOnClickListener { showXmlBanner(xml.xmlBanner, "BANNER") }
        xml.xmlSizeLargeBanner.setOnClickListener { showXmlBanner(xml.xmlLargeBanner, "LARGE_BANNER") }
        xml.xmlSizeMediumRectangle.setOnClickListener { showXmlBanner(xml.xmlMediumRectangle, "MEDIUM_RECTANGLE") }
        xml.xmlSizeSmartBanner.setOnClickListener { showXmlBanner(xml.xmlSmartBanner, "SMART_BANNER") }
    }

    private fun showBanner(size: BannerSize, sizeName: String) {
        eventLog.log(getString(R.string.loading_banner, sizeName))
        binding.codeSection.bannerContainer.removeAllViews()

        val bannerView = AdiveryBannerAdView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            setPlacementId(placementId)
            setBannerSize(size)
            setBannerAdListener(listener("code"))
        }

        binding.codeSection.bannerContainer.addView(bannerView)
        bannerView.loadAd()
    }

    private fun showXmlBanner(banner: AdiveryBannerAdView, sizeName: String) {
        eventLog.log(getString(R.string.loading_banner, sizeName))
        xmlBanners.forEach { it.isVisible = it === banner }
        banner.loadAd()
    }

    private fun listener(source: String) = object : AdiveryAdListener() {
        override fun onAdLoaded() = eventLog.log("$source: onAdLoaded")
        override fun onAdShown() = eventLog.log("$source: onAdShown")
        override fun onAdClicked() = eventLog.log("$source: onAdClicked")
        override fun onError(reason: String) = eventLog.log("$source: onError: $reason")
    }

    private companion object {
        const val TAB_CODE = 0
        const val TAB_XML = 1
    }
}

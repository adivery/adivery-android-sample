package com.adivery.sample

import android.app.ActionBar
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.adivery.sdk.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Adivery.setLoggingEnabled(true)
        Adivery.configure(application, "59c36ce3-7125-40a7-bd34-144e6906c796")

        findViewById<Button>(R.id.btn_banner).setOnClickListener {
            Adivery.requestBannerAd(this, "a355be22-970a-46b8-bc52-f0a59c4ded05",
                BannerType.BANNER, object : AdiveryBannerCallback() {
                    override fun onAdLoaded(adView: View) {
                        adView.apply {
                            displayBannerAd(this)
                        }
                    }
            })
        }

        findViewById<Button>(R.id.btn_large_banner).setOnClickListener {
            Adivery.requestBannerAd(this, "90717d2a-dad1-4ac1-8e71-c4d741a225bb",
                BannerType.LARGE_BANNER, object : AdiveryBannerCallback() {
                    override fun onAdLoaded(adView: View) {
                        adView.apply {
                            displayBannerAd(adView)
                        }
                    }
                })
        }
        findViewById<Button>(R.id.btn_medium_rectangle).setOnClickListener {
            Adivery.requestBannerAd(this, "850d771f-dd47-4b2e-832c-ad1ad70d9266",
                BannerType.MEDIUM_RECTANGLE, object : AdiveryBannerCallback() {
                    override fun onAdLoaded(adView: View) {
                        adView.apply {
                            displayBannerAd(adView)
                        }
                    }
                })
        }

        findViewById<Button>(R.id.btn_interstitial).setOnClickListener {
            Adivery.requestInterstitialAd(this, "38b301f2-5e0c-4776-b671-c6b04a612311",
                object : AdiveryInterstitialCallback() {
                    override fun onAdLoaded(ad: AdiveryLoadedAd) {
                        ad.apply {
                            show()
                        }
                    }
                })
        }
        findViewById<Button>(R.id.btn_rewarded).setOnClickListener {
            Adivery.requestRewardedAd(this, "16414bae-368e-4904-b259-c5b89362206d",
                object : AdiveryRewardedCallback() {
                    override fun onAdLoaded(ad: AdiveryLoadedAd) {
                        ad.apply {
                            show()
                        }
                    }

                    override fun onAdRewarded() {
                        Toast.makeText(
                            this@MainActivity,"Ad Rewarded", Toast.LENGTH_LONG
                        ).show()
                        Log.i("MainActivity", "Rewarded called")
                    }
                })
        }

        findViewById<Button>(R.id.btn_native).setOnClickListener {
            Adivery.requestNativeAd(this, "ff454979-efaa-4ab8-b084-7db19e995d9b",
                object : AdiveryNativeCallback() {
                    override fun onAdLoaded(ad: AdiveryNativeAd) {
                        ad.apply {
                            displayNativeAd(this)
                        }
                    }
                })
        }
    }

    private fun displayNativeAd(adiveryNativeAd: AdiveryNativeAd) {
        findViewById<AdiveryNativeAdView>(R.id.native_ad_layout).apply {
            visibility = View.VISIBLE
            disableBanner()
            setNativeAd(adiveryNativeAd)
        }
    }

    private fun disableBanner() {
        findViewById<LinearLayout>(R.id.banner_ad_container).apply {
            visibility = View.GONE
        }
    }

    fun displayBannerAd(adView: View){
        findViewById<LinearLayout>(R.id.banner_ad_container).apply {
            disableNative()
            removeAllViews()
            visibility = View.VISIBLE
            adView.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
            addView(adView)
        }
    }

    private fun disableNative() {
        findViewById<AdiveryNativeAdView>(R.id.native_ad_layout).apply {
            visibility = View.GONE
        }
    }
}
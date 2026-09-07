package com.adivery.sample

import android.app.Activity
import androidx.annotation.StringRes
import com.adivery.sample.javasamples.JavaAppOpenActivity
import com.adivery.sample.javasamples.JavaBannerActivity
import com.adivery.sample.javasamples.JavaInterstitialActivity
import com.adivery.sample.javasamples.JavaNativeActivity
import com.adivery.sample.javasamples.JavaRewardedActivity
import com.adivery.sample.javasamples.JavaVastActivity
import com.adivery.sample.kotlinsamples.KotlinAppOpenActivity
import com.adivery.sample.kotlinsamples.KotlinBannerActivity
import com.adivery.sample.kotlinsamples.KotlinInterstitialActivity
import com.adivery.sample.kotlinsamples.KotlinNativeActivity
import com.adivery.sample.kotlinsamples.KotlinRewardedActivity
import com.adivery.sample.kotlinsamples.KotlinVastActivity

/** The language a sample is written in. Both implementations behave identically. */
enum class SampleLanguage { KOTLIN, JAVA }

/**
 * Every placement type Adivery supports, with the Kotlin and the Java screen implementing it.
 */
enum class AdSample(
    @get:StringRes val titleRes: Int,
    private val kotlinActivity: Class<out Activity>,
    private val javaActivity: Class<out Activity>,
) {
    INTERSTITIAL(
        R.string.sample_interstitial,
        KotlinInterstitialActivity::class.java,
        JavaInterstitialActivity::class.java,
    ),
    REWARDED(
        R.string.sample_rewarded,
        KotlinRewardedActivity::class.java,
        JavaRewardedActivity::class.java,
    ),
    APP_OPEN(
        R.string.sample_app_open,
        KotlinAppOpenActivity::class.java,
        JavaAppOpenActivity::class.java,
    ),
    BANNER(
        R.string.sample_banner,
        KotlinBannerActivity::class.java,
        JavaBannerActivity::class.java,
    ),
    NATIVE(
        R.string.sample_native,
        KotlinNativeActivity::class.java,
        JavaNativeActivity::class.java,
    ),
    VAST(
        R.string.sample_vast,
        KotlinVastActivity::class.java,
        JavaVastActivity::class.java,
    );

    fun activityFor(language: SampleLanguage): Class<out Activity> = when (language) {
        SampleLanguage.KOTLIN -> kotlinActivity
        SampleLanguage.JAVA -> javaActivity
    }
}

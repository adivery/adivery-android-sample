package com.adivery.sample

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.adivery.sdk.Adivery
import java.util.concurrent.TimeUnit

/**
 * The app open pattern Adivery recommends: whenever the user comes back to the app after being away
 * for more than [AWAY_THRESHOLD_MILLIS], show the app open ad on the activity being resumed.
 *
 * Registered once from [SampleApplication]. In this sample it is disabled by default and can be
 * switched on from the app open screens so it does not interrupt the other demos.
 */
class AppOpenAdManager(private val placementId: String) : Application.ActivityLifecycleCallbacks {

    private var lastPauseTime = 0L

    override fun onActivityResumed(activity: Activity) {
        if (!autoShowEnabled) return
        if (System.currentTimeMillis() - lastPauseTime > AWAY_THRESHOLD_MILLIS) {
            Adivery.showAppOpenAd(activity, placementId)
        }
    }

    override fun onActivityPaused(activity: Activity) {
        lastPauseTime = System.currentTimeMillis()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
    override fun onActivityDestroyed(activity: Activity) = Unit

    companion object {
        private val AWAY_THRESHOLD_MILLIS = TimeUnit.SECONDS.toMillis(5)

        /** Toggled from the app open samples; also readable from Java as a static field. */
        @JvmStatic
        var autoShowEnabled = false
    }
}

package com.adivery.sample

import android.app.Application
import com.adivery.sdk.Adivery

/**
 * Configures the Adivery SDK once for the whole process.
 */
class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val profile = ProfileStore.get(this).activeProfile

        // Prints the SDK's diagnostics to Logcat. Keep it off in production builds.
        Adivery.setLoggingEnabled(BuildConfig.DEBUG)
        Adivery.configure(this, profile.appId)

        // One listener for every placement in the app, as opposed to the per-screen listeners the
        // samples register with Adivery.addPlacementListener. See GlobalAdListener.
        GlobalAdListener.register()

        registerActivityLifecycleCallbacks(AppOpenAdManager(profile.appOpenPlacementId))
    }
}

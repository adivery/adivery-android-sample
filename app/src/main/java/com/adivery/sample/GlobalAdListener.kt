package com.adivery.sample

import android.util.Log
import com.adivery.sdk.Adivery
import com.adivery.sdk.AdiveryListener

/**
 * A single listener that receives the full screen callbacks of *every* placement, registered once in
 * [SampleApplication] with `Adivery.addGlobalListener`.
 *
 * This is the counterpart to the per-screen listeners the samples register with
 * `Adivery.addPlacementListener`: a global listener always reports which placement fired, so it
 * suits app wide work such as analytics, crediting a reward, or logging. Both kinds fire for the
 * same ad, so the placement screens and this listener report the same events side by side.
 *
 * It stays registered for the life of the process, which is why nothing calls
 * `Adivery.removeGlobalListener` here. Register from an `Activity` instead and you must remove it
 * again, exactly as the placement screens do in `onDestroy`, or the listener leaks the activity.
 */
class GlobalAdListener : AdiveryListener() {

    override fun onInterstitialAdLoaded(placementId: String) = report(placementId, "interstitial loaded")
    override fun onInterstitialAdShown(placementId: String) = report(placementId, "interstitial shown")
    override fun onInterstitialAdClicked(placementId: String) = report(placementId, "interstitial clicked")
    override fun onInterstitialAdClosed(placementId: String) = report(placementId, "interstitial closed")

    override fun onRewardedAdLoaded(placementId: String) = report(placementId, "rewarded loaded")
    override fun onRewardedAdShown(placementId: String) = report(placementId, "rewarded shown")
    override fun onRewardedAdClicked(placementId: String) = report(placementId, "rewarded clicked")

    override fun onRewardedAdClosed(placementId: String, isRewarded: Boolean) {
        report(placementId, "rewarded closed, isRewarded = $isRewarded")
        if (isRewarded) {
            // An app wide place to credit the reward, so it survives the user leaving the screen
            // that showed the ad.
            report(placementId, "reward earned")
        }
    }

    override fun onAppOpenAdLoaded(placementId: String) = report(placementId, "app open loaded")
    override fun onAppOpenAdShown(placementId: String) = report(placementId, "app open shown")
    override fun onAppOpenAdClicked(placementId: String) = report(placementId, "app open clicked")
    override fun onAppOpenAdClosed(placementId: String) = report(placementId, "app open closed")

    /** Loading failures and other SDK diagnostics arrive here rather than on a dedicated callback. */
    override fun log(placementId: String, message: String) = report(placementId, "log: $message")

    private fun report(placementId: String, event: String) {
        Log.d(TAG, "[$placementId] $event")
    }

    companion object {
        private const val TAG = "AdiveryGlobal"

        @JvmStatic
        fun register() {
            Adivery.addGlobalListener(GlobalAdListener())
        }
    }
}

package com.adivery.sample.compose

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.doOnLayout
import com.adivery.sdk.AdiveryAdListener
import com.adivery.sdk.AdiveryBannerAdView
import com.adivery.sdk.AdiveryNativeAdView
import com.adivery.sdk.BannerSize

/**
 * The whole Compose interop layer for Adivery, in two composables.
 *
 * Adivery has no Compose artifact and does not need one: banner and native placements are ordinary
 * Views ([AdiveryBannerAdView], [AdiveryNativeAdView]), so Compose hosts them with [AndroidView].
 * Full screen placements — interstitial, rewarded, app open — are plain static calls on `Adivery`
 * and need no wrapper at all; only their listener has to follow the composition, which
 * [ComposeShowcaseActivity] shows with a `DisposableEffect`.
 *
 * Copy this file into your app as is.
 */

/**
 * A banner placement as a composable.
 *
 * @param onAdEvent called for every SDK callback, on the main thread.
 */
@Composable
fun AdiveryBanner(
    placementId: String,
    size: BannerSize,
    modifier: Modifier = Modifier,
    onAdEvent: (String) -> Unit = {},
) {
    // A banner view takes its placement and size at construction; changing them on a view that has
    // already loaded does not re-request the ad. key() scopes the AndroidView node itself, so a new
    // size builds a new view rather than leaving the previous banner on screen. Without it, the
    // factory below would never run again and the size buttons would appear to do nothing.
    key(placementId, size) {
        // Keeps the callback current without rebuilding the view. Capturing onAdEvent directly
        // would pin the first lambda; putting it in the key would throw the loaded ad away every
        // time the caller recomposes with a fresh lambda.
        val currentOnAdEvent by rememberUpdatedState(onAdEvent)

        AndroidView(
            modifier = modifier,
            factory = { context ->
                AdiveryBannerAdView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                    )
                    setPlacementId(placementId)
                    setBannerSize(size)
                    setBannerAdListener(adListener { currentOnAdEvent(it) })
                    // factory runs once per node, so the ad is requested exactly once. loadAd() in
                    // the update block instead would re-request on every recomposition.
                    loadAd()
                }
            },
        )
    }
}

/**
 * A native placement as a composable.
 *
 * @param template layout whose ids `AdiveryNativeAdView` fills in, e.g. `R.layout.view_native_ad`.
 * @param onAdEvent called for every SDK callback, on the main thread.
 */
@Composable
fun AdiveryNativeAd(
    placementId: String,
    @LayoutRes template: Int,
    modifier: Modifier = Modifier,
    onAdEvent: (String) -> Unit = {},
) {
    key(placementId, template) {
        val currentOnAdEvent by rememberUpdatedState(onAdEvent)

        AndroidView(
            modifier = modifier,
            factory = { context ->
                AdiveryNativeAdView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                    )
                    setPlacementId(placementId)
                    // The View samples pick the template in XML with app:adivery_native_ad_layout;
                    // from code it is setNativeAdLayout. See res/layout/view_native_ad.xml for the
                    // ids the SDK looks for.
                    setNativeAdLayout(template)
                    setListener(adListener { currentOnAdEvent(it) })
                    // The ad is rendered into the template, so the template has to be laid out
                    // first. AndroidView's factory runs before the first layout pass, which is why
                    // the request waits for it — the Compose equivalent of the post { loadAd() }
                    // in KotlinNativeActivity. Loading straight from factory returns an ad that
                    // renders into a zero height template.
                    doOnLayout { loadAd() }
                }
            },
        )
    }
}

/** `AdiveryAdListener` is an open class rather than an interface, so it cannot be a lambda. */
private fun adListener(onAdEvent: (String) -> Unit) = object : AdiveryAdListener() {
    override fun onAdLoaded() = onAdEvent("onAdLoaded")
    override fun onAdShown() = onAdEvent("onAdShown")
    override fun onAdClicked() = onAdEvent("onAdClicked")
    override fun onError(reason: String) = onAdEvent("onError: $reason")
}

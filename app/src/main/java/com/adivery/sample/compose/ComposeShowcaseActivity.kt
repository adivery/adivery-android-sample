package com.adivery.sample.compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.adivery.sample.AdProfile
import com.adivery.sample.ProfileStore
import com.adivery.sample.R
import com.adivery.sdk.Adivery
import com.adivery.sdk.AdiveryListener
import com.adivery.sdk.BannerSize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * The Compose sample.
 *
 * It covers the three cases where Compose changes the integration and nothing else: a banner and a
 * native ad hosted in `AndroidView` (see [AdiveryCompose.kt][AdiveryBanner]), and a full screen
 * placement whose listener is registered and released by a `DisposableEffect`. Rewarded, app open
 * and Pre-Roll are left to the Kotlin and Java screens — from Compose they are the same static
 * `Adivery` calls as the interstitial below, with the same listener handling.
 */
class ComposeShowcaseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val profile = ProfileStore.get(this).activeProfile
        setContent {
            // The View samples get their palette from Theme.AdiverySample; a Compose screen brings
            // its own. Dynamic color is left off here too, so both look the same on every device.
            MaterialTheme(
                colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme(),
            ) {
                ComposeShowcaseScreen(profile)
            }
        }
    }
}

@Composable
private fun ComposeShowcaseScreen(profile: AdProfile) {
    val eventLog = rememberAdEventLog()

    Column(
        modifier = Modifier
            .fillMaxSize()
            // Compose handles the status and navigation bars with insets modifiers rather than the
            // view padding applied by SystemBars.applySystemBarInsets in the other samples.
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.sample_compose_title),
            style = MaterialTheme.typography.titleLarge,
        )

        InterstitialSection(profile.interstitialPlacementId, eventLog)
        BannerSection(profile.bannerPlacementId, eventLog)
        NativeSection(profile.nativePlacementId, eventLog)
        EventLogCard(eventLog)
    }
}

/**
 * The only Compose specific part of a full screen placement: the listener has to be attached for as
 * long as the composable is in the composition, which is what `DisposableEffect` expresses. In the
 * View samples the same pair of calls sits in `onCreate` and `onDestroy`.
 */
@Composable
private fun InterstitialSection(placementId: String, eventLog: AdEventLogState) {
    val context = LocalContext.current
    var loaded by remember(placementId) { mutableStateOf(Adivery.isLoaded(placementId)) }
    val currentEventLog by rememberUpdatedState(eventLog)
    // Resolved during composition: a click listener runs outside one and cannot read resources.
    val adNotReady = stringResource(R.string.ad_not_ready)

    DisposableEffect(placementId) {
        val listener = object : AdiveryListener() {

            override fun onInterstitialAdLoaded(placementId: String) {
                currentEventLog.log("onInterstitialAdLoaded")
                loaded = true
            }

            override fun onInterstitialAdShown(placementId: String) {
                currentEventLog.log("onInterstitialAdShown")
                // The shown ad is consumed; Adivery starts loading the next one automatically.
                loaded = false
            }

            override fun onInterstitialAdClicked(placementId: String) =
                currentEventLog.log("onInterstitialAdClicked")

            override fun onInterstitialAdClosed(placementId: String) =
                currentEventLog.log("onInterstitialAdClosed")

            /** Loading failures and other SDK diagnostics are reported here. */
            override fun log(placementId: String, message: String) = currentEventLog.log(message)
        }

        Adivery.addPlacementListener(placementId, listener)
        onDispose {
            // removePlacementListener takes only the placement id, so it drops whatever listener
            // that placement currently has rather than specifically the one added above. With one
            // listener per placement, as here, that is the same thing.
            Adivery.removePlacementListener(placementId)
        }
    }

    SectionCard(stringResource(R.string.section_compose_interstitial), placementId) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    eventLog.log("prepareInterstitialAd")
                    Adivery.prepareInterstitialAd(context, placementId)
                },
            ) {
                Text(stringResource(R.string.request_ad))
            }
            Button(
                enabled = loaded,
                onClick = {
                    if (Adivery.isLoaded(placementId)) {
                        Adivery.showAd(placementId)
                    } else {
                        eventLog.log(adNotReady)
                    }
                },
            ) {
                Text(stringResource(R.string.show_ad))
            }
        }
    }
}

@Composable
private fun BannerSection(placementId: String, eventLog: AdEventLogState) {
    var size by remember { mutableStateOf(BannerSize.BANNER) }

    SectionCard(stringResource(R.string.section_compose_banner), placementId) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BannerSizeButton(BannerSize.BANNER, "320×50", size) { size = it }
            BannerSizeButton(BannerSize.LARGE_BANNER, "320×100", size) { size = it }
            BannerSizeButton(BannerSize.MEDIUM_RECTANGLE, "300×250", size) { size = it }
        }
        AdiveryBanner(
            placementId = placementId,
            size = size,
            modifier = Modifier.fillMaxWidth(),
            onAdEvent = { eventLog.log("banner: $it") },
        )
    }
}

@Composable
private fun BannerSizeButton(
    size: BannerSize,
    label: String,
    selected: BannerSize,
    onClick: (BannerSize) -> Unit,
) {
    if (size == selected) {
        Button(onClick = { onClick(size) }) { Text(label) }
    } else {
        OutlinedButton(onClick = { onClick(size) }) { Text(label) }
    }
}

@Composable
private fun NativeSection(placementId: String, eventLog: AdEventLogState) {
    SectionCard(stringResource(R.string.section_compose_native), placementId) {
        AdiveryNativeAd(
            placementId = placementId,
            // The same template the Kotlin and Java native screens use.
            template = R.layout.view_native_ad,
            modifier = Modifier.fillMaxWidth(),
            onAdEvent = { eventLog.log("native: $it") },
        )
    }
}

@Composable
private fun SectionCard(
    title: String,
    placementId: String,
    content: @Composable () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(R.string.placement_id, placementId),
                style = MaterialTheme.typography.bodySmall,
            )
            content()
        }
    }
}

@Composable
private fun EventLogCard(eventLog: AdEventLogState) {
    val scrollState = rememberScrollState()
    LaunchedEffect(eventLog.lines.size) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.section_callbacks),
                style = MaterialTheme.typography.titleMedium,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .verticalScroll(scrollState),
            ) {
                eventLog.lines.forEach { line ->
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                    )
                }
            }
        }
    }
}

/**
 * The Compose counterpart of `AdEventLog`, which writes into a `TextView` and so cannot be reused
 * here. Adivery delivers every callback on the main thread, so the list can be written directly.
 */
@Stable
private class AdEventLogState {

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)

    val lines = mutableStateListOf<String>()

    fun log(message: String) {
        Log.d("AdiverySample", message)
        lines += "${timeFormat.format(Date())}  $message"
    }
}

@Composable
private fun rememberAdEventLog(): AdEventLogState = remember { AdEventLogState() }

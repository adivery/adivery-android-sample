package com.adivery.sample.kotlin

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.ima.ImaAdsLoader
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.adivery.sample.AdEventLog
import com.adivery.sample.ProfileStore
import com.adivery.sample.R
import com.adivery.sample.applySystemBarInsets
import com.adivery.sample.databinding.ActivityVastAdBinding
import com.adivery.sdk.Adivery
import com.google.ads.interactivemedia.v3.api.AdEvent
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory

/**
 * Pre-Roll (VAST) ads in Kotlin.
 *
 * Adivery does not render this placement type itself: it hands out a VAST url and a video player
 * plays the ad in front of the content. Here that player is ExoPlayer with its IMA extension.
 */
class KotlinVastActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVastAdBinding
    private lateinit var eventLog: AdEventLog
    private lateinit var placementId: String

    private val handler = Handler(Looper.getMainLooper())

    private var player: ExoPlayer? = null
    private var adsLoader: ImaAdsLoader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVastAdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applySystemBarInsets()

        placementId = ProfileStore.get(this).activeProfile.vastPlacementId
        eventLog = AdEventLog(binding.log)

        binding.title.setText(R.string.sample_vast_kotlin)
        binding.placementId.text = getString(R.string.placement_id, placementId)

        binding.play.setOnClickListener {
            eventLog.clear()
            awaitVastUrl(attempt = 1)
        }
    }

    /**
     * There is no load callback for this placement type: the url becomes available as a side effect
     * of the SDK warming up, so [Adivery.getVastUrl] is polled until it answers with one. An empty
     * or null answer only means "not ready yet".
     */
    private fun awaitVastUrl(attempt: Int) {
        val vastUrl = Adivery.getVastUrl(placementId)
        if (!vastUrl.isNullOrEmpty()) {
            eventLog.log(getString(R.string.vast_url_ready, vastUrl))
            startPlayback(vastUrl)
            return
        }

        if (attempt > MAX_ATTEMPTS) {
            eventLog.log(getString(R.string.vast_url_unavailable))
            startPlayback(vastUrl = null)
            return
        }

        eventLog.log(getString(R.string.waiting_for_vast_url, attempt, MAX_ATTEMPTS))
        handler.postDelayed({ awaitVastUrl(attempt + 1) }, RETRY_DELAY_MS)
    }

    /** Plays [CONTENT_URL], preceded by the ad in [vastUrl] when there is one. */
    @OptIn(UnstableApi::class)
    private fun startPlayback(vastUrl: String?) {
        releasePlayer()

        val playerBuilder = ExoPlayer.Builder(this)
        val mediaItemBuilder = MediaItem.Builder().setUri(CONTENT_URL)
        if (vastUrl != null) {
            val adsLoader = buildAdsLoader()
            this.adsLoader = adsLoader

            mediaItemBuilder.setAdsConfiguration(
                MediaItem.AdsConfiguration.Builder(vastUrl.toUri()).build()
            )

            playerBuilder.setMediaSourceFactory(
                DefaultMediaSourceFactory(this)
                    .setLocalAdInsertionComponents({ adsLoader }, binding.playerView)
            )
        }

        val player = playerBuilder.build()
        this.player = player

        binding.playerView.player = player
        adsLoader?.setPlayer(player)

        player.setMediaItem(mediaItemBuilder.build())
        player.prepare()
        player.play()
    }

    @OptIn(UnstableApi::class)
    private fun buildAdsLoader() = ImaAdsLoader.Builder(this)
        .setCompanionAdSlots(listOf(companionAdSlot()))
        .setAdEventListener { adEvent ->
            eventLog.log("onAdEvent: ${adEvent.type}")
            when (adEvent.type) {
                AdEvent.AdEventType.LOADED -> binding.companionAdSlot.isVisible = true

                AdEvent.AdEventType.SKIPPED,
                AdEvent.AdEventType.COMPLETED,
                AdEvent.AdEventType.ALL_ADS_COMPLETED,
                    -> binding.companionAdSlot.isVisible = false

                else -> Unit
            }
        }
        .build()

    /** The 320×50 slot IMA draws the banner accompanying the video into. */
    @OptIn(UnstableApi::class)
    private fun companionAdSlot() = ImaSdkFactory.getInstance().createCompanionAdSlot().apply {
        container = binding.companionAdSlot
        setSize(COMPANION_WIDTH, COMPANION_HEIGHT)
    }

    /** Otherwise the video, and any ad playing over it, keeps running after the user leaves. */
    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onDestroy() {
        // The retry runnable holds this activity, so it has to go with the player.
        handler.removeCallbacksAndMessages(null)
        releasePlayer()
        super.onDestroy()
    }

    /** Both the player and the ads loader hold native resources and leak without this. */
    @OptIn(UnstableApi::class)
    private fun releasePlayer() {
        player?.let {
            adsLoader?.setPlayer(null)
            binding.playerView.player = null
            it.release()
            player = null
        }

        adsLoader?.release()
        adsLoader = null

        binding.companionAdSlot.isVisible = false
    }

    private companion object {
        const val CONTENT_URL =
            "https://cdn.adivery.com/media/native/c2c76c3b-24ad-4cd3-a31c-009264681765-converted/360p.mp4"

        const val RETRY_DELAY_MS = 1_000L
        const val MAX_ATTEMPTS = 10

        const val COMPANION_WIDTH = 320
        const val COMPANION_HEIGHT = 50
    }
}

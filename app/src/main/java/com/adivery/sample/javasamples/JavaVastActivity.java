package com.adivery.sample.javasamples;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.ima.ImaAdsLoader;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;

import com.adivery.sample.AdEventLog;
import com.adivery.sample.ProfileStore;
import com.adivery.sample.R;
import com.adivery.sample.SystemBars;
import com.adivery.sample.databinding.ActivityVastAdBinding;
import com.adivery.sdk.Adivery;
import com.google.ads.interactivemedia.v3.api.CompanionAdSlot;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;

import java.util.Collections;
import java.util.List;

/**
 * Pre-Roll (VAST) ads in Java.
 *
 * <p>Adivery does not render this placement type itself: it hands out a VAST url and a video player
 * plays the ad in front of the content. Here that player is ExoPlayer with its IMA extension.
 */
public class JavaVastActivity extends AppCompatActivity {

    /** Stands in for the video the app would normally be playing. */
    private static final String CONTENT_URL =
            "https://cdn.adivery.com/media/native/c2c76c3b-24ad-4cd3-a31c-009264681765-converted/360p.mp4";

    private static final long RETRY_DELAY_MS = 1_000L;
    private static final int MAX_ATTEMPTS = 10;

    private static final int COMPANION_WIDTH = 320;
    private static final int COMPANION_HEIGHT = 50;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private ActivityVastAdBinding binding;
    private AdEventLog eventLog;
    private String placementId;

    private ExoPlayer player;
    private ImaAdsLoader adsLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityVastAdBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SystemBars.applySystemBarInsets(binding.getRoot());

        placementId = ProfileStore.get(this).getActiveProfile().getVastPlacementId();
        eventLog = new AdEventLog(binding.log);

        binding.title.setText(R.string.sample_vast_java);
        binding.placementId.setText(getString(R.string.placement_id, placementId));

        binding.play.setOnClickListener(view -> {
            eventLog.clear();
            awaitVastUrl(1);
        });
    }

    /**
     * There is no load callback for this placement type: the url becomes available as a side effect
     * of the SDK warming up, so {@link Adivery#getVastUrl(String)} is polled until it answers with
     * one. An empty or null answer only means "not ready yet".
     */
    private void awaitVastUrl(int attempt) {
        String vastUrl = Adivery.getVastUrl(placementId);
        if (!TextUtils.isEmpty(vastUrl)) {
            eventLog.log(getString(R.string.vast_url_ready, vastUrl));
            startPlayback(vastUrl);
            return;
        }

        if (attempt > MAX_ATTEMPTS) {
            // Playing the content without a pre-roll beats making the user wait for an ad that is
            // not coming.
            eventLog.log(getString(R.string.vast_url_unavailable));
            startPlayback(null);
            return;
        }

        eventLog.log(getString(R.string.waiting_for_vast_url, attempt, MAX_ATTEMPTS));
        handler.postDelayed(() -> awaitVastUrl(attempt + 1), RETRY_DELAY_MS);
    }

    /** Plays {@link #CONTENT_URL}, preceded by the ad in {@code vastUrl} when there is one. */
    @OptIn(markerClass = UnstableApi.class)
    private void startPlayback(@Nullable String vastUrl) {
        // Released first so pressing play twice restarts from a clean player rather than resuming a
        // stream whose ad has already been played.
        releasePlayer();

        ExoPlayer.Builder playerBuilder = new ExoPlayer.Builder(this);
        MediaItem.Builder mediaItemBuilder = new MediaItem.Builder().setUri(CONTENT_URL);

        // Without a url there is no ad to insert, so IMA is left out of the graph entirely rather
        // than attached with nothing to serve.
        if (vastUrl != null) {
            adsLoader = buildAdsLoader();

            mediaItemBuilder.setAdsConfiguration(
                    new MediaItem.AdsConfiguration.Builder(Uri.parse(vastUrl)).build());

            // Replaces the deprecated setAdsLoaderProvider/setAdViewProvider pair: the factory needs
            // the loader that inserts the ad and the view it is rendered over.
            playerBuilder.setMediaSourceFactory(new DefaultMediaSourceFactory(this)
                    .setLocalAdInsertionComponents(unusedAdTagUri -> adsLoader, binding.playerView));
        }

        player = playerBuilder.build();

        binding.playerView.setPlayer(player);
        if (adsLoader != null) {
            adsLoader.setPlayer(player);
        }

        player.setMediaItem(mediaItemBuilder.build());
        player.prepare();
        player.play();
    }

    @OptIn(markerClass = UnstableApi.class)
    private ImaAdsLoader buildAdsLoader() {
        return new ImaAdsLoader.Builder(this)
                .setCompanionAdSlots(companionAdSlots())
                .setAdEventListener(adEvent -> {
                    eventLog.log("onAdEvent: " + adEvent.getType());
                    switch (adEvent.getType()) {
                        case LOADED:
                            binding.companionAdSlot.setVisibility(View.VISIBLE);
                            break;
                        case SKIPPED:
                        case COMPLETED:
                        case ALL_ADS_COMPLETED:
                            binding.companionAdSlot.setVisibility(View.GONE);
                            break;
                        default:
                            break;
                    }
                })
                .build();
    }

    /** The 320×50 slot IMA draws the banner accompanying the video into. */
    @NonNull
    private List<CompanionAdSlot> companionAdSlots() {
        CompanionAdSlot companionAdSlot = ImaSdkFactory.getInstance().createCompanionAdSlot();
        companionAdSlot.setContainer(binding.companionAdSlot);
        companionAdSlot.setSize(COMPANION_WIDTH, COMPANION_HEIGHT);
        return Collections.singletonList(companionAdSlot);
    }

    /** Otherwise the video, and any ad playing over it, keeps running after the user leaves. */
    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected void onDestroy() {
        // The retry runnable holds this activity, so it has to go with the player.
        handler.removeCallbacksAndMessages(null);
        releasePlayer();
        super.onDestroy();
    }

    /** Both the player and the ads loader hold native resources and leak without this. */
    @OptIn(markerClass = UnstableApi.class)
    private void releasePlayer() {
        if (player != null) {
            if (adsLoader != null) {
                adsLoader.setPlayer(null);
            }
            binding.playerView.setPlayer(null);
            player.release();
            player = null;
        }

        if (adsLoader != null) {
            adsLoader.release();
            adsLoader = null;
        }

        binding.companionAdSlot.setVisibility(View.GONE);
    }
}

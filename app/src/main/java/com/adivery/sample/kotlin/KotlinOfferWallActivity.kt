package com.adivery.sample.kotlin

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.adivery.sample.AdEventLog
import com.adivery.sample.ProfileStore
import com.adivery.sample.R
import com.adivery.sample.Wallet
import com.adivery.sample.applySystemBarInsets
import com.adivery.sample.databinding.ActivityOfferWallBinding
import com.adivery.sdk.Adivery
import com.adivery.sdk.AdiveryListener

/**
 * Offer wall in Kotlin.
 *
 * Unlike the other full screen formats, the offer wall is never loaded ahead of time. Its missions,
 * step states and deadlines are fetched when it loads, so a wall loaded early would show stale ones.
 * Call [Adivery.prepareOfferWallAd] only when the user asks for the wall, and show it as soon as
 * `onOfferWallAdLoaded` arrives. Adivery does not reload a closed wall either, so every tap on the
 * button loads it again.
 *
 * Loading can take a few seconds. [waitingForOfferWall] records whether the user is still waiting,
 * and the wall is only shown if they are, so it never opens when they no longer expect it.
 */
class KotlinOfferWallActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOfferWallBinding
    private lateinit var eventLog: AdEventLog
    private lateinit var wallet: Wallet
    private lateinit var placementId: String

    /** Set from the tap on the button until the wall is shown or the wait is given up. */
    private var waitingForOfferWall = false

    private val handler = Handler(Looper.getMainLooper())
    private val loadTimeout = Runnable {
        eventLog.log(getString(R.string.offer_wall_timed_out))
        stopWaiting()
    }

    private val listener = object : AdiveryListener() {

        override fun onOfferWallAdLoaded(placementId: String) {
            eventLog.log("onOfferWallAdLoaded")
            if (waitingForOfferWall) {
                stopWaiting()
                Adivery.showAd(placementId)
            } else {
                eventLog.log(getString(R.string.offer_wall_not_waiting))
            }
        }

        override fun onOfferWallAdShown(placementId: String) = eventLog.log("onOfferWallAdShown")

        /**
         * Fires each time the user claims the reward of a mission step, so possibly several times
         * per wall, and while the wall is still on screen.
         *
         * This screen's listener is removed in [onDestroy], so a reward claimed after the system
         * destroyed this screen behind the wall would be lost. To rule that out, credit it from a
         * global listener instead (see `GlobalAdListener`), but never from both or every step is
         * credited twice.
         */
        override fun onOfferWallMissionRewarded(
            placementId: String,
            currency: String,
            rewardAmount: Int,
        ) {
            eventLog.log("onOfferWallMissionRewarded(currency = $currency, rewardAmount = $rewardAmount)")
            wallet.add(rewardAmount)
        }

        override fun onOfferWallAdClosed(placementId: String) {
            eventLog.log("onOfferWallAdClosed")
            showBalance()
        }

        /** Loading failures are reported here. Stop waiting so the user can try again. */
        override fun log(placementId: String, message: String) {
            eventLog.log(message)
            stopWaiting()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOfferWallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applySystemBarInsets()

        placementId = ProfileStore.get(this).activeProfile.offerWallPlacementId
        eventLog = AdEventLog(binding.log)
        wallet = Wallet(this)

        binding.title.setText(R.string.sample_offer_wall_kotlin)
        binding.placementId.text = getString(R.string.placement_id, placementId)
        showBalance()

        if (placementId.isEmpty()) {
            binding.noPlacement.isVisible = true
            binding.open.isEnabled = false
            return
        }

        // A placement listener only receives callbacks for this placement id. Use
        // Adivery.addGlobalListener to receive them for every placement instead.
        Adivery.addPlacementListener(placementId, listener)

        binding.open.setOnClickListener { openOfferWall() }
    }

    override fun onStop() {
        // The user has left the screen, so they no longer expect the wall to open. Showing the wall
        // stops this screen too, but the wait has already ended by then.
        if (waitingForOfferWall) {
            eventLog.log(getString(R.string.offer_wall_left_screen))
            stopWaiting()
        }
        super.onStop()
    }

    override fun onDestroy() {
        // Anonymous listeners hold a reference to this activity, so always detach them.
        Adivery.removePlacementListener(placementId)
        super.onDestroy()
    }

    private fun openOfferWall() {
        if (waitingForOfferWall) return
        waitingForOfferWall = true
        setLoading(true)
        handler.postDelayed(loadTimeout, LOAD_TIMEOUT_MS)

        eventLog.log("prepareOfferWallAd")
        Adivery.prepareOfferWallAd(this, placementId)
    }

    private fun stopWaiting() {
        waitingForOfferWall = false
        handler.removeCallbacks(loadTimeout)
        setLoading(false)
    }

    private fun setLoading(loading: Boolean) {
        binding.progress.isInvisible = !loading
        binding.open.isEnabled = !loading
    }

    private fun showBalance() {
        binding.balance.text = getString(R.string.offer_wall_balance, wallet.balance)
    }

    private companion object {
        /** How long the user is kept waiting before the load is given up on. */
        const val LOAD_TIMEOUT_MS = 10_000L
    }
}

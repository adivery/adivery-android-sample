package com.adivery.sample.kotlin

import android.os.Bundle
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
 * Unlike the other full screen formats, the offer wall never should be loaded ahead of time.
 * Its missions, step states and deadlines are fetched when it loads, so a wall loaded early would
 * show stale ones. Call [Adivery.prepareOfferWallAd] only when the user asks for the wall, and
 * show it as soon as `onOfferWallAdLoaded` arrives. Adivery does not reload a closed wall either,
 * so every tap on the  button loads it again.
 */
class KotlinOfferWallActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOfferWallBinding
    private lateinit var eventLog: AdEventLog
    private lateinit var wallet: Wallet
    private lateinit var placementId: String

    private val listener = object : AdiveryListener() {

        override fun onOfferWallAdLoaded(placementId: String) {
            eventLog.log("onOfferWallAdLoaded")
            setLoading(false)
            Adivery.showAd(placementId)
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

        /** Loading failures are reported here. Hide the indicator so the user can try again. */
        override fun log(placementId: String, message: String) {
            eventLog.log(message)
            setLoading(false)
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

    override fun onDestroy() {
        // Anonymous listeners hold a reference to this activity, so always detach them.
        Adivery.removePlacementListener(placementId)
        super.onDestroy()
    }

    private fun openOfferWall() {
        // The disabled button also keeps a second tap from loading the wall again.
        setLoading(true)

        eventLog.log("prepareOfferWallAd")
        Adivery.prepareOfferWallAd(this, placementId)
    }

    private fun setLoading(loading: Boolean) {
        binding.progress.isInvisible = !loading
        binding.open.isEnabled = !loading
    }

    private fun showBalance() {
        binding.balance.text = getString(R.string.offer_wall_balance, wallet.balance)
    }
}

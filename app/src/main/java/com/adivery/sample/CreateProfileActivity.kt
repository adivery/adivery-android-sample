package com.adivery.sample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.adivery.sample.databinding.ActivityCreateProfileBinding
import com.google.android.material.textfield.TextInputLayout
import java.util.UUID

/**
 * Lets you run the sample against your own dashboard credentials instead of the bundled ones.
 */
class CreateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applySystemBarInsets()

        binding.create.setOnClickListener { createProfile() }
    }

    private fun createProfile() {
        val profile = readProfile() ?: return
        ProfileStore.get(this).save(profile)
        Toast.makeText(this, R.string.message_profile_created, Toast.LENGTH_LONG).show()
        finish()
    }

    /** Returns the profile, or null after marking every invalid field. */
    private fun readProfile(): AdProfile? {
        val name = readProfileName()
        val appId = binding.appIdLayout.requireUuid()
        val interstitial = binding.interstitialLayout.requireUuid()
        val rewarded = binding.rewardedLayout.requireUuid()
        val appOpen = binding.appOpenLayout.requireUuid()
        val banner = binding.bannerLayout.requireUuid()
        val nativeAd = binding.nativeLayout.requireUuid()
        val vast = binding.vastLayout.requireUuid()

        if (name == null || appId == null || interstitial == null || rewarded == null ||
            appOpen == null || banner == null || nativeAd == null || vast == null
        ) {
            return null
        }

        return AdProfile(
            name = name,
            appId = appId,
            interstitialPlacementId = interstitial,
            rewardedPlacementId = rewarded,
            appOpenPlacementId = appOpen,
            bannerPlacementId = banner,
            nativePlacementId = nativeAd,
            vastPlacementId = vast,
        )
    }

    /**
     * Names are the storage key, so reusing one would overwrite the profile already saved under it.
     */
    private fun readProfileName(): String? {
        val value = binding.profileLayout.editText?.text?.toString().orEmpty().trim()
        val errorRes = when {
            value.isEmpty() -> R.string.error_empty
            ProfileStore.get(this).hasProfile(value) -> R.string.error_profile_exists
            else -> null
        }
        binding.profileLayout.error = errorRes?.let(::getString)
        return value.takeIf { errorRes == null }
    }

    private fun TextInputLayout.requireUuid(): String? =
        requireText(R.string.error_invalid_uuid) { it.isValidUuid() }

    private inline fun TextInputLayout.requireText(
        errorRes: Int,
        isValid: (String) -> Boolean,
    ): String? {
        val value = editText?.text?.toString().orEmpty().trim()
        error = if (isValid(value)) null else getString(errorRes)
        return value.takeIf { isValid(it) }
    }

    private fun String.isValidUuid(): Boolean = runCatching { UUID.fromString(this) }.isSuccess
}

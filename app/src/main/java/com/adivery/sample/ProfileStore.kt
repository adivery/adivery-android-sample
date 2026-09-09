package com.adivery.sample

import android.content.Context
import androidx.core.content.edit

/**
 * Stores the [AdProfile]s the sample can run with, backed by [android.content.SharedPreferences].
 *
 * Nothing here is part of the Adivery integration; it only exists so the sample can be run against
 * your own application id and placement ids without rebuilding it.
 */
class ProfileStore private constructor(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val profiles: List<AdProfile>
        get() = profileNames.mapNotNull(::findProfile).sortedBy { it.name }

    var activeProfile: AdProfile
        get() = prefs.getString(KEY_ACTIVE_PROFILE, null)
            ?.let(::findProfile)
            ?: AdProfile.BUNDLED
        set(value) = prefs.edit(commit = true) { putString(KEY_ACTIVE_PROFILE, value.name) }

    private val profileNames: Set<String>
        get() = prefs.getStringSet(KEY_PROFILES, emptySet()).orEmpty()

    fun save(profile: AdProfile) {
        prefs.edit {
            putStringSet(KEY_PROFILES, profileNames + profile.name)
            putString(profile.key(KEY_APP_ID), profile.appId)
            putString(profile.key(KEY_INTERSTITIAL), profile.interstitialPlacementId)
            putString(profile.key(KEY_REWARDED), profile.rewardedPlacementId)
            putString(profile.key(KEY_APP_OPEN), profile.appOpenPlacementId)
            putString(profile.key(KEY_BANNER), profile.bannerPlacementId)
            putString(profile.key(KEY_NATIVE), profile.nativePlacementId)
            putString(profile.key(KEY_VAST), profile.vastPlacementId)
        }
    }

    fun hasProfile(name: String): Boolean = name in profileNames

    private fun findProfile(name: String): AdProfile? {
        val appId = prefs.getString("${name}_$KEY_APP_ID", null) ?: return null
        return AdProfile(
            name = name,
            appId = appId,
            interstitialPlacementId = prefs.read(name, KEY_INTERSTITIAL),
            rewardedPlacementId = prefs.read(name, KEY_REWARDED),
            appOpenPlacementId = prefs.read(name, KEY_APP_OPEN),
            bannerPlacementId = prefs.read(name, KEY_BANNER),
            nativePlacementId = prefs.read(name, KEY_NATIVE),
            vastPlacementId = prefs.read(name, KEY_VAST),
        )
    }

    private fun android.content.SharedPreferences.read(profile: String, key: String) =
        getString("${profile}_$key", "").orEmpty()

    private fun AdProfile.key(key: String) = "${name}_$key"

    companion object {
        private const val PREFS_NAME = "ad_profiles"
        private const val KEY_PROFILES = "profiles"
        private const val KEY_ACTIVE_PROFILE = "active_profile"
        private const val KEY_APP_ID = "app_id"
        private const val KEY_INTERSTITIAL = "interstitial"
        private const val KEY_REWARDED = "rewarded"
        private const val KEY_APP_OPEN = "app_open"
        private const val KEY_BANNER = "banner"
        private const val KEY_NATIVE = "native"
        private const val KEY_VAST = "vast"

        @Volatile
        private var instance: ProfileStore? = null

        @JvmStatic
        fun get(context: Context): ProfileStore =
            instance ?: synchronized(this) {
                instance ?: ProfileStore(context).also {
                    if (!it.hasProfile(AdProfile.BUNDLED.name)) {
                        it.save(AdProfile.BUNDLED)
                    }
                    instance = it
                }
            }
    }
}

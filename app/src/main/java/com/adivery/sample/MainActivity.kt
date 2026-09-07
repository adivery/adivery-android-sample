package com.adivery.sample

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.adivery.sample.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Entry point of the app: pick the profile to run with, pick the language to read, then open one
 * of the placement type demos.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var profileStore: ProfileStore

    private val selectedLanguage: SampleLanguage
        get() = if (binding.languageJava.isChecked) SampleLanguage.JAVA else SampleLanguage.KOTLIN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applySystemBarInsets()

        profileStore = ProfileStore.get(this)

        binding.createProfile.setOnClickListener {
            startActivity(Intent(this, CreateProfileActivity::class.java))
        }
        binding.applyProfile.setOnClickListener { applySelectedProfile() }

        binding.interstitial.setOnClickListener { open(AdSample.INTERSTITIAL) }
        binding.rewarded.setOnClickListener { open(AdSample.REWARDED) }
        binding.appOpen.setOnClickListener { open(AdSample.APP_OPEN) }
        binding.banner.setOnClickListener { open(AdSample.BANNER) }
        binding.nativeAd.setOnClickListener { open(AdSample.NATIVE) }
    }

    override fun onResume() {
        super.onResume()
        showProfiles()
    }

    private fun showProfiles() {
        val names = profileStore.profiles.map { it.name }
        val active = profileStore.activeProfile
        binding.profileDropdown.setSimpleItems(names.toTypedArray())
        binding.profileDropdown.setText(active.name, false)
        binding.activeProfile.text = getString(R.string.active_profile, active.name, active.appId)
    }

    private fun open(sample: AdSample) {
        startActivity(Intent(this, sample.activityFor(selectedLanguage)))
    }

    /**
     * `Adivery.configure` runs once per process with a single application id, so a different profile
     * only takes effect after a restart.
     */
    private fun applySelectedProfile() {
        val selected = binding.profileDropdown.text.toString()
        val profile = profileStore.profiles.firstOrNull { it.name == selected } ?: return
        if (profile.name == profileStore.activeProfile.name) return

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.restart_required_title)
            .setMessage(getString(R.string.restart_required_message, profile.name))
            .setPositiveButton(R.string.restart_now) { _, _ ->
                profileStore.activeProfile = profile
                restart()
            }
            .setNegativeButton(R.string.later, null)
            .setOnDismissListener { showProfiles() }
            .show()
    }

    private fun restart() {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            ?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(launchIntent)
        // Tears the process down so Application.onCreate runs again with the new application id.
        Runtime.getRuntime().exit(0)
    }
}

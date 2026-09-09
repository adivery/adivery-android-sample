package com.adivery.sample

/**
 * A set of Adivery credentials: one application id plus one placement id per ad type.
 *
 * The sample ships with [BUNDLED], but users can add their own profile from
 * [CreateProfileActivity] and switch between them on the home screen.
 */
data class AdProfile(
    val name: String,
    val appId: String,
    val interstitialPlacementId: String,
    val rewardedPlacementId: String,
    val appOpenPlacementId: String,
    val bannerPlacementId: String,
    val nativePlacementId: String,
    val vastPlacementId: String,
) {
    companion object {

        /** Public demo credentials owned by Adivery. Always available in the profile list. */
        val BUNDLED = AdProfile(
            name = "adivery",
            appId = "7e27fb38-5aff-473a-998f-437b89426f66",
            interstitialPlacementId = "de5db046-765d-478f-bb2e-30dc2eaf3f51",
            rewardedPlacementId = "2efedcaa-fcc0-4610-a025-109ff17594af",
            appOpenPlacementId = "9e9dd375-a1fe-4c2b-8432-b5bf8a5095f6",
            bannerPlacementId = "5f2c4c86-a6ec-4735-9a44-f881fe40789f",
            nativePlacementId = "25928bf1-d4f7-432c-aaf7-1780602796c3",
            vastPlacementId = "a750d453-bc0f-4006-8346-d398fcf34a50",
        )
    }
}

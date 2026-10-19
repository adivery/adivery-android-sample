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
    /** Empty when the application has no offer wall, which Adivery support enables per application. */
    val offerWallPlacementId: String,
) {
    companion object {

        /** Public demo credentials owned by Adivery. Always available in the profile list. */
        val BUNDLED = AdProfile(
            name = "adivery",
            appId = "738c5e66-2e4f-4ff0-8118-ef6c8235a5e9",
            interstitialPlacementId = "5788d25b-b7d6-442c-a562-b4bef582c18c",
            rewardedPlacementId = "29f509a9-3826-4ada-8404-4735aa14b510",
            appOpenPlacementId = "c0b3bf0e-75a5-4183-ad86-2cc5c07c07d6",
            bannerPlacementId = "06f0c520-3cc8-45c0-bbf1-f866f7eac525",
            nativePlacementId = "9bc87b9f-3aef-4988-b1d2-2f4e5b67b2c5",
            vastPlacementId = "a750d453-bc0f-4006-8346-d398fcf34a50",
            offerWallPlacementId = "9cad440a-8271-49d2-8f81-0819268a5ce4",
        )
    }
}

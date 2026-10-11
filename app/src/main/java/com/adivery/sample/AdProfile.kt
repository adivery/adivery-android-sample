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
            appId = "d5247816-a170-4b90-b4e2-c0f1fcd804a4",
            interstitialPlacementId = "7436cec1-2b41-4140-bfef-cfd5b0fc7bda",
            rewardedPlacementId = "271c8bd4-9000-476e-b096-6ef9bee296dc",
            appOpenPlacementId = "6f5ebb45-f5a5-4220-9303-b5d44b6b86d2",
            bannerPlacementId = "d47ebd8b-269e-4e59-ba12-3b83326ef351",
            nativePlacementId = "b607ef9a-3218-4a7c-9c34-a91f9888c7e7",
            vastPlacementId = "a750d453-bc0f-4006-8346-d398fcf34a50",
            offerWallPlacementId = "9cad440a-8271-49d2-8f81-0819268a5ce4",
        )
    }
}

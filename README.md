# Adivery Android sample

A working example of every Adivery placement type, implemented twice: once in Kotlin and once in
Java. Import the project into Android Studio and run it — it ships with Adivery's public demo
credentials, so no dashboard account is needed to try it out.

Full documentation: <https://adivery.com/android>

## Placement types

| Placement    | Kotlin                                                                                            | Java                                                                                        |
|--------------|---------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------|
| Interstitial | [`KotlinInterstitialActivity`](app/src/main/java/com/adivery/sample/kotlinsamples/KotlinInterstitialActivity.kt) | [`JavaInterstitialActivity`](app/src/main/java/com/adivery/sample/javasamples/JavaInterstitialActivity.java) |
| Rewarded     | [`KotlinRewardedActivity`](app/src/main/java/com/adivery/sample/kotlinsamples/KotlinRewardedActivity.kt)         | [`JavaRewardedActivity`](app/src/main/java/com/adivery/sample/javasamples/JavaRewardedActivity.java)         |
| App open     | [`KotlinAppOpenActivity`](app/src/main/java/com/adivery/sample/kotlinsamples/KotlinAppOpenActivity.kt)           | [`JavaAppOpenActivity`](app/src/main/java/com/adivery/sample/javasamples/JavaAppOpenActivity.java)           |
| Banner       | [`KotlinBannerActivity`](app/src/main/java/com/adivery/sample/kotlinsamples/KotlinBannerActivity.kt)             | [`JavaBannerActivity`](app/src/main/java/com/adivery/sample/javasamples/JavaBannerActivity.java)             |
| Native       | [`KotlinNativeActivity`](app/src/main/java/com/adivery/sample/kotlinsamples/KotlinNativeActivity.kt)             | [`JavaNativeActivity`](app/src/main/java/com/adivery/sample/javasamples/JavaNativeActivity.java)             |

Each screen requests an ad, shows it, and prints every SDK callback it receives so the ad lifecycle
is visible while the sample runs.

## Supporting code

| File | What it shows |
|------|---------------|
| [`SampleApplication.kt`](app/src/main/java/com/adivery/sample/SampleApplication.kt) | `Adivery.configure` and `Adivery.setLoggingEnabled`, called once per process |
| [`AppOpenAdManager.kt`](app/src/main/java/com/adivery/sample/AppOpenAdManager.kt) | The recommended app open pattern: show the ad when the user returns after a few seconds away |
| [`view_native_ad.xml`](app/src/main/res/layout/view_native_ad.xml) | The native ad template and the view ids `AdiveryNativeAdView` populates |
| [`proguard-rules.pro`](app/proguard-rules.pro) | A local copy of the `keep`/`dontwarn` rules the SDK already contributes through its own consumer rules, kept because the documentation links to this file |
| [`ProfileStore.kt`](app/src/main/java/com/adivery/sample/ProfileStore.kt), [`CreateProfileActivity.kt`](app/src/main/java/com/adivery/sample/CreateProfileActivity.kt) | Sample plumbing only — storage for the credentials the sample runs with |

## Running with your own credentials

The home screen's **New profile** button takes an application id and one placement id per type from
the [Adivery publisher panel](https://panel.adivery.com). Select the new profile and press
**Apply**; because `Adivery.configure` runs once per process, the sample restarts to pick it up.

## Requirements

- `minSdk` 21 or above
- The SDK ships its own manifest entries and ProGuard rules, so adding the dependency is enough:

```kotlin
dependencies {
    implementation("com.adivery:sdk:4.9.0")
}
```

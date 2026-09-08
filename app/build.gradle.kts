plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.adivery.sample"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.adivery.sample"
        // The Adivery SDK requires minSdk 21 or above.
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.adivery.sdk)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.material)
    // The Adivery SDK reads the Google Advertising ID (GAID) via AdvertisingIdClient
    // to pass it to ad networks for better ad targeting. Fetching the user's GAID only
    // happens when this artifact is on the classpath, so the publisher app must declare
    // it explicitly, or the GAID will not be sent.
    implementation(libs.play.services.ads.identifier)
    // Only the Pre-Roll (VAST) sample needs these: ExoPlayer plays the content, its IMA extension
    // reads the VAST url Adivery hands out and inserts the ad before it.
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)
    implementation(libs.media3.exoplayer.ima)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

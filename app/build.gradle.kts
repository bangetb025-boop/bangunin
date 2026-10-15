plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
kotlinOptions {
        jvmTarget = "17"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
        namespace = "com.bangunin.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bangunin.app"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}

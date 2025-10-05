import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")

}

// Read API key from local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { stream ->
        localProperties.load(stream)
    }
}

android {
    namespace = "com.example.donowaste"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.donowaste"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Expose the API key as a build config field
        buildConfigField("String", "GEMINI_API_KEY", "\"${localProperties.getProperty("GEMINI_API_KEY")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Jetpack Compose
    buildFeatures {
        compose = true
        buildConfig = true // Enable BuildConfig
    }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.14" }

    // JDK 17
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    // ----- Compose BOM (pins all Compose artifacts to compatible versions)
    implementation(platform("androidx.compose:compose-bom:2024.09.02"))

    // Material 3 + core UI
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Activity + lifecycle + navigation
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.navigation:navigation-compose:2.8.2")

    // AndroidX core + AppCompat + (legacy) Material — explicit coords to avoid catalog issues
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")

    // ----- Firebase BOM + services
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.android.gms:play-services-auth:21.2.0") // Google Sign-In

    // ----- Gemini
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // ----- Tests (explicit so they don't rely on the version catalog)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    implementation("androidx.navigation:navigation-compose:2.7.7")
}
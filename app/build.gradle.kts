plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.easyshop"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.easyshop"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
// Compose BOM – version managed centrally
    implementation(platform(libs.androidx.compose.bom))

    // Core libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose
    implementation(libs.androidx.ui) // Should map to androidx.compose.ui:ui
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Navigation for Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore) // ✅ Required for NavHost

    implementation ("io.coil-kt:coil-compose:2.2.0")
    implementation("com.tbuonomo:dotsindicator:5.1.0")

    // ⛔ Removed: `libs.androidx.navigation.compose.jvmstubs` — it causes crash/conflict

    //razorpay
    implementation("com.razorpay:checkout:1.6.33")

    //Icon
    implementation("androidx.compose.material:material-icons-extended")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debug-only tools
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
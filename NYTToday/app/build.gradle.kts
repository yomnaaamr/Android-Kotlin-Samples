plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.google.gms)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.nyttoday"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.nyttoday"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }


        buildConfigField("String", "NYT_API_KEY", "\"DBrGZBTi4MVRtFbaUuey8EUvkAwTsziR\"")

    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    // Jetpack Compose Navigation
    implementation (libs.androidx.navigation.compose)

//    // viewModel
//    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${rootProject.extra["lifecycle_version"]}")
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation (libs.androidx.lifecycle.runtime.compose)


//
//    //Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)


//    splash screen
    implementation(libs.androidx.core.splashscreen)


//    // Dagger
    implementation(libs.google.dagger.hilt.android)
    kapt(libs.google.dagger.compiler) // Dagger compiler
    kapt(libs.google.hilt.compiler)   // Hilt compiler
    implementation(libs.androidx.hilt.navigation.compose)

//    // WorkManager
    implementation (libs.androidx.work.runtime.ktx)
//
//    // Hilt extension for WorkManager
    implementation (libs.androidx.hilt.work)
    kapt(libs.androidx.hilt.work.compiler)
//


    //    firebase

    implementation(platform(libs.firebase.bom))
    // add the dependencies for Firebase Authentication and Cloud Firestore
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.analytics)
    // Firebase Firestore dependency
    implementation (libs.firebase.firestore.ktx)

    implementation (libs.firebase.auth.ktx)
//     required to integrate Google Sign-In into your app.
    implementation (libs.play.services.auth)

//    realtime database
    implementation(libs.firebase.database)

    //    Icons library
    implementation(libs.androidx.material.icons.extended)

//       coil
    implementation (libs.coil.compose)


    // Retrofit
    implementation(libs.retrofit)


    // Kotlin serialization
    implementation(libs.kotlinx.serialization.json)

// Retrofit with Kotlin serialization Converter
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.okhttp)


    implementation(libs.androidx.vectordrawable.animated)


//    do not need it anymore it is part of material3 now
//    implementation(libs.accompanist.swiperefresh)

    debugImplementation(libs.leakcanary.android)


    // Paging
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.compose)
//    implementation(kotlin("reflect"))


}
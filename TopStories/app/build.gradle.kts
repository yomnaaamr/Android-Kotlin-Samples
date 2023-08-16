import java.lang.System.getProperty

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("kotlin-kapt")
    id ("androidx.navigation.safeargs.kotlin")
    id ("kotlin-parcelize")
    id ("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.topstories"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.topstories"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "API_KEY", "\"${getProperty("API_KEY")}\"")
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
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_18.toString()
    }

    buildFeatures {
        dataBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    //    safe-args / Navigation
    val navVersion = "2.6.0"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")


    // lifecycle
    val lifecycle_version = "2.6.1"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")



    // Retrofit with Moshi Converter
    implementation ("com.squareup.retrofit2:converter-moshi:2.9.0")

    // Room
    val room_version = "2.5.2"
    implementation("androidx.room:room-runtime:$room_version")
    implementation ("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")


    // Moshi
    implementation ("com.squareup.moshi:moshi-kotlin:1.13.0")

    // Coil
    implementation ("io.coil-kt:coil:1.1.1")


    // Glide
    implementation ("com.github.bumptech.glide:glide:4.12.0")

    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")

    implementation("com.google.code.gson:gson:2.9.0")


    // Coroutines
    val coroutines = "1.6.4"
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines")


    // WorkManager
    val version_work = "2.8.1"
    implementation ("androidx.work:work-runtime-ktx:$version_work")


    // joda time library for dealing with time
    implementation ("joda-time:joda-time:2.10")

}
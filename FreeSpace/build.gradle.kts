// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra.apply {
        set("lifecycle_version","2.6.1")
        set("nav_version","2.7.2")
        set("room_version", "2.5.2")
    }
    dependencies{
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")
//        classpath ("androidx.hilt:hilt-compiler:1.0.0")
    }
    repositories {
        google()
    }
}
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.13"
    id("com.android.library") version "8.0.2" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}



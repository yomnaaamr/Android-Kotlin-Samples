// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies{
//        classpath(libs.dagger.hilt.android)
    }
    repositories {
        google()
    }
}
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.devtools.ksp) apply false
    alias(libs.plugins.google.gms) apply false
    alias(libs.plugins.dagger.hilt.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.serialization) apply false

//    id("com.android.library") version "8.0.2" apply false

}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
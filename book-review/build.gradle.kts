// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    repositories {
        google()
        mavenCentral()
    }

    dependencies {

        classpath("com.android.tools.build:gradle:7.4.2")
        val kotlinVersion = "1.8.0"
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        val navVersion = "2.5.3"
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")

    }
}



plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id ("com.android.library") version "7.4.0" apply false
    id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false

}
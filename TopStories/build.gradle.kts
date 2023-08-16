// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript{

    repositories {
        google()
        mavenCentral()
    }

    dependencies{
        classpath("com.android.tools.build:gradle:7.4.2")
        val kotlinVersion = "1.9.0"
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        val navVersion = "2.6.0"
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
    }
}
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id ("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false
}
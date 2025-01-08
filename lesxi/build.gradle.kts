// Project-level build.gradle.kts file
buildscript {
    repositories {
        google()  // Ensure Google repository is included
        mavenCentral()
    }

    dependencies {
        // Add the Google Services Gradle plugin classpath
        classpath("com.google.gms:google-services:4.4.2")
    }
}

plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
    // Don't apply 'com.google.gms.google-services' here
}

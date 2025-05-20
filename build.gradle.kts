buildscript {
    repositories {
        google()
        mavenCentral()
        maven (url ="https://jitpack.io")

    }



    dependencies {
        val nav_version = "2.8.9"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
        classpath(libs.google.services)
        classpath ("com.android.tools.build:gradle:8.3.1")    }
}


plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.googleAndroidLibrariesMapsplatformSecretsGradlePlugin) apply false
}


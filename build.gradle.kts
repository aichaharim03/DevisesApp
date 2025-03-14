buildscript {
    repositories {
        google()
    }


    dependencies {
        val navVersion = "2.7.3"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
        classpath(libs.google.services)
        classpath ("com.android.tools.build:gradle:8.3.1") // ou une version stable plus récente si disponible

    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
}
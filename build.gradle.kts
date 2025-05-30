// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.android.library) apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
}

detekt {
    toolVersion = "1.23.1"
    buildUponDefaultConfig = true
    allRules = false
}
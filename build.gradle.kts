// https://youtrack.jetbrains.com/issue/KTIJ-19369
@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
   alias(libs.plugins.androidApplication) apply false
   alias(libs.plugins.androidLibrary) apply false
   alias(libs.plugins.kotlinAndroid) apply false
}

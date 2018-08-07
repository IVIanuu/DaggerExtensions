@file:Suppress("ClassName", "unused")

object Versions {
    // android
    const val compileSdk = 28
    const val minSdk = 14
    const val minSdkConductor = 16
    const val targetSdk = 28
    const val versionCode = 1
    const val versionName = "1.0"

    const val androidGradlePlugin = "3.1.4"

    const val archComponentsWork = "1.0.0-alpha05"

    const val autoCommon = "0.10"
    const val autoService = "1.0-rc4"

    const val conductor = "2.1.5"

    const val dagger = "2.16"

    const val glide = "4.7.1"

    const val javaPoet = "1.11.1"

    const val kotlin = "1.2.51"
    const val kotlinPoet = "0.7.0"

    const val mavenGradlePlugin = "2.1"
    const val support = "28.0.0-alpha3"
}

object Deps {
    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.androidGradlePlugin}"

    const val archComponentsWorkRuntime = "android.arch.work:work-runtime-ktx:${Versions.archComponentsWork}"

    const val autoCommon = "com.google.auto:auto-common:${Versions.autoCommon}"

    const val autoService = "com.google.auto.service:auto-service:${Versions.autoService}"

    const val conductor = "com.bluelinelabs:conductor:${Versions.conductor}"

    const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
    const val daggerAndroid = "com.google.dagger:dagger-android:${Versions.dagger}"
    const val daggerAndroidSupport = "com.google.dagger:dagger-android-support:${Versions.dagger}"
    const val daggerAndroidProcessor = "com.google.dagger:dagger-android-processor:${Versions.dagger}"

    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glide}"

    const val javaPoet = "com.squareup:javapoet:${Versions.javaPoet}"

    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
    const val kotlinPoet = "com.squareup:kotlinpoet:${Versions.kotlinPoet}"

    const val mavenGradlePlugin = "com.github.dcendents:android-maven-gradle-plugin:${Versions.mavenGradlePlugin}"

    const val supportAppCompat = "com.android.support:appcompat-v7:${Versions.support}"
}
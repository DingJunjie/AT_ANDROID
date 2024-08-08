plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.plugin.serialization) //    添加 seriali 插件，不添加seriali的类打包后会找不到
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("bitat.jks")
            storePassword = "9527at"
            keyAlias = "at"
            keyPassword = "9527at"
        }
    }
    namespace = "com.bitat"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bitat"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        signingConfig = signingConfigs.getByName("debug")

        android {
            defaultConfig {
                ndk {
                    abiFilters.addAll(listOf("arm64-v8a")) // 暂仅支持64位ARM架构的设备（32位：armeabi-v7a）
                }
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { //        jvmTarget = "1.8"
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx) //    implementation(libs.androidx.activity.compose)
    //    implementation(platform(libs.androidx.compose.bom))
    //    implementation(libs.androidx.ui)
    //    implementation(libs.androidx.ui.graphics)
    //    implementation(libs.androidx.ui.tooling.preview)
    //    implementation(libs.androidx.material3)
    //    testImplementation(libs.junit)
    //    androidTestImplementation(libs.androidx.junit)
    //    androidTestImplementation(libs.androidx.espresso.core)
    //    androidTestImplementation(platform(libs.androidx.compose.bom))
    //    androidTestImplementation(libs.androidx.ui.test.junit4)
    //    debugImplementation(libs.androidx.ui.tooling)
    //    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.navigation.compose)

    implementation(libs.androidx.lifecycle.viewmodel.compose.v261)

    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json) //    implementation(libs.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose.android)


    //    // Java language implementation
    //    implementation("androidx.navigation:navigation-fragment:$navVersion")
    //    implementation("androidx.navigation:navigation-ui:$navVersion")

    // Kotlin
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Feature module Support
    implementation(libs.androidx.navigation.dynamic.features.fragment)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.junit.ktx)
    testImplementation(libs.junit.junit)

    // Testing Navigation
    androidTestImplementation(libs.androidx.navigation.testing)

    // Jetpack Compose Integration
    implementation(libs.navigation.compose)

    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
    implementation(libs.kotlinx.serialization.json)

    // 网易易盾 一键登录
    implementation(libs.quicklogin)

    // 相机
//    implementation(libs.androidx.camera.core)
//    implementation(libs.androidx.camera.camera2.v110beta01)
//    implementation(libs.androidx.camera.lifecycle.v110beta01)
//    implementation(libs.androidx.camera.video)
//    implementation(libs.androidx.camera.view.v110beta01)
//    implementation(libs.androidx.camera.extensions)

    // CameraX core library using the camera2 implementation
    // The following line is optional, as the core library is included indirectly by camera-camera2
    implementation(libs.camera.core.v140rc01)
    implementation(libs.camera.camera2.v140rc01)
    // If you want to additionally use the CameraX Lifecycle library
    implementation(libs.camera.lifecycle.v140rc01)
    // If you want to additionally use the CameraX VideoCapture library
    implementation(libs.camera.video)
    // If you want to additionally use the CameraX View class
    implementation(libs.androidx.camera.view)
    // If you want to additionally add CameraX ML Kit Vision Integration
    implementation(libs.androidx.camera.mlkit.vision)
    // If you want to additionally use the CameraX Extensions library
    implementation(libs.camera.extensions.v140rc01)



    implementation(libs.accompanist.permissions)
    implementation(libs.ui)
    implementation(libs.com.google.protobuf.protobuf.java)
    implementation(libs.androidx.material)
    implementation(libs.qiniu.android.sdk)
    implementation(libs.androidx.activity.ktx)

    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)

    implementation(libs.lottie.compose)

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))


}
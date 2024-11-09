import android.annotation.SuppressLint
import com.android.build.api.variant.FilterConfiguration
import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import java.text.SimpleDateFormat
import java.util.Date

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
}

val dateFormat = SimpleDateFormat("yyMMddHH")

android {
    namespace = "io.github.lumyuan.turingbox"
    compileSdk = 33

    signingConfigs {
        create("turing-box-signing") {
            storeFile = file("turing-key.jks")
            storePassword = "qwq9870" // Ensure correct indentation here
            keyAlias = "rootes"       // Ensure correct indentation here
            keyPassword = "qwq9870"
            this.enableV1Signing = true
            this.enableV2Signing = true
            this.enableV3Signing = true
            this.enableV4Signing = true
        }
    }

    defaultConfig {
        applicationId = "io.github.lumyuan.turingbox"
        minSdk = 21
        val appVersionCode = dateFormat.format(Date()).trim().toLong()

        @SuppressLint("EditedTargetSdkVersion")
        targetSdk = 33
        versionCode = appVersionCode.toInt()
        versionName = "0.0.3-$appVersionCode"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            isDebuggable = false
            isJniDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("turing-box-signing")
        }

        create("release-mini") {
            isShrinkResources = true
            isMinifyEnabled = true
            isDebuggable = false
            isJniDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("turing-box-signing")
        }
    }

    val abiCodes = mapOf("armeabi-v7a" to 1, "arm64-v8a" to 2, "x86" to 3, "x86_64" to 4)

    applicationVariants.all {
        val buildType = this.buildType.name
        val variant = this
        outputs.all {
            val name =
                this.filters.find { it.filterType == FilterConfiguration.FilterType.ABI.name }?.identifier
            val baseAbiCode = abiCodes[name]
            if (baseAbiCode != null) {
                //写入cpu架构信息
                variant.buildConfigField("String", "CUP_ABI", "\"${name}\"")
            }
            if (this is ApkVariantOutputImpl) {
                //修改apk名称
                outputFileName = "Turing Box-${defaultConfig.versionName}-${buildType}.apk"
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    sourceSets {
        getByName("main") {
            assets {
                srcDirs("src\\main\\assets", "src\\main\\assets")
            }
        }
    }
}

dependencies {

    // Core and Compose dependencies
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(libs.android.appcompat)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation("androidx.compose.material3:material3:1.0.0") // Material3 for Android 12 UI features

    // OkHttp dependencies
    implementation("com.squareup.okhttp3:okhttp:4.10.0") // OkHttp version (use the latest)

implementation("org.bouncycastle:bcprov-jdk15on:1.70") // Conscrypt 
implementation("org.conscrypt:conscrypt-android:2.5.0") // OpenJSSE 
implementation("org.openjsse:openjsse:1.0.0") 

implementation("androidx.compose.ui:ui:1.4.0") // 或者你项目当前使用的版本
    implementation("androidx.compose.material:material:1.4.0") // 或者你项目当前使用的版本
    implementation("androidx.compose.foundation:foundation:1.4.0") // 或者你项目当前使用的版本
    implementation("androidx.compose.runtime:runtime:1.4.0") // 或者你项目当前使用的版本

  
    // Compose UI Testing and Preview
    implementation(libs.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.constraint.layout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    androidTestImplementation(platform(libs.compose.bom))
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    // ImmersionBar for status bar and navigation bar styling
    implementation(libs.immersionbar.ui)
    implementation(libs.immersionbar.ui.ktx)

    // JNI
    implementation(project(":TuringBoxJNI"))

    // Gson for JSON parsing
    implementation(libs.gson)
}

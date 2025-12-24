plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.nankaworks.cryptomessage"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nankaworks.cryptomessage"
        minSdk = 24
        targetSdk = 35
        versionCode = 20
        versionName = "20.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // FileProviderのauthorityを定義
        resValue("string", "file_provider_authority", "${applicationId}.fileprovider")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                debugSymbolLevel = "SYMBOL_TABLE"
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("com.google.zxing:core:3.4.1")
    implementation("com.journeyapps:zxing-android-embedded:4.2.0")
    implementation("com.google.android.material:material:1.5.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.dotsindicator)
    implementation(libs.barcode.scanning)
    implementation(libs.review)
    implementation(libs.review.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.play.services.ads)
    implementation("com.google.android.play:review:2.0.2")
    implementation("com.google.android.play:review-ktx:2.0.2")
}

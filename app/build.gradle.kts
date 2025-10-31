plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.manilaavenue"
    compileSdk = 34



    defaultConfig {
        applicationId = "com.example.manilaavenue"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        viewBinding { enable = true }

        dataBinding { enable = true }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.bom)
    implementation(libs.firebase.storage)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.core)
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.gson)
    implementation(libs.glide)
    implementation(libs.dotsindicator)
    implementation(libs.firebase.database.ktx)
    implementation(libs.google.material)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.circleimageview)
    implementation(libs.picasso)
    implementation(libs.play.services.maps)
    implementation(libs.osmdroid.android)
    testImplementation(libs.androidx.runner)
    testImplementation(libs.junit)
    testImplementation(libs.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.github.bumptech.glide:glide:4.14.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.0")

}




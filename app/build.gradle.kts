plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
//    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "com.englishto.urdu.translator"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.englishto.urdu.translator"
        minSdk = 26
        targetSdk = 35
        versionCode = 4
        versionName = "1.0.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true //make it true for production
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += "version"

    productFlavors {
        create("english_to_urdu") {
            dimension = "version"
            applicationId = "com.englishto.urdu.translator"
            versionCode = 14
            versionName = "1.0.14"
            buildConfigField("String", "FLAVOR", "\"english_to_urdu\"")
        }
        create("english_to_hindi") {
            dimension = "version"
            applicationId = "com.englishto.hindi.translator"
            versionCode = 9
            versionName = "1.0.9"
            buildConfigField("String", "FLAVOR", "\"english_to_hindi\"")
        }
        create("arabic_to_english") {
            dimension = "version"
            applicationId = "com.arabic.to.english.translator"
            versionCode = 10
            versionName = "1.0.10"
            buildConfigField("String", "FLAVOR", "\"arabic_to_english\"")
        }
        create("english_to_marathi") {
            dimension = "version"
            applicationId = "com.english.to.marathi.translator"
            versionCode = 10
            versionName = "1.0.10"
            buildConfigField("String", "FLAVOR", "\"english_to_marathi\"")
        }
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
        buildConfig = true

    }


}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.activity)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.mlkit)
    implementation(libs.recognition)
    implementation(libs.converterGson)

}

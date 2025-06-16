plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android") //hilt
}

android {
    namespace = "com.miassolutions.rollcall"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.miassolutions.rollcall"
        minSdk = 26
        targetSdk = 35
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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //splash screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    implementation("de.hdodenhof:circleimageview:3.1.0")




    implementation("com.leinardi.android:speed-dial:3.2.0")
    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.9")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.9") //
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    // Room Database
    implementation("androidx.room:room-runtime:2.7.1")
    implementation("androidx.room:room-ktx:2.7.1")
    ksp("androidx.room:room-compiler:2.7.1")
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    // Image Loading
    implementation("com.github.bumptech.glide:glide:4.16.0") // Assumed latest stable version
    ksp("com.github.bumptech.glide:compiler:4.16.0")
    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.10.1") // Assumed latest stable version
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
    //data store
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    // fragment for view model code generation
    implementation("androidx.fragment:fragment-ktx:1.8.6")
    //hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-android-compiler:2.51.1")

    implementation("org.apache.poi:poi:5.2.2")
    implementation("org.apache.poi:poi-ooxml:5.2.2")

    implementation("org.apache.xmlbeans:xmlbeans:5.1.1") // Match a recent stable version
    implementation("com.fasterxml:aalto-xml:1.3.2") // A fast, non-blocking XML processor
    implementation("javax.xml.stream:stax-api:1.0-2") // Standard Java XML Streaming API

    implementation("com.github.angads25:toggle:1.1.0")

}
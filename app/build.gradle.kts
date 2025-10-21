import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services") // Firebase plugin
}

android {
    namespace = "com.crecheconnect.crechemanagement_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.crecheconnect.crechemanagement_app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
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
}

// Ensure all Kotlin compile tasks use JVM 1.8
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // Firebase BOM (manages versions for you)
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))

    // Firebase SDKs
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Firebase Realtime Database
    implementation("com.google.firebase:firebase-database-ktx")

    // AndroidX
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.fragment:fragment-ktx:1.6.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // dropdown menu
    implementation("com.google.android.material:material:1.10.0")


    // Stripe Android SDK
    implementation("com.stripe:stripe-android:20.45.0")

    // Firebase Functions client (to call your backend securely)
    implementation("com.google.firebase:firebase-functions-ktx")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.project"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.project"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
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
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1") // Core Kotlin extensions
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1") // Lifecycle-aware components
    implementation("androidx.activity:activity-compose:1.7.0") // Compose-based activities
    implementation(platform("androidx.compose:compose-bom:2023.08.00")) // Compose BOM (Bill of Materials)
    implementation("androidx.compose.ui:ui") // Compose UI library
    implementation("androidx.compose.ui:ui-graphics") // Compose UI graphics library
    implementation("androidx.compose.ui:ui-tooling-preview") // Compose UI tooling for preview
    implementation("androidx.compose.material3:material3") // Compose Material3 components
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7") // Navigation fragment KTX
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7") // Navigation UI KTX
    testImplementation("junit:junit:4.13.2") // JUnit testing framework
    androidTestImplementation("androidx.test.ext:junit:1.1.5") // JUnit Android testing support
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1") // Espresso UI testing framework
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00")) // Compose BOM for Android tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4") // Compose UI testing with JUnit 4
    debugImplementation("androidx.compose.ui:ui-tooling") // Compose UI tooling for debugging
    debugImplementation("androidx.compose.ui:ui-test-manifest") // Compose UI test manifest
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.compose.material:material-icons-core") // Material Icons core library
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material3:material3:1.2.1")// Material Icons extended library
}

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs")
    id("org.jetbrains.kotlin.plugin.parcelize")
}

fun getSecret(key: String): String? {
    val items = HashMap<String, String>()

    try {
        val f = File("secrets.properties")
        f.forEachLine { items[it.split("=")[0]] = it.split("=")[1] }
    } catch (e: Exception) {
        print(e)
        return null
    }
    return items[key]
}

android {
    compileSdk = 32

    applicationVariants.all {
        resValue("string", "versionName", versionName)
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("keystores/debug/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        create("release") {
            storeFile = file("keystores/release/watkanikaan.keystore")
            storePassword = getSecret("storePassword") ?: System.getenv("SIGNING_STORE_PASSWORD")
            keyAlias = getSecret("keyAlias") ?: System.getenv("SIGNING_KEY_ALIAS")
            keyPassword = getSecret("storePassword") ?: System.getenv("SIGNING_KEY_PASSWORD")
        }
    }

    defaultConfig {
        applicationId = ("nl.watkanikaan.app")
        minSdk = 26
        targetSdk = 32
        versionCode = 12
        versionName = "1.4.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
        getByName("release") {
            val tmpFilePath = System.getProperty("user.home") + "/work/_temp/"
            val allFilesFromDir = File(tmpFilePath).listFiles()

            if (allFilesFromDir != null) {
                val keystoreFile = allFilesFromDir.first()
                keystoreFile.renameTo(File("watkanikaan.keystore"))
            }

            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
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
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    lint {
        baseline = file("lint-baseline.xml")
    }
}

dependencies {
    // todo make pretty
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.24.13-rc")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.10")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.3")
    implementation("androidx.fragment:fragment-ktx:1.5.0")

    // google - android
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.google.android.material:material:1.7.0-alpha03")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation("com.google.android.gms:play-services-location:20.0.0")
    implementation("com.google.maps.android:android-maps-utils:2.4.0")
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    // testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("org.mockito:mockito-core:4.0.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")

    // network
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // database
    implementation("androidx.room:room-runtime:2.4.2")
    implementation("androidx.room:room-ktx:2.4.2")
    kapt("androidx.room:room-compiler:2.4.2")

    // Dependency Injection
    implementation("com.google.dagger:hilt-android:2.43.2")
    kapt("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.4.2")
    kapt("com.google.dagger:hilt-android-compiler:2.43.2")

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.0")

    // In app review
    implementation("com.google.android.play:core:1.10.3")
    implementation("com.google.android.play:core-ktx:1.8.1")

    // permission helper
    implementation("com.github.TimGW:PermissionsHelper:v1.0.0")
}
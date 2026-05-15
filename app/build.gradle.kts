plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    if (name.contains("UnitTest", ignoreCase = true)) {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs += listOf("-Xadd-modules=java.desktop")
        }
    }
}

tasks.withType<Test>().configureEach {
    jvmArgs("--add-modules=java.desktop")
}
android {
    namespace = "hu.kdea.szavazas"
    compileSdk = 34

    defaultConfig {
        applicationId = "hu.kdea.szavazas"
        minSdk = 24
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
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
    packaging {
        resources {
            pickFirsts.add("META-INF/sisu/javax.inject.Named")
        }
    }
}

// Configure all test tasks (including compilation) to use the Java desktop module
tasks.withType<Test>().configureEach {
    jvmArgs("--add-modules=java.desktop")
}

// Ensure unit tests compile with java.desktop
tasks.withType<JavaCompile>().configureEach {
    if (name.contains("Test", ignoreCase = true)) {
        options.compilerArgs.add("--add-modules=java.desktop")
    }
}

dependencies {
    // BoofCV for all image processing
    implementation("org.boofcv:boofcv-android:1.2.0")
    implementation("org.boofcv:boofcv-core:1.2.0")
    implementation("org.boofcv:boofcv-geo:1.2.0")
    implementation("org.georegression:georegression:0.28.0")

    // CameraX
    implementation("androidx.camera:camera-core:1.3.0")
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")

    // QR
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("com.google.zxing:core:3.5.2")

    // AndroidX
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Unit testing – include boofcv classes and JVM dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("com.google.code.gson:gson:2.10.1")
    testImplementation("org.boofcv:boofcv-core:1.2.0")   // essential for ConvertBufferedImage
    testImplementation("org.boofcv:boofcv-geo:1.2.0")
    testImplementation("org.boofcv:boofcv-io:1.2.0")
    // Android instrumentation tests
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    // BoofCV for image processing
    implementation("org.boofcv:boofcv-core:0.41")
    implementation("org.boofcv:boofcv-io:0.41")    // needed by UtilImageIO

    // ZXing for QR decoding
    implementation("com.google.zxing:core:3.5.1")

    // JUnit for tests
    testImplementation("junit:junit:4.13.2")
}
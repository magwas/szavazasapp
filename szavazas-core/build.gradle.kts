plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(project(":konveyor"))
    implementation(libs.dagger)
    implementation(libs.javax.inject)

    annotationProcessor(libs.dagger.compiler)
    testAnnotationProcessor(libs.dagger.compiler)

    implementation("org.boofcv:boofcv-core:1.2.0")
    implementation("org.boofcv:boofcv-io:1.2.0")
    implementation("com.google.zxing:core:3.5.1")

    testImplementation(testFixtures(project(":konveyor")))
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito.core)
}

plugins {
    id("com.android.application")
    id("jacoco") // Thêm plugin JaCoCo
}

android {
    namespace = "com.duyth10.dellhieukieugiservice"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.duyth10.dellhieukieugiservice"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    // JaCoCo configuration for unit tests
    testOptions {
        unitTests.all {
            it.extensions.configure(JacocoTaskExtension::class) {
                isIncludeNoLocationClasses = true
                excludes = listOf("jdk.internal.*") // Exclude certain classes
            }
        }
    }
}

// JaCoCo configuration
jacoco {
    toolVersion = "0.8.10" // Set JaCoCo version
}

tasks.register("createDebugCoverageReport", JacocoReport::class) {
    dependsOn("connectedDebugAndroidTest") // Ensure tests are executed first

    reports {
        xml.required.set(true)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml")) // Output folder for HTML report
    }

    // File filters to exclude from coverage report
    val fileFilter = listOf(
        "**/R.class", "**/R$*.class", "**/BuildConfig.*",
        "**/Manifest*.*", "**/*Test*.*", "android/**/*.*"
    )

    val classDirs = fileTree(mapOf(
        "dir" to "${buildDir}/intermediates/javac/debug",
        "excludes" to fileFilter
    ))

    val sourceDirs = files("src/main/java")

    classDirectories.setFrom(classDirs)
    sourceDirectories.setFrom(sourceDirs)

    executionData.setFrom(fileTree(mapOf("dir" to "${buildDir}/outputs/code_coverage/debugAndroidTest/connected", "includes" to listOf("**/*.ec"))))

    // Print the coverage report location after execution
    doLast {
        println("Coverage report generated at: ${layout.buildDirectory.dir("jacocoHtml").get().asFile.absolutePath}/index.html")
    }
}

dependencies {
    // Core libraries
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // QR code scanning
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.4.1")
    implementation("com.google.mlkit:barcode-scanning:17.0.0")

    // CameraX
    implementation("androidx.camera:camera-core:1.3.0")
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")
    implementation("androidx.camera:camera-extensions:1.3.0")

    // Unit testing dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.8.0")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.robolectric:robolectric:4.8.1")

    // Android Test dependencies (Instrumentation Testing)
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-idling-resource:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    debugImplementation("androidx.fragment:fragment-testing:1.8.3")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
}

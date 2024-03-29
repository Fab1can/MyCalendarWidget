plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "eu.fab1can.mycalendarwidget"
    compileSdk = 34

    defaultConfig {
        applicationId = "eu.fab1can.mycalendarwidget"
        minSdk = 31
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
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += arrayOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE.txt",
                "**/package-info.java",
                "META-INF/groovy-release-info.properties",
                "META-INF/INDEX.LIST",
                "META-INF/groovy/**",
                "zoneinfo-global/**",
                "org/apache/commons/codec/language/bm/*.txt",
                "META-INF/DEPENDENCIES"
            )

        }
        jniLibs {
            excludes += arrayOf("META-INF/groovy/**", "zoneinfo-global/**")
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.0")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation("com.google.android.material:material:1.12.0-alpha03")
    implementation("com.google.apis:google-api-services-tasks:v1-rev20230401-2.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.23.0")
    implementation("com.google.apis:google-api-services-calendar:v3-rev305-1.23.0")
    implementation("com.google.android.gms:play-services-auth:20.4.0")
    implementation("androidx.annotation:annotation:1.7.0")

    implementation("org.mnode.ical4j:ical4j:3.2.14")
    implementation("javax.cache:cache-api:1.1.1")
    implementation("backport-util-concurrent:backport-util-concurrent:3.1")
    implementation("commons-codec:commons-codec:1.16.0")
    implementation("commons-lang:commons-lang:2.6")

    testImplementation("junit:junit:4.13.2")

    implementation("org.slf4j:slf4j-api:2.0.9")
    testImplementation("org.slf4j:slf4j-simple:2.0.9")
    implementation("uk.uuid.slf4j:slf4j-android:2.0.9-0")

    //to avoid conflicts in libraries
    implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")

    implementation("com.google.api-client:google-api-client-android:1.23.0") {
        exclude(group = "org.apache.httpcomponents")
    }

    //so that we can easily control permissions
    implementation("pub.devrel:easypermissions:3.0.0")

    val workVersion = "2.9.0"

    // (Java only)
    implementation("androidx.work:work-runtime:$workVersion")

    // Kotlin + coroutines
    implementation("androidx.work:work-runtime-ktx:$workVersion")

    // optional - RxJava2 support
    implementation("androidx.work:work-rxjava2:$workVersion")

    // optional - GCMNetworkManager support
    implementation("androidx.work:work-gcm:$workVersion")

    // optional - Test helpers
    androidTestImplementation("androidx.work:work-testing:$workVersion")

    // optional - Multiprocess support
    implementation ("androidx.work:work-multiprocess:$workVersion")
}
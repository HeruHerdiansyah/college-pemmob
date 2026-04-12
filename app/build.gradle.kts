import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

// Load env.properties for database configuration
val envProperties = Properties()
val envFile = rootProject.file("env.properties")
if (envFile.exists()) {
    envProperties.load(envFile.inputStream())
}

android {
    namespace = "com.example.assignment"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.assignment"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Database config from env.properties
        buildConfigField("String", "DB_HOST", "\"${envProperties.getProperty("DB_HOST", "10.0.2.2")}\"")
        buildConfigField("String", "DB_PORT", "\"${envProperties.getProperty("DB_PORT", "3306")}\"")
        buildConfigField("String", "DB_NAME", "\"${envProperties.getProperty("DB_NAME", "college_smartzoo")}\"")
        buildConfigField("String", "DB_USER", "\"${envProperties.getProperty("DB_USER", "root")}\"")
        buildConfigField("String", "DB_PASSWORD", "\"${envProperties.getProperty("DB_PASSWORD", "password")}\"")
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
    buildFeatures {
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/io.netty.versions.properties"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.mysql.connector)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
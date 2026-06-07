import org.gradle.api.publish.maven.MavenPublication

plugins {
    alias(libs.plugins.android.library)
    `maven-publish`
    signing
}

android {
    namespace = "com.senninseyi.overlay_sdk"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    publishing {
        singleVariant("release")
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.dynamicanimation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = "com.senninseyi"
                artifactId = "overlay-sdk"
                version = "1.0.0"

                from(components["release"])

                pom {
                    name.set("Overlay SDK")
                    description.set("Floating bubble overlay SDK for Android")
                    url.set("https://github.com/senninseyi/overlay-sdk")

                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }

                    developers {
                        developer {
                            id.set("Senninseyi")
                            name.set("Oluwaseyi")
                        }
                    }

                    scm {
                        connection.set(
                            "scm:git:git://github.com/senninseyi/overlay-sdk.git"
                        )
                        developerConnection.set(
                            "scm:git:ssh://github.com/senninseyi/overlay-sdk.git"
                        )
                        url.set("https://github.com/senninseyi/overlay-sdk")
                    }
                }
            }
        }
    }
}
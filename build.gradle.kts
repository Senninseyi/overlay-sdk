
plugins {
    id("com.vanniktech.maven.publish") version "0.34.0"
    alias(libs.plugins.android.library)
    `maven-publish`
    signing
}

val signingKey = System.getenv("SIGNING_KEY")
val signingPassword = System.getenv("SIGNING_PASSWORD")

android {
    namespace = "com.senninseyi.overlay_sdk"
    compileSdk = 34

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

}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.dynamicanimation)
    
    // Image loading with SVG support
    implementation(libs.coil)
    implementation(libs.coil.svg)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

//afterEvaluate {
//    publishing {
//        publications {
//            create<MavenPublication>("release") {
//                groupId = "io.github.senninseyi"
//                artifactId = "overlay-sdk"
//                version = "1.0.0"
//                from(components["release"])
//                pom {
//                    name.set("Overlay SDK")
//                    description.set("Android floating bubble overlay SDK")
//                    url.set("https://github.com/senninseyi/overlay-sdk")
//
//                    licenses {
//                        license {
//                            name.set("MIT License")
//                            url.set("https://opensource.org/licenses/MIT")
//                        }
//                    }
//
//                    developers {
//                        developer {
//                            id.set("senninseyi")
//                            name.set("Oluwaseyi")
//                        }
//                    }
//
//                    scm {
//                        connection.set(
//                            "scm:git:git://github.com/senninseyi/overlay-sdk.git"
//                        )
//                        developerConnection.set(
//                            "scm:git:ssh://github.com/senninseyi/overlay-sdk.git"
//                        )
//                        url.set("https://github.com/senninseyi/overlay-sdk")
//                    }
//                }
//            }
//        }
//    }
//}

signing {
    if (!signingKey.isNullOrBlank() && !signingPassword.isNullOrBlank()) {
        useInMemoryPgpKeys(signingKey.replace("\\n", "\n"), signingPassword)
        sign(publishing.publications)
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates(
        "io.github.senninseyi",
        "overlay-sdk",
        version = System.getenv("VERSION") ?: "1.0.0"
    )
    pom {
        name.set("Overlay SDK")
        description.set("Android floating bubble overlay SDK")
        url.set("https://github.com/senninseyi/overlay-sdk")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        developers {
            developer {
                id.set("senninseyi")
                name.set("Oluwaseyi")
            }
        }

        scm {
            url.set("https://github.com/senninseyi/overlay-sdk")
            connection.set("scm:git:git://github.com/senninseyi/overlay-sdk.git")
            developerConnection.set("scm:git:ssh://github.com/senninseyi/overlay-sdk.git")

        }
    }
}
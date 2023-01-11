import com.android.build.gradle.tasks.factory.AndroidUnitTest
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

plugins {
    kotlin("multiplatform") version "1.6.21"
    id("com.android.library")
    `maven-publish`
    idea
}

group = "net.kodein.demo.crypto"
version = "1.0"

repositories {
    google()
    mavenCentral()
}

kotlin {
    explicitApi()

    android {
        publishAllLibraryVariants()
    }

    fun KotlinNativeTarget.libnexa() {
        compilations["main"].cinterops {
            val libnexa by creating {
                includeDirs.headerFilterOnly(project.file("libnexa/libnexa/include/"))
                tasks[interopProcessingTaskName].dependsOn(":libnexa:buildLibnexaIos")
            }
        }
    }

    ios {
        libnexa()
    }

    iosSimulatorArm64 {
        compilations["main"].defaultSourceSet.dependsOn(sourceSets["iosMain"])
        compilations["test"].defaultSourceSet.dependsOn(sourceSets["iosTest"])

        libnexa()
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
                implementation("androidx.test.ext:junit:1.1.3")
                implementation("androidx.test.espresso:espresso-core:3.4.0")
            }
        }
        val iosMain by getting
        val iosTest by getting

        all {
            languageSettings {
                optIn("kotlin.ExperimentalUnsignedTypes")
            }
        }
    }
}

android {
    compileSdk = 32
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
        targetSdk = 32
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        /*
        testFunctionalTest = true

        javaCompileOptions {
            annotationProcessorOptions {
                arguments.put("room.schemaLocation", "$projectDir/schemas")
            }
        }
        */
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8

    }
    externalNativeBuild {
        cmake {
            // arguments += "-DANDROID_STL=c++_shared"
            path("src/androidMain/cpp/CMakeLists.txt")
        }
    }
}

task<Exec>("generateJniHeaders") {
    group = "build"
    dependsOn("compileDebugKotlinAndroid")

    afterEvaluate {
        commandLine("ls")
        /*  We do not need to autogenerate jni headers
        val output = "$buildDir/nativeHeaders/"
        val compileTask = tasks["compileDebugKotlinAndroid"] as KotlinCompile
        inputs.files(compileTask)
        outputs.dir(output)
        val classPath = compileTask.classpath + compileTask.outputs.files

        val javah = (File("$rootDir/local.properties").takeIf { it.exists() } ?: error("Please create a local.properties file with a javah property (path to the javah binary)"))
            .inputStream().use { Properties().apply { load(it) } }
            .getProperty("javah") ?: error("Please add a javah property in the local.properties file (path to the javah binary)")

        commandLine(javah, "-d", output, "-cp", classPath.joinToString(File.pathSeparator), "net.kodein.demo.crypto.LibnexaJni")
*/
    }
}

afterEvaluate {
    tasks.filter { it.name.startsWith("configureCMake") } .forEach {
        it.dependsOn("generateJniHeaders", ":libnexa:buildLibnexaAndroid")
    }
}

afterEvaluate {
    tasks.withType<AndroidUnitTest>().all {
        enabled = true
    }
}

afterEvaluate {
    tasks.withType<AbstractTestTask> {
        testLogging {
            events("passed", "skipped", "failed", "standard_out", "standard_error")
            showExceptions = true
            showStackTraces = true
        }
    }
}

// Stop android studio from indexing the contrib folder
idea {
    module {
        excludeDirs.add(File("libnexa/nexa"))
        excludeDirs.add(File("/fast/bitcoin/nexa"))  // Painful but the IDE can't deal with symlinks
    }
}
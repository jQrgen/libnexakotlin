import com.android.build.gradle.LibraryExtension
import org.gradle.internal.os.OperatingSystem

val currentOs = OperatingSystem.current()
val bash = if (currentOs.isWindows) "bash.exe" else "bash"

val buildLibnexa by tasks.creating { group = "build" }

val buildLibnexaIos by tasks.creating {
    group = "build"
    buildLibnexa.dependsOn(this)
}

fun creatingBuildLibnexaIos(arch: String) = tasks.creating(Exec::class) {
    group = "build"
    buildLibnexaIos.dependsOn(this)

    onlyIf { currentOs.isMacOsX }

    inputs.files("$projectDir/build-ios.sh", fileTree("$projectDir/libnexa/nexa") {
        include("*.c", "*.h")
        exclude("*-config.h")
    })
    outputs.dir("$projectDir/build/ios/$arch")

    environment("ARCH", arch)
    commandLine(bash, "build-ios.sh")
}

val buildLibnexaIosArm64 by creatingBuildLibnexaIos("arm64")
val buildLibnexaIosArm64Sim by creatingBuildLibnexaIos("arm64-sim")
val buildLibnexaIosX86_64Sim by creatingBuildLibnexaIos("x86_64-sim")


val buildLibnexaAndroid by tasks.creating {
    group = "build"
    buildLibnexa.dependsOn(this)
}

fun creatingBuildLibnexaAndroid(arch: String) = tasks.creating(Exec::class) {
    group = "build"
    buildLibnexaAndroid.dependsOn(this)

    //  onlyIf { currentOs.isLinux }

    inputs.files("build-android.sh", fileTree("$projectDir/libnexa/src") {
        include("*.c", "*.h")
        exclude("*-config.h")
    })
    outputs.dir("$projectDir/build/android/$arch")

    workingDir = projectDir

    val toolchain = when {
        currentOs.isLinux -> "linux-x86_64"
        currentOs.isMacOsX -> "darwin-x86_64"
        currentOs.isWindows -> "windows-x86_64"
        else -> error("No Android toolchain defined for this OS: $currentOs")
    }
    environment("TOOLCHAIN", toolchain)
    environment("ARCH", arch)
    val le = (rootProject.extensions["android"] as LibraryExtension)
    environment("ANDROID_NDK", le.ndkDirectory)
    commandLine(bash, "build-android.sh")
}
val buildLibnexaAndroidX86_64 by creatingBuildLibnexaAndroid("x86_64")
val buildLibnexaAndroidX86 by creatingBuildLibnexaAndroid("x86")
val buildLibnexaAndroidArm64v8a by creatingBuildLibnexaAndroid("arm64-v8a")
val buildLibnexaAndroidArmeabiv7a by creatingBuildLibnexaAndroid("armeabi-v7a")

val clean by tasks.creating {
    group = "build"
    doLast {
        delete("$projectDir/build")
    }
}

# iOS cinterop Kotlin/Native template
The template uses cinterop to bridge C++/C header-code to iOS Kotlin/Native.

There is also an Java Native Interface-Android part to deploy the template using multiplatform.

This template is extracted from [a more comprehensive example](https://github.com/KodeinKoders/Playground-Demo-Crypto) see [the corresponding YouTube talk](https://www.youtube.com/watch?v=Z2PHpxVD9_s). Credits to both the code snippet and the video goes to [Salomon Brys](https://twitter.com/salomonbrys) 
## Folder structure
    ├── secp256k1               # Gradle module for secp256k1 C++/C headers and build tasks
    │   └── secp256k1           # Original C++ implementation with C headers
    ├── src                     # Kotlin Multiplatform Source files
    │   ├── androidMain         # Android target-spesific 
    │   ├── androidTest         # Android target-spesific tests
    │   ├── commonMain          # Common Kotlin code (Shared across all targets)
    │   ├── commonTest          # Common Kotlin tests (Shared across all targets)
    │   ├── iosMain             # Kotlin/Native iOS-target code
    │   ├── iosTest             # Kotlin/Native iOS-target tests
    │   └── nativeInterop       # Kotlin/Native C-interoparability-target (.def file)
    └── build.gradle.kts        # Main Gradle configuration file

## Mac Setup
Install javah (Required by Android JNI bindings. Not relevant for the iOS cinterop template)
`brew install --cask temurin8`

`local.properties`
```
javah=/Library/Java/JavaVirtualMachines/temurin-8.jdk/Contents/Home/bin/javah
```

## Build
`./gradlew build`

## Deploy
Publishes the library to your local Maven repository
`./gradlew publishToMavenLocal`

## Import in a Kotlin Multiplatform app
In a separate Kotlin Multiplatform app library:

#### 1. Add `mavenLocal()` everywhere `mavenCentral()` is located

```kts
repositories {
    // ..
    mavenCentral()
    mavenLocal()
}
```

#### 2. Add library to dependencies

`build.gradle.kts` - conventionally located in the `shared/´-module

```kts
kotlin {
    // ..
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("net.kodein.demo.crypto:DemoCrypto:1.0")
            }
         // ..
        }
    // ..
}
```

#### 3. Import and call library Kotlin/Common 
For example in:
`shared/src/commonMain/TestInterop.kt`
```kotlin
package info.bitcoinunlimited.testimportcinterop
import net.kodein.demo.crypto.*

/**
 * Example
 */

class TestCInterop() {

    init {
        secp256k1()
        println(secp256k1().toString())
        
        val secp256k1 = secp256k1()
        val secKey = ByteArray(32) { it.toByte() }
        val pubKey = secp256k1.createPubKey(secKey)
        val message = "Hello, World!".encodeToByteArray()
        val signature = secp256k1.signMessage(message, secKey)
    }
}
```

package net.kodein.demo.crypto

public class AndroidPlatform : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}

public actual fun getPlatform(): Platform = AndroidPlatform()
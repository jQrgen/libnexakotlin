package net.kodein.demo.crypto

public interface Platform {
    public val name: String
}

public expect fun getPlatform(): Platform
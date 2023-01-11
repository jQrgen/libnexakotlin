package bitcoinunlimited.libbitcoincash

public interface Platform {
    public val name: String
}

public expect fun getPlatform(): Platform
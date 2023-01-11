package bitcoinunlimited.libbitcoincash

private class Hash
{
    companion object
    {
        @JvmStatic
        external fun hash256(data: ByteArray): ByteArray

        @JvmStatic
        external fun sha256(data: ByteArray): ByteArray

        @JvmStatic
        external fun hash160(data: ByteArray): ByteArray
    }
}

private class LibNexaAndroid : LibNexa
{
    override fun hash256(data: ByteArray): ByteArray
    {
        return Hash.hash256(data)
    }
}

private var isInitialized = false

public actual fun libnexa(): LibNexa
{
    if (!isInitialized)
    {
        System.loadLibrary("linknexakotlin")
        isInitialized = true
    }
    return LibNexaAndroid()
}

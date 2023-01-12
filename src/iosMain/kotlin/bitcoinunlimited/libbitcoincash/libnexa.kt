package bitcoinunlimited.libbitcoincash
import libnexa.*

private class LibNexaIos : LibNexa
{
    override fun hash256(data: ByteArray): ByteArray
    {
    }
}

public actual fun libnexa(): LibNexa = LibNexaIos()

package bitcoinunlimited.libbitcoincash
import kotlinx.cinterop.*
import libnexa.*

private class LibNexaIos : LibNexa
{
    val mem = NativeFreeablePlacement()
    override fun hash256(data: ByteArray): ByteArray
    {
        var result:ByteArray

        // val d = nativeHeap.allocArrayOf(data)
        // libnexa.hash256(d., data.size, result)
        return result
    }
}

public actual fun libnexa(): LibNexa = LibNexaIos()

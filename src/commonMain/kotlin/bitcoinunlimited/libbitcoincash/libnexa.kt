package bitcoinunlimited.libbitcoincash

public interface LibNexa
{
    public fun hash256(data: ByteArray): ByteArray
}

public expect fun libnexa(): LibNexa

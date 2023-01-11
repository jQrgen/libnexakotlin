package bitcoinunlimited.libbitcoincash

public fun ByteArray.toHex(): String =
    joinToString("") { it.toUByte().toString(radix = 16).padStart(2, '0') }
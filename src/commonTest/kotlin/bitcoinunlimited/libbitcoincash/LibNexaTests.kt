package bitcoinunlimited.libbitcoincash

import kotlin.test.Test


class LibNexaTests
{
    @Test
    fun verify()
    {
        println("PLATFORM: " + getPlatform().name)
        val libnexa = libnexa()
        val hash = libnexa.hash256(byteArrayOf(1,2))
        check(hash.toHex() == "76a56aced915d2513dcd84c2c378b2e8aa5cd632b5b71ca2f2ac5b0e3a649bdb")
        println(hash.toHex())
    }
}

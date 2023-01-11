package bitcoinunlimited.libbitcoincash

import kotlin.test.Test

class GreetingTest
{
    @Test
    fun greet()
    {
        Greeting().greet()
        println("PLATFORM: " + getPlatform().name)
        println("test run")
        val libnexa = libnexa()

        val hash = libnexa.hash256(byteArrayOf(1,2))
        println(hash.toHex())

    }
}
package io.silv.jikannoto.data.util

import org.junit.Assert
import org.junit.Test

class CryptUnitTest {

    private lateinit var crypto: Crypto

    @Test
    fun encryptDecryptCorrect() {
        crypto = Crypto()

        val key = crypto.generateKey(crypto.aesGCMKeySize)

        val cipherText = crypto.encryptGcm(
            "hello".toByteArray(),
            key = key
        )

        val result = crypto.decryptGcm(cipherText, key)

        Assert.assertEquals("hello", result.decodeToString())
    }
}
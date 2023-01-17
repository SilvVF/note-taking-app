package io.silv.jikannoto.data.util

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlinx.serialization.Serializable

class Crypto {

    /**
     * Be sure to use a SecureRandom!
     */
    private val secureRandom = SecureRandom()

    val aesGCMKeySize = 16 * 8
    /**
     * Generates a key with [sizeInBits] bits.
     */
    fun generateKey(sizeInBits: Int): ByteArray {
        val result = ByteArray(sizeInBits / 8)
        secureRandom.nextBytes(result)
        return result
    }

    /**
     * Generates an IV. The IV is always 128 bit long.
     */
    fun generateIv(): ByteArray {
        val result = ByteArray(128 / 8)
        secureRandom.nextBytes(result)
        return result
    }

    /**
     * Generates a nonce for GCM mode. The nonce is always 96 bit long.
     */
    private fun generateNonce(): ByteArray {
        val result = ByteArray(96 / 8)
        secureRandom.nextBytes(result)
        return result
    }

    @Serializable
    data class Ciphertext(val ciphertext: ByteArray, val iv: ByteArray) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Ciphertext

            if (!ciphertext.contentEquals(other.ciphertext)) return false
            if (!iv.contentEquals(other.iv)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = ciphertext.contentHashCode()
            result = 31 * result + iv.contentHashCode()
            return result
        }
    }

    /**
     * Encrypts the given [plaintext] with the given [key] under AES GCM.
     *
     * This method generates a random nonce.
     *
     * @return Ciphertext and nonce
     */
    fun encryptGcm(plaintext: ByteArray, key: ByteArray): Ciphertext {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val keySpec = SecretKeySpec(key, "AES")

        val nonce = generateNonce()
        val gcmSpec = GCMParameterSpec(128, nonce) // 128 bit authentication tag

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec)

        val ciphertext = cipher.doFinal(plaintext)

        return Ciphertext(ciphertext, nonce)
    }

    /**
     * Decrypts the given [ciphertext] using the given [key] under AES GCM.
     *
     * @return Plaintext
     */
    fun decryptGcm(ciphertext: Ciphertext, key: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val keySpec = SecretKeySpec(key, "AES")

        val gcmSpec = GCMParameterSpec(128, ciphertext.iv) // 128 bit authentication tag

        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec)

        val plaintext = cipher.doFinal(ciphertext.ciphertext)
        return plaintext
    }
}

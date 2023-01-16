package io.silv.jikannoto.data.util

// ref: https://www.baeldung.com/java-aes-encryption-decryption

import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

val algorithm = "AES/CBC/PKCS5Padding"

val key = SecretKeySpec("jsdhfjksdhfjksah".toByteArray(), "AES")

private val iv = IvParameterSpec(ByteArray(16))

fun decrypt(
    algorithm: String,
    cipherText: String,
    key: SecretKeySpec,
    iv: IvParameterSpec = IvParameterSpec(ByteArray(cipherText.toByteArray().size))
): String {
    val cipher = Cipher.getInstance(algorithm)
    cipher.init(Cipher.DECRYPT_MODE, key, iv)
    val plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText))
    return runCatching { plainText.toString() }.getOrDefault("")
}

fun encrypt(
    algorithm: String,
    inputText: String,
    key: SecretKeySpec,
    iv: IvParameterSpec = IvParameterSpec(ByteArray(inputText.toByteArray().size))
): String {
    val cipher = Cipher.getInstance(algorithm)
    cipher.init(Cipher.ENCRYPT_MODE, key, iv)
    val cipherText = cipher.doFinal(inputText.toByteArray())
    return runCatching { Base64.getEncoder().encodeToString(cipherText) }.getOrDefault("")
}

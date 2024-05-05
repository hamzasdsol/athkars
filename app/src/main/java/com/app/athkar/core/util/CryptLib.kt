package com.app.athkar.core.util

import android.util.Base64
import java.io.UnsupportedEncodingException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptLib {
    private val _cx: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

    private val _key: ByteArray = ByteArray(32)
    private val _iv: ByteArray = ByteArray(16)

    /**
     *
     * @param inputText Text to be encrypted or decrypted
     * @param encryptionKey Encryption key to used for encryption / decryption
     * @param initVector Initialization vector
     * @return encrypted or decrypted bytes based on the mode
     * @throws UnsupportedEncodingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    @Throws(
        UnsupportedEncodingException::class,
        InvalidKeyException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    private fun decrypt(
        inputText: String, encryptionKey: String, initVector: String
    ): ByteArray {
        var len = encryptionKey.toByteArray(charset("UTF-8")).size
        if (encryptionKey.toByteArray(charset("UTF-8")).size > _key.size) len = _key.size
        var ivLength = initVector.toByteArray(charset("UTF-8")).size
        if (initVector.toByteArray(charset("UTF-8")).size > _iv.size) ivLength = _iv.size
        System.arraycopy(encryptionKey.toByteArray(charset("UTF-8")), 0, _key, 0, len)
        System.arraycopy(initVector.toByteArray(charset("UTF-8")), 0, _iv, 0, ivLength)
        val keySpec = SecretKeySpec(
            _key,
            "AES"
        )
        val ivSpec =
            IvParameterSpec(_iv)
        _cx.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        val decodedValue = Base64.decode(inputText.toByteArray(), Base64.DEFAULT)
        return _cx.doFinal(decodedValue)
    }

    @Throws(Exception::class)
    fun decryptData(cipherText: String): String {
        val bytes =
            decrypt(cipherText, keySHA256(), generateRandomIV16())
        val decryptedString = String(bytes)
        return decryptedString.substring(16, decryptedString.length)
    }

    /**
     * Generate IV with 16 bytes
     * @return
     */
    private fun generateRandomIV16(): String {
        val ranGen = SecureRandom()
        val aesKey = ByteArray(16)
        ranGen.nextBytes(aesKey)
        val result = StringBuilder()
        for (b in aesKey) {
            result.append(String.format("%02x", b))
        }
        return if (16 > result.toString().length) {
            result.toString()
        } else {
            result.toString().substring(0, 16)
        }
    }

    /***
     * This function computes the SHA256 hash of input string
     * @return returns SHA256 hash of input text
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    @Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class)
    private fun keySHA256(): String {
        val resultString: String
        val md = MessageDigest.getInstance("SHA-256")
        md.update(Constants.ENCRYPTED_KEY.toByteArray(charset("UTF-8")))
        val digest = md.digest()
        val result = StringBuilder()
        for (b in digest) {
            result.append(String.format("%02x", b))
        }
        resultString = if (32 > result.toString().length) {
            result.toString()
        } else {
            result.toString().substring(0, 32)
        }
        return resultString
    }
}
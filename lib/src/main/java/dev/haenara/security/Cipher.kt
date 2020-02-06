package dev.haenara.security

import android.util.Base64
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

const val KEY_SIZE = 16

/**
 * Simple Cipher Interface to encrypt and decrypt text for SharedPreferences.
 */
interface ICipher {
    fun encrypt(plain: String) : String
    fun decrypt(encrypted: String) : String
}

/**
 * Implementation of Simple Cipher Interface to encrypt and decrypt text for Custom EncryptedSharedPreferences.
 */
class BrickCipher private constructor(key: ByteArray) : ICipher {
    private val keySpec : Key = getAESKey(key)
    private val cipher : Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

    companion object {
        @Volatile private var instance: BrickCipher? = null

        @JvmStatic fun getInstance(key: ByteArray): BrickCipher =
            instance ?: synchronized(this) {
                instance ?: BrickCipher(key).also {
                    instance = it
                }
            }
    }

    /**
     * Create AES key with seed bytes.
     */
    private fun getAESKey(key: ByteArray): Key {
        val keySpec: Key
        val keyBytes = ByteArray(KEY_SIZE)

        var len = key.size
        if (len > keyBytes.size) {
            len = keyBytes.size
        }

        System.arraycopy(key, 0, keyBytes, 0, len)
        keySpec = SecretKeySpec(keyBytes, "AES")

        return keySpec
    }


    /**
     * Simple string encryption method using AES algorithm.
     * Encrypt plain string text into encrypted and Base64 encoded string text.
     */
    override fun encrypt(plain: String) = encAES(plain)

    /**
     * Simple string decryption method using AES algorithm.
     * Decrypt encrypted and Base64 encoded string text into plain string text.
     */
    override fun decrypt(encrypted: String) = decAES(encrypted)

    /**
     * encrypt with AES (and encode with Base64)
     * @param str
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun encAES(str: String): String {
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, IvParameterSpec(ByteArray(16)))
        val encrypted = cipher.doFinal(str.toByteArray(Charsets.UTF_8))

        return String(Base64.encode(encrypted, Base64.NO_WRAP))
    }

    /**
     * deencrypt with AES (after decode with Base64)
     * @param enStr
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun decAES(enStr: String): String {
        cipher.init(Cipher.DECRYPT_MODE, keySpec, IvParameterSpec(ByteArray(16)))
        val byteStr = Base64.decode(enStr.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)

        return String(cipher.doFinal(byteStr), Charsets.UTF_8)
    }
}


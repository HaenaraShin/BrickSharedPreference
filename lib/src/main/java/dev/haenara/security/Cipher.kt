package dev.haenara.security

import android.util.Base64
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


interface ICipher {
    fun encrypt(plain: String) : String
    fun decrypt(encrypted: String) : String
}

class BrickCipher private constructor(key: String) : ICipher {
    private val keySpec : Key = getAESKey(key)
    private val cipher : Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

    companion object {
        @Volatile private var instance: BrickCipher? = null

        @JvmStatic fun getInstance(key: String): BrickCipher =
            instance ?: synchronized(this) {
                instance ?: BrickCipher(key).also {
                    instance = it
                }
            }
    }

    fun getAESKey(key: String): Key {
        val keySpec: Key
        val keyBytes = ByteArray(16)
        val b = key.toByteArray(charset("UTF-8"))

        var len = b.size
        if (len > keyBytes.size) {
            len = keyBytes.size
        }

        System.arraycopy(b, 0, keyBytes, 0, len)
        keySpec = SecretKeySpec(keyBytes, "AES")

        return keySpec
    }

    override fun encrypt(plain: String) = encAES(plain)

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
        val encrypted = cipher.doFinal(str.toByteArray(charset("UTF-8")))

        return String(Base64.encode(encrypted, Base64.NO_WRAP))
    }

    /**
     * deencrypt with AES
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


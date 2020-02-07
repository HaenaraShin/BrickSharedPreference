package dev.haenara.bricksharepref

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import dev.haenara.security.BrickCipher
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.random.Random

/**
 * Custom EncrytpedSharedPreferences for under sdk 23.
 * Since AndroidX Security no support sdk under 23, developer should use custom EncryptedSharedPreferences.
 */
class EncryptedSharedPreferenceUnder23 (private val mContext: Context, private val mFile: String) :
    SharedPreferences {

    private val mSharedPreferences =
        mContext.getSharedPreferences("${BRICK_FILE_PREFIX}$mFile", Context.MODE_PRIVATE)
    private val mKey = getKey()
    private val cipher = BrickCipher.getInstance(mKey)

    /**
     * For make AES key, use signature.
     */
    private fun getKey(): ByteArray {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = mContext.packageManager.getPackageInfo(
                mContext.packageName,
                PackageManager.GET_SIGNATURES
            )
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        for (signature in packageInfo!!.signatures) {
            try {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                return md.digest()
            } catch (e: NoSuchAlgorithmException) { }
        }
        return MessageDigest.getInstance("SHA").digest(packageInfo.packageName.toByteArray())
    }

    /**
     * Every encrypted data is actually stored with string regardless of its type.
     */
    private fun get(key: String?): String? {
        return mSharedPreferences.getString(encrypt(key), null)
    }

    /**
     * Returns if this SharedPreferences has a specific value with its key.
     * Since the key is also encrytped, check with an encrypted key.
     */
    override fun contains(key: String?): Boolean {
        return mSharedPreferences.contains(encrypt(key ?: ""))
    }

    /**
     * Get a boolean value
     */
    override fun getBoolean(key: String?, defValue: Boolean)
            = get(key)?.decrypt()?.parse() as Boolean? ?: defValue


    /**
     * Same as normal SharedPreferences.
     */
    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    /**
     * Get an int value
     */
    override fun getInt(key: String?, defValue: Int)
            = get(key)?.decrypt()?.parse() as Int? ?: defValue

    /**
     * Get a set of all data
     */
    override fun getAll(): Map<String, *> {
           return mutableMapOf<String, Any?>().apply {
                mSharedPreferences.all.entries.forEach { entry->
                    if (entry.value is String) {
                        put(entry.key.decrypt(), "${entry.value}".decrypt().parse())
                    }
                }
            }.toMap()
    }

    /**
     * Get Editor to put some data into storage.
     * Same as normal SharedPreferences.
     */
    override fun edit() = Editor()

    /**
     * Get a long value.
     */
    override fun getLong(key: String?, defValue: Long)
            = get(key)?.decrypt()?.parse() as Long? ?: defValue

    /**
     * Get a float value.
     */
    override fun getFloat(key: String?, defValue: Float)
            = get(key)?.decrypt()?.parse() as Float? ?: defValue

    /**
     * Get a string set data.
     */
    override fun getStringSet(key: String?, defValues: MutableSet<String>?) : MutableSet<String>?
            = get(key)?.decrypt()?.parse() as MutableSet<String>? ?: defValues

    /**
     * Same as normal SharedPreferences.
     */
    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        mSharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    /**
     * Get a string value.
     */
    override fun getString(key: String?, defValue: String?)
            = get(key)?.decrypt()?.parse() as String? ?: defValue

    /**
     * Decrypt string text.
     */
    private fun String.decrypt() : String {
         return cipher.decrypt(this)
    }

    /**
     * To configure which type is the value, add a prefix on a data value.
     * With the prefix, you can cast into original data type.
     */
    private fun String.parse() : Any? {
        return when (this[0]) {
            'S' -> substring(5)
            'B' -> substring(5).toBoolean()
            'I' -> substring(5).toInt()
            'L' -> substring(5).toLong()
            'F' -> substring(5).toFloat()
            'T' -> substring(5).toStringSet()
            else -> throw Exception()
        }

    }
    /**
     * Convert a string value into a set
     */
    private fun String.toStringSet() : MutableSet<String> {
        var cnt = 0
        val json = JSONObject(this)
        return mutableSetOf<String>().apply {
            while (json.has("${cnt}")) {
                add(json.getString("${cnt++}"))
            }
        }
    }

    /**
     * Encrypt a string text.
     */
    private fun encrypt(plain: String?) : String {
        return cipher.encrypt(plain ?: "")
    }

    /**
     * Editor is defined by delegation.
     * When save a value, add a prefix shows what type is and 4 digits random integer to make
     * encrypted text would be differ every time.
     * Unless random text is used, every same value has same encrypted text so it could be guessed.
     */
    inner class Editor : SharedPreferences.Editor by mSharedPreferences.edit(){

        /**
         * Put a long value.
         */
        override fun putLong(key: String?, value: Long) = put(key, "L${randomTxt()}$value")

        /**
         * Put an int value
         */
        override fun putInt(key: String?, value: Int) = put(key, "I${randomTxt()}$value")

        /**
         * Put a boolean value
         */
        override fun putBoolean(key: String?, value: Boolean) = put(key, "B${randomTxt()}$value")

        /**
         * Put a string value set.
         */
        override fun putStringSet(
            key: String?,
            values: MutableSet<String>?
        ): SharedPreferences.Editor {
            if (key.isNullOrEmpty() or values.isNullOrEmpty()) {
                return this
            } else {
                // Parsing with JSON.
                val json = JSONObject()
                for ((index, str) in values!!.withIndex()) {
                    json.put("$index", str)
                }
                return put(key, "T${randomTxt()}$json")
            }
        }

        /**
         * Put a float value.
         */
        override fun putFloat(key: String?, value: Float) = put(key, "F${randomTxt()}$value")

        /**
         * Put a string value.
         */
        override fun putString(key: String?, value: String?)
                = put(key, "S${randomTxt()}$value")

        /**
         * Every value is stored with string like AndroidX EncryptedSharedPreferences.
         */
        private fun put(key: String?, value: String) : SharedPreferences.Editor {
            return mSharedPreferences.edit().putString(encrypt(key ?: ""), encrypt(value))
        }

        /**
         * Make 4 digits random integer.
         */
        private fun randomTxt() = String.format("%04d", Random.nextInt(10000))

    }
}

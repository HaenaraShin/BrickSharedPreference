package dev.haenara.bricksharepref

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Base64
import dev.haenara.security.BrickCipher
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.random.Random

class EncryptedSharedPreferenceUnder23 (private val mContext: Context, private val mFile: String) :
    SharedPreferences {

    private val mSharedPreferences = mContext.getSharedPreferences("${BRICK_FILE_PREFIX}$mFile", Context.MODE_PRIVATE)
    private val mKey = getKey()
    private val cipher = BrickCipher.getInstance(mKey)

    private fun getKey(): String {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = mContext.getPackageManager().getPackageInfo(
                mContext.getPackageName(),
                PackageManager.GET_SIGNATURES
            )
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        var key = ""
        for (signature in packageInfo!!.signatures) {
            try {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                key = Base64.encodeToString(md.digest(), Base64.DEFAULT)
            } catch (e: NoSuchAlgorithmException) { }
        }
        return key
    }

    private fun get(key: String?) : String? {
        return mSharedPreferences.getString(key, null)
    }

    override fun contains(key: String?): Boolean {
        return mSharedPreferences.contains(key)
    }

    override fun getBoolean(key: String?, defValue: Boolean)
        = get(key)?.decrypt()?.parse() as Boolean? ?: defValue


    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    override fun getInt(key: String?, defValue: Int)
            = get(key)?.decrypt()?.parse() as Int? ?: defValue

    override fun getAll(): MutableMap<String, *> {
           return mutableMapOf<String, Any?>().apply {
                mSharedPreferences.all.entries.forEach { entry->
                    if (entry.value is String) {
                        put(entry.key.decrypt(), "${entry.value}".decrypt().parse())
                    }
                }
            }
    }

    override fun edit() = Editor()

    override fun getLong(key: String?, defValue: Long)
            = get(key)?.decrypt()?.parse() as Long? ?: defValue

    override fun getFloat(key: String?, defValue: Float)
            = get(key)?.decrypt()?.parse() as Float? ?: defValue

    override fun getStringSet(key: String?, defValues: MutableSet<String>?) : MutableSet<String>?
            = get(key)?.decrypt()?.parse() as MutableSet<String>? ?: defValues

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        mSharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun getString(key: String?, defValue: String?)
            = get(key)?.decrypt().parse() as String? ?: defValue

    private fun String.decrypt() : String {
        return cipher.decrypt(this)
    }

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

    private fun String.toStringSet() : MutableSet<String> {
        var cnt = 0
        val json = JSONObject(this)
        return mutableSetOf<String>().apply {
            while (json.has("${cnt}")) {
                add(json.getString("${cnt++}"))
            }
        }
    }

    inner class Editor : SharedPreferences.Editor by mSharedPreferences.edit(){
        override fun putLong(key: String?, value: Long) = put(key, "L${randomTxt()}$value")

        override fun putInt(key: String?, value: Int) = put(key, "I${randomTxt()}$value")

        override fun putBoolean(key: String?, value: Boolean) = put(key, "B${randomTxt()}$value")

        override fun putStringSet(
            key: String?,
            values: MutableSet<String>?
        ): SharedPreferences.Editor {
            if (key.isNullOrEmpty() or values.isNullOrEmpty()) {
                return this
            } else {
                val json = JSONObject()
                for ((index, str) in values!!.withIndex()) {
                    json.put("$index", str)
                }
                return put(key, "T${randomTxt()}$json")
            }
        }

        override fun putFloat(key: String?, value: Float) = put(key, "F${randomTxt()}$value")

        override fun putString(key: String?, value: String?)
                = put(key, "S${randomTxt()}$value")

        private fun put(key: String?, value: String) : SharedPreferences.Editor {
            return mSharedPreferences.edit().putString(encrypt(key ?: ""), encrypt(value))
        }

        private fun encrypt(plain: String) : String {
            return cipher.encrypt(plain)
        }

        private fun randomTxt() = "${Random.nextInt(10000)}".format("%04s")

    }
}

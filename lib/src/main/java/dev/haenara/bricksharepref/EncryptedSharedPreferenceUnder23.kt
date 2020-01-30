package dev.haenara.bricksharepref

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class EncryptedSharedPreferenceUnder23 (private val mContext: Context, private val mFile: String) :
    SharedPreferences {

    private val mSharedPreferences = mContext.getSharedPreferences("${BRICK_FILE_PREFIX}$mFile", Context.MODE_PRIVATE)
    private val mKey = getKey()

    private fun getKey(): Any {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = mContext.getPackageManager().getPackageInfo(
                mContext.getPackageName(),
                PackageManager.GET_SIGNATURES
            )
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        if (packageInfo == null) Log.e("KeyHash", "KeyHash:null")

        var key = ""
        for (signature in packageInfo!!.signatures) {
            try {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                key = Base64.encodeToString(md.digest(), Base64.DEFAULT)
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            } catch (e: NoSuchAlgorithmException) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=$signature", e)
            }
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
        = get(key)?.decrypt()?.toBoolean() ?: defValue


    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    override fun getInt(key: String?, defValue: Int)
            = get(key)?.decrypt()?.toInt() ?: defValue

    override fun getAll(): MutableMap<String, *> {
           return mutableMapOf<String, Any?>().apply {
                mSharedPreferences.all.entries.forEach { entry->
                    if (entry.value is String) {
                        put(entry.key.decrypt(), "${entry.value}".decrypt())
                    }
                }
            }
    }

    override fun edit() = Editor()

    override fun getLong(key: String?, defValue: Long)
        = get(key)?.decrypt()?.toLong() ?: defValue

    override fun getFloat(key: String?, defValue: Float)
            = get(key)?.decrypt()?.toFloat() ?: defValue

    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String> {
        var json = JSONObject(get(key)?.decrypt())
        val set = mutableSetOf<String>()
        json.keys().forEach {
            set.plus(json[it])
        }
        return set
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        mSharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun getString(key: String?, defValue: String?)
            = get(key)?.decrypt() ?: defValue

    private fun String.decrypt() : String {
        // TODO
        return replace("encrypted_" , "")
    }

    inner class Editor : SharedPreferences.Editor by mSharedPreferences.edit(){
        override fun putLong(key: String?, value: Long) = put(key, "$value")

        override fun putInt(key: String?, value: Int) = put(key, "$value")

        override fun putBoolean(key: String?, value: Boolean) = put(key, "$value")

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
                return put(key, "${json}")
            }
        }

        override fun putFloat(key: String?, value: Float) = put(key, "$value")

        override fun putString(key: String?, value: String?)
                = put(key, "$value")

        private fun put(key: String?, value: String)
                = mSharedPreferences.edit().putString(encrypt(key ?: ""), encrypt(value))

        private fun encrypt(plain: String) : String {
            // TODO
            return "encrypted_$plain"
        }
    }
}

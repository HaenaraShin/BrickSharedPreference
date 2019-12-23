package dev.haenara.bricksharepref

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

const val BRICK_FILE_PREFIX = "brick_"

//class BrickSharedPreferences (private val mContext: Context, private val mFile: String) :
//    SharedPreferences by EncryptedSharedPreferences.create(
//        "$BRICK_FILE_PREFIX$mFile",
//        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
//        mContext,
//        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//    ){
class BrickSharedPreferences (private val mContext: Context, private val mFile: String) :
    SharedPreferences{

    val keySpec = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    val mSharedPreferences = EncryptedSharedPreferences.create(
        "$BRICK_FILE_PREFIX$mFile",
        keySpec,
        mContext,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    val mFileName = "$BRICK_FILE_PREFIX$mFile"

    inner class LegacySharedPreferences
        : SharedPreferences by mContext.getSharedPreferences(mFile, Context.MODE_PRIVATE)

    val legacy = LegacySharedPreferences()

    fun migrateEncryptedSharedPreferences() {
        copyToBrick(getLegacyDatasets())
        clearLegacy()
    }

    private fun getLegacyDatasets() = legacy.all.entries

    private fun copyToBrick(entries: MutableSet<out MutableMap.MutableEntry<String, out Any?>>) {
        entries.forEach {
            when (it.value) {
                is String -> this.edit().putString(it.key, it.value as String).apply()
                is Long -> this.edit().putLong(it.key, it.value as Long).apply()
                is Int -> this.edit().putInt(it.key, it.value as Int).apply()
                is Float -> this.edit().putFloat(it.key, it.value as Float).apply()
                is Boolean -> this.edit().putBoolean(it.key, it.value as Boolean).apply()
            }
        }
    }

    private fun clearLegacy() {
        legacy.all.clear()
    }

    override fun contains(key: String?) = mSharedPreferences.contains(key)

    override fun getBoolean(key: String?, defValue: Boolean) = mSharedPreferences.getBoolean(key, defValue)

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?)
     = mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)

    override fun getInt(key: String?, defValue: Int) = mSharedPreferences.getInt(key, defValue)

    override fun getAll(): MutableMap<String, *> = mSharedPreferences.all

    override fun edit(): SharedPreferences.Editor = mSharedPreferences.edit()

    override fun getLong(key: String?, defValue: Long) = mSharedPreferences.getLong(key, defValue)

    override fun getFloat(key: String?, defValue: Float) = mSharedPreferences.getFloat(key, defValue)

    override fun getStringSet(key: String?, defValues: MutableSet<String>?) = mSharedPreferences.getStringSet(key, defValues)

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?)
    = mSharedPreferences.registerOnSharedPreferenceChangeListener(listener)

    override fun getString(key: String?, defValue: String?) = mSharedPreferences.getString(key, defValue)

}
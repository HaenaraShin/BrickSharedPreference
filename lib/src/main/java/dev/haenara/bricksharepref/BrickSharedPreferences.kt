package dev.haenara.bricksharepref

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

/**
 * For safe, BrickSharedPreferences does not use same name with legacy SharedPreferences.
 * Simply add prefix 'brick_' at front of legacy SharedPreferences's name
 */
const val BRICK_FILE_PREFIX = "brick_"

/**
 * With extension function, BrickSharedPreferences helps you to change legacy codes.
 */
fun Context.getBrickSharedPreferences(fileName: String, type: Int = 0) = BrickSharedPreferences(this, fileName)

/**
 * BrickSharedPreferences helps you to migrate legacy SharedPreferences to EncryptedSharedPreferences.
 */
class BrickSharedPreferences (private val mContext: Context, private val mFile: String) :
        SharedPreferences by getInstance(mFile, mContext) {

    val mFileName = "$BRICK_FILE_PREFIX$mFile"

    /**
     * Migrate legacy SharedPreferences to EncryptedSharedPreferences.
     * Copy all legacy data and encrypt it.
     */
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
                is Set<*> -> this.edit().putStringSet(it.key, it.value as Set<String>).apply()
            }
        }
    }

    /**
     * Get rid of all datas from legacy SharedPreferences.
     */
    private fun clearLegacy() {
        legacy.edit().clear().apply()
    }

    /**
     * If you need to use a legacy SharedPreferences, simply can access it as follows.
     */
    inner class LegacySharedPreferences
        : SharedPreferences by mContext.getSharedPreferences(mFile, Context.MODE_PRIVATE)

    val legacy = LegacySharedPreferences()

    /**
     * A factory to return an EncryptedSharedPreferences instance regard of sdk version.
     * Over api 23 would get an AndroidX EncryptedSharedPreferences instance.
     * Under api 23 would get a custom EncryptedSharedPreferences instance.
     */
    companion object SharePreferencesFactory {
        private fun getInstance(file: String, context: Context) : SharedPreferences {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Use Jetpack (minSdk 23)
                EncryptedSharedPreferences.create(
                    "$BRICK_FILE_PREFIX$file",
                    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
            } else {
                // Without Jetpack (minSdk 19)
                EncryptedSharedPreferenceUnder23(context, file)
            }
        }
    }
}
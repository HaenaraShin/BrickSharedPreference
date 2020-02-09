package dev.haenara.bricksharepref

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

/**
 * Custom EncrytpedSharedPreferences for under sdk 23.
 * Since AndroidX Security no support sdk under 23, developer should use custom EncryptedSharedPreferences.
 */
class EncryptedSharedPreferenceOver23 (private val mContext: Context, file: String) :
    SharedPreferences by EncryptedSharedPreferences.create(
            "$BRICK_FILE_PREFIX$file",
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            mContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

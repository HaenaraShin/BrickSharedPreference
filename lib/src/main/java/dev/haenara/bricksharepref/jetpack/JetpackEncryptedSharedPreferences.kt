package dev.haenara.bricksharepref.jetpack

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dev.haenara.bricksharepref.BRICK_FILE_PREFIX

@RequiresApi(api = Build.VERSION_CODES.M)
object JetpackEncryptedSharedPreferences {
    fun create(file: String, context: Context) : SharedPreferences {
        return EncryptedSharedPreferences.create(
            "$BRICK_FILE_PREFIX$file",
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    }
}
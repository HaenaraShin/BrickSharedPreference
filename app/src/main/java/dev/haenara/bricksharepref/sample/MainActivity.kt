package dev.haenara.bricksharepref.sample

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import dev.haenara.bricksharepref.BrickSharedPreferences
import dev.haenara.bricksharepref.getBrickSharedPreferences
import kotlinx.android.synthetic.main.activity_main.*

const val KEY_STRING = "LegacyString"
const val KEY_INT = "LegacyInt"
const val KEY_BOOLEAN = "LegacyBoolean"
const val KEY_FlOAT = "LegacyFloat"
const val KEY_LONG = "LegacyLong"

class MainActivity : AppCompatActivity() {
    val mFileName = "sample"
    lateinit var mSharedPreferences : BrickSharedPreferences
    val textViews by lazy { arrayOf(tv_data1, tv_data2, tv_data3, tv_data4, tv_data5)}
    val encTextViews by lazy { arrayOf(tv_enc_data1, tv_enc_data2, tv_enc_data3, tv_enc_data4, tv_enc_data5)}

    /**
     * This code shows how to migrate legacy SharedPreferences to EncryptedSharedPreferences.
     */
    private fun encryptData() {
        mSharedPreferences!!.migrateEncryptedSharedPreferences()
        refreshData()
    }

    /**
     * This shows how to get a decrypted value from EncryptedSharedPreferences
     */
    private fun decryptData() {
        Log.d("BRICK", "decrypt.")
        tv_enc_filename.text = "${mSharedPreferences.mFileName}.xml"
        encTextViews.forEach { it.text = "" }
        for (entry in mSharedPreferences.all.entries) {
            for (tv in encTextViews) {
                if (tv.text.isEmpty()) {
                    tv.text = "${entry.key} : ${entry.value}"
                    break
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // This code shows how to get BrickSharedPreferences.
        // You also can get like BrickSharedPreferences(context, fileName)
        mSharedPreferences = getBrickSharedPreferences(mFileName, Context.MODE_PRIVATE)

        btn_encrypt.setOnClickListener { encryptData() }
        btn_decrypt.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> decryptData()
                MotionEvent.ACTION_UP -> refreshData()
            }
            false
        }
        setSampleLegacyData()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun setSampleLegacyData() {
        mSharedPreferences!!.legacy.edit().let{
            it.putString(KEY_STRING, "SampleData is Plain text.").apply()
            it.putInt(KEY_INT, 12345).apply()
            it.putBoolean(KEY_BOOLEAN, true).apply()
            it.putFloat(KEY_FlOAT, 12345.678F).apply()
            it.putLong(KEY_LONG, 123456789L).apply()
        }
    }

    private fun refreshData() {
        tv_filename.text = "$mFileName.xml"
        textViews.forEach { it.text = "" }
        for (entry in mSharedPreferences!!.legacy.all.entries) {
            for (tv in textViews) {
                if (tv.text.isEmpty()) {
                    tv.text = "${entry.key} : ${entry.value}"
                    break
                }
            }
        }

        tv_enc_filename.text = "${mSharedPreferences.mFileName}.xml"
        encTextViews.forEach { it.text = "" }
        for (entry in getSharedPreferences(mSharedPreferences.mFileName, Context.MODE_PRIVATE).all.entries) {
            for (tv in encTextViews) {
                if (tv.text.isEmpty() and
                    entry.key.startsWith("__androidx_security_crypto_encrypted_prefs_").not()) {
                    tv.text = "${entry.key} : ${entry.value}"
                    break
                }
            }
        }
    }
}

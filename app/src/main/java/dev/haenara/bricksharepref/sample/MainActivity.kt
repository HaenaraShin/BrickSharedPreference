package dev.haenara.bricksharepref.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import dev.haenara.bricksharepref.BrickSharedPreferences
import kotlinx.android.synthetic.main.activity_main.*

const val KEY_STRING = "LegacyString"
const val KEY_INT = "LegacyInt"
const val KEY_BOOLEAN = "LegacyBoolean"
const val KEY_FlOAT = "LegacyFloat"
const val KEY_LONG = "LegacyLong"

class MainActivity : AppCompatActivity() {
    val mFileName = "sample"
    val mSharedPreferences = SampleApplication.sharedPreferences
    val textViews = arrayOf(tv_data1, tv_data2, tv_data3, tv_data4, tv_data5)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_encrypt.setOnClickListener { encryptData() }
        btn_decrypt.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_BUTTON_PRESS -> decryptData()
                MotionEvent.ACTION_BUTTON_RELEASE -> refreshData()
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

    fun refreshData() {
        textViews.forEach { it.text = "" }
        for (entry in mSharedPreferences!!.legacy.all.entries) {
            textViews.forEach {
                if (it.text.isEmpty()) { it.text = "${entry.key} : ${entry.value}" }
            }
        }
    }

    fun decryptData() {
        Log.d("BRICK", "decrypt.")
    }

    fun encryptData() {
        mSharedPreferences!!.migrateEncryptedSharedPreferences()
        refreshData()
    }
}

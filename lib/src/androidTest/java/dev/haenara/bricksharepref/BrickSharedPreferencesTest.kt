package dev.haenara.bricksharepref

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class BrickSharedPreferencesTest {
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    val fileName = "test"
    val spBrick = BrickSharedPreferences(appContext, fileName)
    val spLegacy = appContext.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    val spBrickWithLegacy = appContext.getSharedPreferences("brick_$fileName", Context.MODE_PRIVATE)

    val KEY_STRING = "KEY_STRING"
    val VALUE_STRING = "Test String"
    val KEY_BOOL = "KEY_BOOL"
    val KEY_INT = "KEY_INT"
    val KEY_FLOAT = "KEY_FLOAT"
    val KEY_STRING_SET = "KEY_STRING_SET"
    val KEY_LONG = "KEY_LONG"
    val VALUE_BOOL = true
    val VALUE_INT = 12345
    val VALUE_FLOAT = 123.45f
    val VALUE_STRING_SET = mutableSetOf<String>("set1", "set2", "set3")
    val VALUE_LONG = 12345L

    @Before
    fun setupTest(){
    }

    private fun SharedPreferences.putDummyData() {
        edit().putString(KEY_STRING, VALUE_STRING).apply()
        edit().putBoolean(KEY_BOOL, VALUE_BOOL).apply()
        edit().putInt(KEY_INT, VALUE_INT).apply()
        edit().putFloat(KEY_FLOAT, VALUE_FLOAT).apply()
        edit().putStringSet(KEY_STRING_SET, VALUE_STRING_SET).apply()
        edit().putLong(KEY_LONG, VALUE_LONG).apply()
    }

    @After
    fun resetTest(){
        spBrick.edit().clear().commit()
        spLegacy.edit().clear().apply()
    }


    @Test
    fun getMFileName() {
        val sampleFileName = "sample"
        assertEquals(
                "brick_sample",
                BrickSharedPreferences(appContext, sampleFileName).mFileName
        )
    }

    @Test
    fun migrateEncryptedSharedPreferences() {
        val legacy = appContext.getSharedPreferences(fileName, Context.MODE_PRIVATE).apply {
            putDummyData()
        }
        assertTrue(legacy.all.isNotEmpty())
        val dataLegacy = legacy.all
        val brick = BrickSharedPreferences(appContext, fileName).apply {
            migrateEncryptedSharedPreferences()
        }
        val dataBrick = brick.all
        assertTrue(legacy.all.isEmpty())
        assertTrue(dataLegacy == dataBrick)
    }

    @Test
    fun getLegacy() {

    }

    @Test
    fun contains() {
        assertEquals(spLegacy.contains(KEY_STRING), spBrick.contains(KEY_STRING))

        val KEY_NOTHING = "NONE"
        assertEquals(spLegacy.contains(KEY_NOTHING), spBrick.contains(KEY_NOTHING))
    }

    @Test
    fun getBoolean() {
        assertEquals(spLegacy.getBoolean(KEY_BOOL, false), spBrick.getBoolean(KEY_BOOL, false))
    }

    @Test
    fun unregisterOnSharedPreferenceChangeListener() {
    }

    @Test
    fun getInt() {
        spBrick.putDummyData()
        spLegacy.putDummyData()
        assertEquals(spLegacy.getInt(KEY_INT, 0), spBrick.getInt(KEY_INT, -1))
    }

    @Test
    fun getAll() {
        spBrick.putDummyData()
        spLegacy.putDummyData()
        assertTrue(spLegacy.all == spBrick.all)
    }

    @Test
    fun putString() {
        resetTest()
        assertEquals(null, spBrick.getString(KEY_STRING, null))
        spBrick.edit().putString(KEY_STRING, VALUE_STRING).apply()
        assertEquals(VALUE_STRING, spBrick.getString(KEY_STRING, null))
        assertFalse(spBrickWithLegacy.contains(KEY_STRING))
    }

    @Test
    fun putBoolean() {
        resetTest()
        assertEquals(false, spBrick.getBoolean(KEY_BOOL, false))
        spBrick.edit().putBoolean(KEY_BOOL, VALUE_BOOL).apply()
        assertEquals(VALUE_BOOL, spBrick.getBoolean(KEY_BOOL, false))
        assertFalse(spBrickWithLegacy.contains(KEY_BOOL))
    }

    @Test
    fun putInt() {
        resetTest()
        assertEquals(0, spBrick.getInt(KEY_INT, 0))
        spBrick.edit().putInt(KEY_INT, VALUE_INT).apply()
        assertEquals(VALUE_INT, spBrick.getInt(KEY_INT, 0))
        assertFalse(spBrickWithLegacy.contains(KEY_INT))
    }

    @Test
    fun putLong() {
        resetTest()
        assertEquals(0L, spBrick.getLong(KEY_LONG, 0L))
        spBrick.edit().putLong(KEY_LONG, VALUE_LONG).apply()
        assertEquals(VALUE_LONG, spBrick.getLong(KEY_LONG, 0L))
        assertFalse(spBrickWithLegacy.contains(KEY_LONG))
    }

    @Test
    fun putFloat() {
        resetTest()
        assertEquals(0F, spBrick.getFloat(KEY_FLOAT, 0F))
        spBrick.edit().putFloat(KEY_FLOAT, VALUE_FLOAT).apply()
        assertEquals(VALUE_FLOAT, spBrick.getFloat(KEY_FLOAT, 0F))
        assertFalse(spBrickWithLegacy.contains(KEY_FLOAT))
    }

    @Test
    fun putStringSet() {
        resetTest()
        assertEquals(mutableSetOf<String>(), spBrick.getStringSet(KEY_STRING_SET, mutableSetOf()))
        spBrick.edit().putStringSet(KEY_STRING_SET, VALUE_STRING_SET).apply()
        assertEquals(VALUE_STRING_SET, spBrick.getStringSet(KEY_STRING_SET, mutableSetOf()))
        assertFalse(spBrickWithLegacy.contains(KEY_STRING_SET))
    }

    @Test
    fun getLong() {
        spBrick.putDummyData()
        spLegacy.putDummyData()
        assertEquals(spLegacy.getLong(KEY_LONG, 0L), spBrick.getLong(KEY_LONG, -1L))
    }

    @Test
    fun getFloat() {
        spBrick.putDummyData()
        spLegacy.putDummyData()
        assertEquals(spLegacy.getFloat(KEY_FLOAT, 0f), spBrick.getFloat(KEY_FLOAT, -1f))

    }

    @Test
    fun getStringSet() {
        spBrick.putDummyData()
        spLegacy.putDummyData()
        assertTrue(spLegacy.getStringSet(KEY_STRING_SET, mutableSetOf("")) == spBrick.getStringSet(KEY_STRING_SET, mutableSetOf()))
    }

    @Test
    fun registerOnSharedPreferenceChangeListener() {
    }

    @Test
    fun getString() {
        spBrick.putDummyData()
        spLegacy.putDummyData()
        assertEquals(spLegacy.getString(KEY_STRING, ""), spBrick.getString(KEY_STRING, null))
    }
}
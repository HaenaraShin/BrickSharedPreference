package dev.haenara.bricksharepref

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class EncryptedSharedPreferenceUnder23Test {
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    val fileEncrypt = "test"
    val fileJetpack = "jetpack"
    val fileLegacy = "legacy"
    val spEncrypt = EncryptedSharedPreferenceUnder23(appContext, fileEncrypt)
    val spJetpack = EncryptedSharedPreferences.create(
        fileJetpack,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        appContext,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    val spLegacy = appContext.getSharedPreferences(fileLegacy, Context.MODE_PRIVATE)
    val spEncryptWithLegacy = appContext.getSharedPreferences(fileEncrypt, Context.MODE_PRIVATE)

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
        putDummyData(spEncrypt, spJetpack, spLegacy)
    }

    private fun putDummyData(vararg sp: SharedPreferences) {
        sp.forEach {
            it.edit().putString(KEY_STRING, VALUE_STRING).apply()
            it.edit().putBoolean(KEY_BOOL, VALUE_BOOL).apply()
            it.edit().putInt(KEY_INT, VALUE_INT).apply()
            it.edit().putFloat(KEY_FLOAT, VALUE_FLOAT).apply()
            it.edit().putStringSet(KEY_STRING_SET, VALUE_STRING_SET).apply()
            it.edit().putLong(KEY_LONG, VALUE_LONG).apply()
        }
    }

    @After
    fun resetTest(){
        spEncrypt.edit().clear().apply()
        spJetpack.edit().clear().commit()
        spLegacy.edit().clear().apply()
    }

    @Test
    fun contains() {
        assertEquals(spJetpack.contains(KEY_STRING), spEncrypt.contains(KEY_STRING))
        assertEquals(spLegacy.contains(KEY_STRING), spEncrypt.contains(KEY_STRING))

        val KEY_NOTHING = "NONE"
        assertEquals(spJetpack.contains(KEY_NOTHING), spEncrypt.contains(KEY_NOTHING))
        assertEquals(spLegacy.contains(KEY_NOTHING), spEncrypt.contains(KEY_NOTHING))
    }

    @Test
    fun getBoolean() {
        assertEquals(spJetpack.getBoolean(KEY_BOOL, false), spEncrypt.getBoolean(KEY_BOOL, false))
        assertEquals(spLegacy.getBoolean(KEY_BOOL, false), spEncrypt.getBoolean(KEY_BOOL, false))
    }

    @Test
    fun unregisterOnSharedPreferenceChangeListener() {
    }

    @Test
    fun getInt() {
        assertEquals(spJetpack.getInt(KEY_INT, 0), spEncrypt.getInt(KEY_INT, -1))
        assertEquals(spLegacy.getInt(KEY_INT, 0), spEncrypt.getInt(KEY_INT, -1))
    }

    @Test
    fun getAll() {
        assertTrue(spJetpack.all == spEncrypt.all)
        assertTrue(spLegacy.all == spEncrypt.all)
    }

    @Test
    fun putString() {
        resetTest()
        assertEquals(null, spEncrypt.getString(KEY_STRING, null))
        spEncrypt.edit().putString(KEY_STRING, VALUE_STRING).apply()
        assertEquals(VALUE_STRING, spEncrypt.getString(KEY_STRING, null))
        assertFalse(spEncryptWithLegacy.contains(KEY_STRING))
    }

    @Test
    fun putBoolean() {
        resetTest()
        assertEquals(false, spEncrypt.getBoolean(KEY_BOOL, false))
        spEncrypt.edit().putBoolean(KEY_BOOL, VALUE_BOOL).apply()
        assertEquals(VALUE_BOOL, spEncrypt.getBoolean(KEY_BOOL, false))
        assertFalse(spEncryptWithLegacy.contains(KEY_BOOL))
    }

    @Test
    fun putInt() {
        resetTest()
        assertEquals(0, spEncrypt.getInt(KEY_INT, 0))
        spEncrypt.edit().putInt(KEY_INT, VALUE_INT).apply()
        assertEquals(VALUE_INT, spEncrypt.getInt(KEY_INT, 0))
        assertFalse(spEncryptWithLegacy.contains(KEY_INT))
    }

    @Test
    fun putLong() {
        resetTest()
        assertEquals(0L, spEncrypt.getLong(KEY_LONG, 0L))
        spEncrypt.edit().putLong(KEY_LONG, VALUE_LONG).apply()
        assertEquals(VALUE_LONG, spEncrypt.getLong(KEY_LONG, 0L))
        assertFalse(spEncryptWithLegacy.contains(KEY_LONG))
    }

    @Test
    fun putFloat() {
        resetTest()
        assertEquals(0F, spEncrypt.getFloat(KEY_FLOAT, 0F))
        spEncrypt.edit().putFloat(KEY_FLOAT, VALUE_FLOAT).apply()
        assertEquals(VALUE_FLOAT, spEncrypt.getFloat(KEY_FLOAT, 0F))
        assertFalse(spEncryptWithLegacy.contains(KEY_FLOAT))
    }

    @Test
    fun putStringSet() {
        resetTest()
        assertEquals(mutableSetOf<String>(), spEncrypt.getStringSet(KEY_STRING_SET, mutableSetOf()))
        spEncrypt.edit().putStringSet(KEY_STRING_SET, VALUE_STRING_SET).apply()
        assertEquals(VALUE_STRING_SET, spEncrypt.getStringSet(KEY_STRING_SET, mutableSetOf()))
        assertFalse(spEncryptWithLegacy.contains(KEY_STRING_SET))
    }

    @Test
    fun getLong() {
        assertEquals(spJetpack.getLong(KEY_LONG, 0L), spEncrypt.getLong(KEY_LONG, -1L))
        assertEquals(spLegacy.getLong(KEY_LONG, 0L), spEncrypt.getLong(KEY_LONG, -1L))
    }

    @Test
    fun getFloat() {
        assertEquals(spJetpack.getFloat(KEY_FLOAT, 0f), spEncrypt.getFloat(KEY_FLOAT, -1f))
        assertEquals(spLegacy.getFloat(KEY_FLOAT, 0f), spEncrypt.getFloat(KEY_FLOAT, -1f))

    }

    @Test
    fun getStringSet() {
        assertTrue(spJetpack.getStringSet(KEY_STRING_SET, mutableSetOf("")) == spEncrypt.getStringSet(KEY_STRING_SET, mutableSetOf()))
        assertTrue(spLegacy.getStringSet(KEY_STRING_SET, mutableSetOf("")) == spEncrypt.getStringSet(KEY_STRING_SET, mutableSetOf()))
    }

    @Test
    fun registerOnSharedPreferenceChangeListener() {
    }

    @Test
    fun getString() {
        assertEquals(spJetpack.getString(KEY_STRING, ""), spEncrypt.getString(KEY_STRING, null))
        assertEquals(spLegacy.getString(KEY_STRING, ""), spEncrypt.getString(KEY_STRING, null))
    }
}
package dev.haenara.bricksharepref.sample

import android.app.Application
import dev.haenara.bricksharepref.BrickSharedPreferences

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = BrickSharedPreferences(this, "sample")
    }

    companion object BrickSharedPref {
        var sharedPreferences = null as BrickSharedPreferences?
    }
}
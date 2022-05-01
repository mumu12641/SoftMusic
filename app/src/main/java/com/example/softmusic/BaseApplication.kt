package com.example.softmusic

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import com.google.android.material.color.DynamicColors
import com.tencent.mmkv.MMKV




class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        context = applicationContext
        val rootDir = MMKV.initialize(this)
        Log.d("Application", "onCreate: mmkv root: $rootDir")
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}
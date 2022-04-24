package com.example.softmusic

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.google.android.material.color.DynamicColors

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        context = applicationContext
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}
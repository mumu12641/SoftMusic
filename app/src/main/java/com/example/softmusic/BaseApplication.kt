package com.example.softmusic

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.softmusic.entity.MusicSongList
import com.example.softmusic.room.DataBaseUtils
import com.google.android.material.color.DynamicColors
import com.permissionx.guolindev.PermissionX
import com.tencent.mmkv.MMKV




class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        context = applicationContext
        val rootDir = MMKV.initialize(this)
        Log.d("Application", "onCreate: mmkv root: $rootDir")
        DataBaseUtils.insertMusicSongList(
            MusicSongList(
            "本地音乐", "5/2/22", 0, "me", "local music", "none")
        )

    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}
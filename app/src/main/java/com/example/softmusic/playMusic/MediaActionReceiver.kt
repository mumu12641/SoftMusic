package com.example.softmusic.playMusic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaControllerCompat
import android.util.Log

class MediaActionReceiver :BroadcastReceiver() {
    private val TAG = "MediaActionReceiver"
    override fun onReceive(context: Context?, intent: Intent?) {
        val action: String? = intent?.action
//        onReceive: android.intent.action.MEDIA_BUTTON
        Log.d(TAG, "onReceive: $action")
    }
}
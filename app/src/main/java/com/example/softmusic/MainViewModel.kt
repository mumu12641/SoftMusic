package com.example.softmusic

import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.softmusic.room.DataBaseUtils

class MainViewModel:ViewModel(){

    val allPlayListSongsCrossRef = DataBaseUtils.getAllPlaylistSongCrossRef()

    val nowProcess:MutableLiveData<Int> = MutableLiveData(0)

    val duration : MutableLiveData<Int> = MutableLiveData()

    val lastProcess : MutableLiveData<Int> = MutableLiveData(-1)

    var autoChangeFlag = false

    val initFlag:MutableLiveData<Boolean> = MutableLiveData(false)

    val nowId:MutableLiveData<List<Long>> = MutableLiveData()

    val nowTitle:MutableLiveData<String> = MutableLiveData()

    val nowMusicRecordImageList = MutableLiveData<List<String>>()

    var haveMusicFlag = false

    val nowImageUri:MutableLiveData<String> = MutableLiveData()

    val likeFlag:MutableLiveData<Boolean> = MutableLiveData(false)

    // now playState
    val playbackState by lazy { MutableLiveData(EMPTY_PLAYBACK_STATE) }


    @Suppress("PropertyName")
    val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
        .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
        .build()

}
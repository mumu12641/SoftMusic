package com.example.softmusic

import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.softmusic.entity.MusicSong
import com.example.softmusic.playMusic.MediaPlaybackService
import com.example.softmusic.room.DataBaseUtils

class MainViewModel : ViewModel() {

    val allPlayListSongsCrossRef = DataBaseUtils.getAllPlaylistSongCrossRef()

    val currentProgress: MutableLiveData<Int> = MutableLiveData(0)

    val duration: MutableLiveData<Int> = MutableLiveData()

    val lastProgress: MutableLiveData<Int> = MutableLiveData(-1)

    var autoChangeFlag = false

    val initFlag: MutableLiveData<Boolean> = MutableLiveData(false)

    val currentId: MutableLiveData<List<Long>> = MutableLiveData()

    val currentMusicId:MutableLiveData<Long> = MutableLiveData()

    val currentTitle: MutableLiveData<String> = MutableLiveData()

    val currentArtist: MutableLiveData<String> = MutableLiveData()

    val currentPlayMode:MutableLiveData<Int> = MutableLiveData(MediaPlaybackService.DEFAULT)

    val nowPlayList = MutableLiveData<List<MusicSong>>()

    val currentPlayList = MutableLiveData<List<MusicSong>>()

    val rawPlayList = MutableLiveData<List<MusicSong>>()

    var haveMusicFlag = false

    val currentImageUri: MutableLiveData<String> = MutableLiveData()

    val likeFlag: MutableLiveData<Boolean> = MutableLiveData(false)

    val playbackState by lazy { MutableLiveData(EMPTY_PLAYBACK_STATE) }


    @Suppress("PropertyName")
    val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
        .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
        .build()

}
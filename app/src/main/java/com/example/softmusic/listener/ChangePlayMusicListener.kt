package com.example.softmusic.listener

import com.example.softmusic.entity.MusicSong

interface ChangePlayMusicListener {
    fun changePlayMusic(musicSongId:Long,musicSongListId: Long)

    fun changePlayMusicByEntity(song:MusicSong)
}
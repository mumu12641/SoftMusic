package com.example.softmusic.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MusicSong(

    var songTitle: String,
    var songSinger: String,
    var songAlbum: String,
    var mediaFileUri: String,
    var duration:Int,
    var albumId:Long
){
    @PrimaryKey(autoGenerate = true) var musicSongId:Long  = 0
}
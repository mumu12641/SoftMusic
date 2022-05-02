package com.example.softmusic.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MusicSongList(

    var songListTitle: String,
    var createDate: String,
    var songNumber: Int,
    var builder: String,
    var description: String,
    var imageFileUri: String
){
    @PrimaryKey(autoGenerate = true) var musicSongListId:Long  = 0
}
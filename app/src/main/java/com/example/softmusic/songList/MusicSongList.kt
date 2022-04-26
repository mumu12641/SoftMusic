package com.example.softmusic.songList

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.softmusic.musicSong.MusicSong

@Entity(tableName = "musicSongList")
class MusicSongList(
    @field:PrimaryKey var songListTitle: String,
    @field:ColumnInfo(name = "createDate") var createDate: String,
    @field:ColumnInfo(
        name = "songNumber"
    ) var songNumber: Int,
    @field:ColumnInfo(name = "builder") var builder: String,
    @field:ColumnInfo(name = "description") var description: String,
    @field:ColumnInfo(name = "imageFileUri") var imageFileUri: String
)
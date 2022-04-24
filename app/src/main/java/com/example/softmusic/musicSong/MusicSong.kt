package com.example.softmusic.musicSong

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "musicSong")
class MusicSong(
    @field:PrimaryKey var songTitle: String,
    @field:ColumnInfo(
        name = "songSinger"
    ) var songSinger: String,
    @field:ColumnInfo(name = "songAlbum") var songAlbum: String,
    @field:ColumnInfo(
        name = "mediaFileUri"
    ) var mediaFileUri: String
)
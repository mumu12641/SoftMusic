package com.example.softmusic.room

import androidx.room.Entity

@Entity(primaryKeys = ["songListTitle", "songTitle"], tableName = "PlaylistSongCrossRef")
class PlaylistSongCrossRef(var songListTitle: String, var songTitle: String)
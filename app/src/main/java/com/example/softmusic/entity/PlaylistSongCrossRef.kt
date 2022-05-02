package com.example.softmusic.entity

import androidx.room.Entity

@Entity(primaryKeys = ["musicSongListId", "musicSongId"])
data class PlaylistSongCrossRef(val musicSongListId: Long, val musicSongId: Long)
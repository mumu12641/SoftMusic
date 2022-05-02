package com.example.softmusic.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PlaylistWithSongs (
    @Embedded
    val musicSongList:MusicSongList,

    @Relation(
        parentColumn = "musicSongListId", entityColumn = "musicSongId", associateBy = Junction(
            PlaylistSongCrossRef::class
        )
    )
    val songs: List<MusicSong>
)
package com.example.softmusic.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class SongWithPlaylists (
    @Embedded
    val song: MusicSong,

    @Relation(
        parentColumn = "musicSongId", entityColumn = "musicSongListId", associateBy = Junction(
            PlaylistSongCrossRef::class
        )
    )
    val playlists: List<MusicSongList>
)
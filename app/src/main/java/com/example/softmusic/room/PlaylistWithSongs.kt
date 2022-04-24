package com.example.softmusic.room

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.softmusic.musicSong.MusicSong
import com.example.softmusic.songList.MusicSongList

class PlaylistWithSongs {
    @Embedded
    var playlist: MusicSongList? = null

    @Relation(
        parentColumn = "songListTitle", entityColumn = "songTitle", associateBy = Junction(
            PlaylistSongCrossRef::class
        )
    )
    var songs: List<MusicSong>? = null
}
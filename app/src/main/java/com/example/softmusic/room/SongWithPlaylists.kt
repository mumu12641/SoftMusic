package com.example.softmusic.room

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.softmusic.musicSong.MusicSong
import com.example.softmusic.songList.MusicSongList

class SongWithPlaylists {
    @Embedded
    var song: MusicSong? = null

    @Relation(
        parentColumn = "songTitle", entityColumn = "songListTitle", associateBy = Junction(
            PlaylistSongCrossRef::class
        )
    )
    var playlists: List<MusicSongList>? = null
}
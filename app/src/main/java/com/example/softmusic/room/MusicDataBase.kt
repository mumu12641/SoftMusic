package com.example.softmusic.room

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.softmusic.musicSong.MusicSong
import com.example.softmusic.songList.MusicSongList

@Database(
    entities = [MusicSongList::class, MusicSong::class, PlaylistSongCrossRef::class],
    version = 1,
    exportSchema = false
)
abstract class MusicDataBase : RoomDatabase() {
    abstract val musicDao: MusicDao

    companion object {
        private lateinit var musicDataBase: MusicDataBase

        @Synchronized
        fun getInstance(context: Context?): MusicDataBase {
            return musicDataBase
        }
    }
}
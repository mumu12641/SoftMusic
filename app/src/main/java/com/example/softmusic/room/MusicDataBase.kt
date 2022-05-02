package com.example.softmusic.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.softmusic.BaseApplication
import com.example.softmusic.entity.MusicSong
import com.example.softmusic.entity.MusicSongList
import com.example.softmusic.entity.PlaylistSongCrossRef

@Database(
    entities = [MusicSongList::class, MusicSong::class, PlaylistSongCrossRef::class],
    version = 5,
    exportSchema = false
)
abstract class MusicDataBase : RoomDatabase() {
    abstract val musicDao: MusicDao

    companion object {
        private val musicDataBase =
            Room.databaseBuilder(BaseApplication.context, MusicDataBase::class.java, "name")
                .fallbackToDestructiveMigration()
                .build()

        @Synchronized
        fun getInstance(): MusicDataBase {
            return musicDataBase
        }
    }
}
package com.example.softmusic.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.softmusic.entity.*

@Dao
interface MusicDao {
    @Insert
    fun insertMusicSong(song: MusicSong?):Long
    @Update
    fun updateMusicSong(vararg songs: MusicSong?)

    @Delete
    fun deleteMusicSong(vararg songs: MusicSong?)

    @get:Query("SELECT * FROM musicSong")
    val allMusicSong: LiveData<List<MusicSong>>
    @Query("SELECT * FROM musicSong")
    fun getAllMusicSongs():List<MusicSong>
    @Query("SELECT mediaFileUri FROM musicsong")
    fun getAllMediaUri():List<String>
    @Query("SELECT musicSongId FROM musicsong WHERE mediaFileUri = :key")
    fun getSongIdByUri(key:String):Long

    @Query("SELECT songAlbum FROM musicsong WHERE musicSongId = :key")
    fun getImageUri(key: Long):String

    @Query("SELECT * FROM musicSong WHERE songTitle = :key")
    fun getMusicSongByKey(key:String): MusicSong
    @Query("SELECT * FROM musicSong WHERE musicSongId = :key")
    fun getMusicSongById(key:Long): MusicSong

    @Insert
    fun insertMusicSongList(vararg songLists: MusicSongList?)

    @Update
    fun updateMusicSongList(vararg songLists: MusicSongList?)

    @Delete
    fun deleteMusicSongList(vararg songLists: MusicSongList?)

    @get:Query("SELECT * FROM musicSongList")
    val allMusicSongList: LiveData<List<MusicSongList>>

    @Query("SELECT * FROM musicSongList WHERE songListTitle =:key")
    fun getMusicSongListByKey(key:String): MusicSongList
    @Query("SELECT * FROM musicSongList WHERE musicSongListId =:key")
    fun getMusicSongListById(key:Long): MusicSongList

    @Insert
    fun insertPlaylistSongCrossRef(vararg playlistSongCrossRefs: PlaylistSongCrossRef?)

    @Update
    fun updatePlaylistSongCrossRef(vararg playlistSongCrossRefs: PlaylistSongCrossRef?)

    @Delete
    fun deletePlaylistSongCrossRef(vararg playlistSongCrossRefs: PlaylistSongCrossRef?)

    @get:Query("SELECT * FROM playlistSongCrossRef")
    val allPlayListSongCrossRef:LiveData<List<PlaylistSongCrossRef>>
    @Query("SELECT * FROM playlistSongCrossRef")
    fun getAllRef():List<PlaylistSongCrossRef>


    @get:Query("SELECT * FROM musicSongList")
    @get:Transaction
    val playlistsWithSongs: List<PlaylistWithSongs?>?

    @get:Query("SELECT * FROM musicSongList")
    @get:Transaction
    val liveDataPlaylistsWithSongs: LiveData<List<PlaylistWithSongs?>?>?

    @Transaction
    @Query("SELECT * FROM musicSongList WHERE musicSongListId = :key")
    fun getLiveDataPlaylistsWithSongsById(key: Long?): LiveData<PlaylistWithSongs>

    @Transaction
    @Query("SELECT * FROM musicSongList WHERE songListTitle = :songListTitle")
    fun getPlayListsWithSongsByKey(songListTitle:String): PlaylistWithSongs

    @Transaction
    @Query("SELECT * FROM musicSongList WHERE musicSongListId = :musicSongListId")
    fun getPlayListsWithSongsById(musicSongListId:Long): PlaylistWithSongs

    @get:Query("SELECT * FROM musicSong")
    @get:Transaction
    val songsWithPlaylists: List<SongWithPlaylists?>?

    @get:Query("SELECT * FROM musicSong")
    @get:Transaction
    val liveDataSongsWithPlaylists: LiveData<List<SongWithPlaylists?>?>?
}
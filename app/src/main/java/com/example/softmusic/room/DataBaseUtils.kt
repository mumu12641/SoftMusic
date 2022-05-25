package com.example.softmusic.room

import androidx.lifecycle.LiveData
import com.example.softmusic.entity.MusicSong
import com.example.softmusic.entity.MusicSongList
import com.example.softmusic.entity.PlaylistSongCrossRef
import com.example.softmusic.entity.PlaylistWithSongs
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask

class DataBaseUtils {
    companion object{
        val dataBase : MusicDataBase = MusicDataBase.getInstance()
        private val musicDao : MusicDao = dataBase.musicDao
        fun insertMusicSong(song: MusicSong):Long {
            val future = FutureTask {
                musicDao.insertMusicSong(song)
            }
            Executors.newCachedThreadPool().execute(future)
            return future.get()
        }
        fun updateMusicSong(vararg musicSongs: MusicSong?) {
            Thread { musicDao.updateMusicSong(*musicSongs) }.start()
        }
        fun deleteMusicSong(vararg musicSongs: MusicSong?) {
            Thread { musicDao.deleteMusicSong(*musicSongs) }.start()
        }
        fun getMusicSongById(key:Long): MusicSong {
            val future = FutureTask {
                musicDao.getMusicSongById(key)
            }
            Executors.newCachedThreadPool().execute(future)
            return future.get()
        }
        fun getMusicIdByAlbumId(key:Long):Long{
            val future = FutureTask {
                musicDao.getMusicIdByAlbumId(key)
            }
            Executors.newCachedThreadPool().execute(future)
            return future.get()
        }
        fun getImageUri(key:Long):String{
            val future = FutureTask {
                musicDao.getImageUri(key)
            }
            Executors.newCachedThreadPool().execute(future)
            return future.get()
        }
        fun getAllMediaUri():List<String>{
            val future = FutureTask {
                musicDao.getAllMediaUri()
            }
            Executors.newCachedThreadPool().execute(future)
            return future.get()
        }
//        @OptIn(DelicateCoroutinesApi::class)
//        fun getAllAlbumId():List<Long>{
//            GlobalScope.launch {
//                return musicDao.getAllAlbumId()
//            }
//        }
        fun getSongIdByUri(key:String):Long{
            val future = FutureTask {
                musicDao.getSongIdByUri(key)
            }
            Executors.newCachedThreadPool().execute(future)
            return future.get()
        }

        fun insertMusicSongRef(vararg playlistSongCrossRefs: PlaylistSongCrossRef?) {
            Thread { musicDao.insertPlaylistSongCrossRef(*playlistSongCrossRefs) }.start()
        }

        fun deleteMusicSongRef(vararg playlistSongCrossRefs: PlaylistSongCrossRef?) {
            Thread { musicDao.deletePlaylistSongCrossRef(*playlistSongCrossRefs) }.start()
        }

        fun updateMusicSongRef(vararg playlistSongCrossRefs: PlaylistSongCrossRef?) {
            Thread { musicDao.deletePlaylistSongCrossRef(*playlistSongCrossRefs) }.start()
        }

        fun getAllPlaylistSongCrossRef():LiveData<List<PlaylistSongCrossRef>>{
            return musicDao.allPlayListSongCrossRef
        }
         fun getAllRef():List<PlaylistSongCrossRef>{
            val future = FutureTask {
                musicDao.getAllRef()
            }
            Executors.newCachedThreadPool().execute(future)
            return future.get()
        }

        fun insertMusicSongList(vararg musicSongLists: MusicSongList) {
            Thread { musicDao.insertMusicSongList(*musicSongLists) }.start()
        }

        fun deleteMusicSongList(vararg musicSongLists: MusicSongList) {
            Thread { musicDao.deleteMusicSongList(*musicSongLists) }.start()
        }

        fun getMusicSongListByKey(key:String): MusicSongList {
            val future = FutureTask {
                musicDao.getMusicSongListByKey(key)
            }
            Executors.newCachedThreadPool().execute(future)
            return future.get()
        }
        fun getMusicSongListById(key:Long): MusicSongList {
            val future = FutureTask {
                musicDao.getMusicSongListById(key)
            }
            Executors.newCachedThreadPool().execute(future)
            return future.get()
        }

        fun updateMusicSongList(vararg musicSongLists: MusicSongList) {
            Thread { musicDao.updateMusicSongList(*musicSongLists) }.start()
        }
        fun getLiveDataPlaylistsWithSongsById(key:Long):LiveData<PlaylistWithSongs>{
            return musicDao.getLiveDataPlaylistsWithSongsById(key)
        }

        fun getAllMusicSongList():LiveData<List<MusicSongList>>{
            return musicDao.allMusicSongList
        }
        fun getAllList():List<MusicSongList>{
            val future = FutureTask {
                musicDao.getAllList()
            }
            Executors.newCachedThreadPool().execute(future)
            return future.get()
        }
        fun getPlayListsWithSongsById(musicSongListId:Long):List<MusicSong>{
            val future = FutureTask {
                musicDao.getPlayListsWithSongsById(musicSongListId)
            }
            Executors.newCachedThreadPool().execute(future)
            return future.get().songs
        }
    }
}
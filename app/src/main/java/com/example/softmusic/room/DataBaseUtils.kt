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

        suspend fun getMusicIdByAlbumId(key:Long):Long{
            return musicDao.getMusicIdByAlbumId(key)

        }

        fun getAllMediaUri():List<String>{
            val future = FutureTask {
                musicDao.getAllMediaUri()
            }
            Executors.newCachedThreadPool().execute(future)
            return future.get()

        }

       suspend fun getSongIdByUri(key:String):Long{
            return musicDao.getSongIdByUri(key)
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

        suspend fun getAllRefSuspend():List<PlaylistSongCrossRef>{
            return musicDao.getAllRefSuspend()
        }

        fun insertMusicSongList(vararg musicSongLists: MusicSongList) {
            Thread { musicDao.insertMusicSongList(*musicSongLists) }.start()
        }

        fun deleteMusicSongList(vararg musicSongLists: MusicSongList) {
            Thread { musicDao.deleteMusicSongList(*musicSongLists) }.start()
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
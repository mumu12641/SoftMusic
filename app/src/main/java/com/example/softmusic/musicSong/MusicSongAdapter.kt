package com.example.softmusic.musicSong

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.softmusic.R
import com.example.softmusic.bottomSheet.StarBottomSheet
import com.example.softmusic.databinding.CardSongBinding
import com.example.softmusic.entity.MusicSong
import com.example.softmusic.entity.PlaylistSongCrossRef
import com.example.softmusic.listener.ChangePlayMusicListener
import com.example.softmusic.room.DataBaseUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File


class MusicSongAdapter(private val context: Context,
                       private var musicSongList: List<MusicSong>?,
                       private val musicSongListId:Long,
                       private val listener:ChangePlayMusicListener,
                       private var selectedId:Long,
                       private val longClickAction:Int,
                       private val manager: FragmentManager?
) :
    RecyclerView.Adapter<MusicSongAdapter.ViewHolder>() {

    private  val TAG = "MusicSongAdapter"

    private val job = Job()
    private val scope = CoroutineScope(job)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardSongListBinding: CardSongBinding = CardSongBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return ViewHolder(cardSongListBinding)
    }

    @SuppressLint("SetTextI18n", "Recycle")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        with(holder.cardSongListBinding) {

            songSinger.text = musicSongList?.get(position)?.songSinger
            songTitle.text = musicSongList?.get(position)?.songTitle
            Glide.with(context)
                .load(musicSongList?.get(position)?.songAlbum)
                    .centerCrop()
                .placeholder(R.drawable.music_note_150)
                .into(holder.cardSongListBinding.songRecord)
            if (longClickAction == DELETE_ACTION){
                songItem.setOnLongClickListener{
                    MaterialAlertDialogBuilder(context)
                        .setTitle("删除歌曲")
                        .setMessage("确认删除 " + musicSongList?.get(position)?.songTitle.toString() + " 吗？")
                        .setNegativeButton("取消") { dialog, _ ->
                            dialog.cancel()
                        }
                        .setPositiveButton("确认") { _, _ ->
                            DataBaseUtils.deleteMusicSongRef(
                                PlaylistSongCrossRef(
                                    musicSongListId,
                                    musicSongList?.get(position)?.musicSongId!!
                                )
                            )
                            val songList = DataBaseUtils.getMusicSongListById(musicSongListId)
                            songList.songNumber--
                            DataBaseUtils.updateMusicSongList(songList)
                        }
                        .show()
                    return@setOnLongClickListener true
                }
            } else if (longClickAction == ADD_ACTION){
                songItem.setOnLongClickListener {
                    // TODO cache the song and update the url
                    val fileName = musicSongList?.get(position)!!.songTitle  + musicSongList?.get(position)!!.albumId + ".mp3"
                    val cacheFile = File(context.cacheDir,fileName)
                    if (!cacheFile.exists()){
                        File.createTempFile(fileName,null,context.cacheDir)
                        FileDownloader.setup(context)
                        FileDownloader.getImpl().create(musicSongList?.get(position)!!.mediaFileUri)
                                .setPath(cacheFile.path)
                                .setListener(object : FileDownloadListener() {
                                        override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                                        }
                                        override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                                        }
                                        override fun completed(task: BaseDownloadTask?) {
                                            Log.d(TAG, "completed")
                                            scope.launch {
                                                Log.d(TAG, "completed:  launch")
                                                val list = DataBaseUtils.dataBase.musicDao.getAllAlbumId()
                                                var song = musicSongList?.get(position)!!
                                                if (list.isEmpty() || !list.contains(song.albumId)){
                                                    // update url
                                                    song.mediaFileUri = cacheFile.path
                                                    song.musicSongId = DataBaseUtils.insertMusicSong(song)
                                                } else {
                                                    song.musicSongId = DataBaseUtils.getMusicIdByAlbumId(song.albumId)
                                                    song = DataBaseUtils.getMusicSongById(song.musicSongId)
                                                    song.mediaFileUri = cacheFile.path
                                                    DataBaseUtils.updateMusicSong(song)
                                                }
                                                val bottomSheet = StarBottomSheet(song.musicSongId)
                                                manager?.let {
                                                    it -> bottomSheet.show(it,StarBottomSheet.TAG)
                                                }
                                            }
                                        }

                                        override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                                        }

                                        override fun error(task: BaseDownloadTask?, e: Throwable?) {
                                        }

                                        override fun warn(task: BaseDownloadTask?) {
                                        }

                                    }).start()
                        }
                    else{
                        scope.launch {
                            var song = musicSongList?.get(position)!!
                            song.musicSongId = DataBaseUtils.getMusicIdByAlbumId(song.albumId)
                            song = DataBaseUtils.getMusicSongById(song.musicSongId)
                            val bottomSheet = StarBottomSheet(song.musicSongId)
                            manager?.let {
                                it -> bottomSheet.show(it,StarBottomSheet.TAG)
                            }
                        }
                    }
                    true
                }
            }
            songItem.setOnClickListener {
                listener.changePlayMusic(
                    musicSongList?.get(position)?.musicSongId!!,
                    musicSongListId
                )
                listener.changePlayMusicByEntity(musicSongList?.get(position)!!)
                setSelectId(musicSongList?.get(position)?.musicSongId!!)
            }
            if (musicSongList?.get(position)?.musicSongId == selectedId ) {
                songSinger.setTextColor(context.resolveColorAttr(android.R.attr.colorPrimaryDark))
                songTitle.setTextColor(context.resolveColorAttr(android.R.attr.colorPrimaryDark))
            }else{
                songSinger.setTextColor(context.resolveColorAttr(android.R.attr.colorAccent))
                songTitle.setTextColor(context.resolveColorAttr(android.R.attr.colorAccent))
            }
        }

    }

    @ColorInt
    private fun Context.resolveColorAttr(@AttrRes colorAttr: Int): Int {
        val resolvedAttr = resolveThemeAttr(colorAttr)
        val colorRes = if (resolvedAttr.resourceId != 0) resolvedAttr.resourceId else resolvedAttr.data
        return ContextCompat.getColor(this, colorRes)
    }

    private fun Context.resolveThemeAttr(@AttrRes attrRes: Int): TypedValue {
        val typedValue = TypedValue()
        theme.resolveAttribute(attrRes, typedValue, true)
        return typedValue
    }

    override fun getItemCount(): Int {
        return musicSongList!!.size
    }

    class ViewHolder(var cardSongListBinding: CardSongBinding) : RecyclerView.ViewHolder(
        cardSongListBinding.root
    )

    @SuppressLint("NotifyDataSetChanged")
    fun setMusicSongs(list:List<MusicSong>){
        this.musicSongList = list
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectId(id:Long){
        selectedId = id
        notifyDataSetChanged()
    }

    companion object{
        const val DELETE_ACTION = 0
        const val ADD_ACTION = 1
        const val NONE_ACTION = 2
    }

}
package com.example.softmusic.musicSong

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.softmusic.R
import com.example.softmusic.databinding.CardSongBinding
import com.example.softmusic.entity.MusicSong
import com.example.softmusic.entity.PlaylistSongCrossRef
import com.example.softmusic.listener.ChangePlayMusicListener
import com.example.softmusic.room.DataBaseUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class MusicSongAdapter(private val context: Context,
                       private var musicSongList: List<MusicSong>?,
                       private val musicSongListId:Long,
                       private val listener:ChangePlayMusicListener,
                       private var selectedId:Long) :
    RecyclerView.Adapter<MusicSongAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardSongListBinding: CardSongBinding = CardSongBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return ViewHolder(cardSongListBinding)
    }

    @SuppressLint("SetTextI18n", "Recycle")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.cardSongListBinding) {

            songSinger.text = musicSongList?.get(position)?.songSinger
            songTitle.text = musicSongList?.get(position)?.songTitle
            Glide.with(context)
                .load(musicSongList?.get(position)?.songAlbum)
                    .centerCrop()
                .placeholder(R.drawable.music_note_150)
                .into(holder.cardSongListBinding.songRecord)
            songItem.setOnLongClickListener {
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
            songItem.setOnClickListener {
                listener.changePlayMusic(
                    musicSongList?.get(position)?.musicSongId!!,
                    musicSongListId
                )
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

}
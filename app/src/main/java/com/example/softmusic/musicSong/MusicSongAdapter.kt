package com.example.softmusic.musicSong

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.softmusic.databinding.CardSongBinding
import com.example.softmusic.entity.MusicSong
import com.example.softmusic.entity.PlaylistSongCrossRef
import com.example.softmusic.listener.ChangePlayMusicListener
import com.example.softmusic.room.DataBaseUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MusicSongAdapter(private val context: Context,
                       private val musicSongList: List<MusicSong>?,
                       private val musicSongListId:Long,
                       private val listener:ChangePlayMusicListener) :
    RecyclerView.Adapter<MusicSongAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardSongListBinding: CardSongBinding = CardSongBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return ViewHolder(cardSongListBinding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cardSongListBinding.songSinger.text = musicSongList?.get(position)?.songSinger
        holder.cardSongListBinding.songTitle.text = musicSongList?.get(position)?.songTitle
        holder.cardSongListBinding.number.text = (position + 1).toString()
        holder.cardSongListBinding.songItem.setOnLongClickListener{
            MaterialAlertDialogBuilder(context)
                .setTitle("删除歌曲")
                .setMessage("确认删除 " + musicSongList?.get(position)?.songTitle.toString() + " 吗？")
                .setNegativeButton("取消") { dialog, _ ->
                    dialog.cancel()
                }
                .setPositiveButton("确认") { _, _ ->
                    DataBaseUtils.deleteMusicSong(musicSongList?.get(position))
                    DataBaseUtils.deleteMusicSongRef(
                        PlaylistSongCrossRef(musicSongListId,
                            musicSongList?.get(position)?.musicSongId!!
                    )
                    )
                }
                .show()
            return@setOnLongClickListener true
        }
        holder.cardSongListBinding.songItem.setOnClickListener{
            listener.changePlayMusic(musicSongList?.get(position)?.musicSongId!!,musicSongListId)
        }

    }

    override fun getItemCount(): Int {
        return musicSongList!!.size
    }

    class ViewHolder(var cardSongListBinding: CardSongBinding) : RecyclerView.ViewHolder(
        cardSongListBinding.root
    )
}
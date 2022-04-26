package com.example.softmusic.musicSong

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.softmusic.R
import com.example.softmusic.databinding.CardSongBinding
import com.example.softmusic.room.DataBaseUtils
import com.example.softmusic.room.PlaylistSongCrossRef
import com.example.softmusic.songList.MusicSongListAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.math.log

class MusicSongAdapter(private val context: Context, private val musicSongList: List<MusicSong>?,private val songListTitle:String) :
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
                    DataBaseUtils.deleteMusicSongRef(PlaylistSongCrossRef(songListTitle,
                    musicSongList?.get(position)?.songTitle.toString()
                    ))
                }
                .show()
            return@setOnLongClickListener true
        }
        holder.cardSongListBinding.songItem.setOnClickListener{
            val controller: NavController = Navigation.findNavController(it)
            val bundle = Bundle()
            bundle.putString("songListTitle",songListTitle)
            bundle.putString("songTitle",musicSongList?.get(position)?.songTitle.toString())
            controller.navigate(R.id.action_musicSongFragment2_to_play_song, bundle)
        }

    }

    override fun getItemCount(): Int {
        return musicSongList!!.size
    }

    class ViewHolder(var cardSongListBinding: CardSongBinding) : RecyclerView.ViewHolder(
        cardSongListBinding.root
    )
}
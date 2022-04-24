package com.example.softmusic.musicSong

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.softmusic.databinding.CardSongBinding

class MusicSongAdapter(private val context: Context, private val musicSongList: List<MusicSong>) :
    RecyclerView.Adapter<MusicSongAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardSongListBinding: CardSongBinding = CardSongBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return ViewHolder(cardSongListBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cardSongListBinding.songSinger.text = musicSongList[position].songSinger
        holder.cardSongListBinding.songTitle.text = musicSongList[position].songTitle
        holder.cardSongListBinding.number.text = (position + 1).toString()
    }

    override fun getItemCount(): Int {
        return musicSongList!!.size
    }

    class ViewHolder(var cardSongListBinding: CardSongBinding) : RecyclerView.ViewHolder(
        cardSongListBinding.root
    )
}
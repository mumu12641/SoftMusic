package com.example.softmusic.playMusic

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.softmusic.R
import com.example.softmusic.databinding.MusicRecordBinding
import com.example.softmusic.entity.MusicSong

class MusicRecordAdapter(private var nowPlayList:List<MusicSong>,
                         private val context:Context
) : RecyclerView.Adapter<MusicRecordAdapter.ViewHolder>() {

    class ViewHolder(var musicRecordBinding: MusicRecordBinding) :
        RecyclerView.ViewHolder(musicRecordBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val musicRecordBinding: MusicRecordBinding =
            MusicRecordBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(musicRecordBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(nowPlayList[position].songAlbum).placeholder(R.drawable.music_note_150)
            .into(holder.musicRecordBinding.recordImage)
    }

    override fun getItemCount(): Int {
        return nowPlayList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setRecordList(list: List<MusicSong>) {
        this.nowPlayList = list
        notifyDataSetChanged()
    }
}
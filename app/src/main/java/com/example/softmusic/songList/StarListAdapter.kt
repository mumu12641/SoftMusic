package com.example.softmusic.songList

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.softmusic.R
import com.example.softmusic.databinding.CardSongBinding
import com.example.softmusic.entity.MusicSongList
import com.example.softmusic.entity.PlaylistSongCrossRef
import com.example.softmusic.room.DataBaseUtils

class StarListAdapter(val context:Context,
                      private val startList:List<MusicSongList>,
                      private val songId:Long) : RecyclerView.Adapter<StarListAdapter.ViewHolder>() {

    class ViewHolder(var cardSongBinding: CardSongBinding) : RecyclerView.ViewHolder(
        cardSongBinding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardSongBinding: CardSongBinding = CardSongBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolder(cardSongBinding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.cardSongBinding){
            songTitle.text = startList[position].songListTitle
            songSinger.text = startList[position].songNumber.toString() + "首"
            if (DataBaseUtils.getAllRef().contains(PlaylistSongCrossRef(startList[position].musicSongListId,songId))){
                songTitle.setTextColor(androidx.appcompat.R.attr.colorAccent)
                songSinger.setTextColor(androidx.appcompat.R.attr.colorAccent)
            }else{
                songTitle.setTextColor(androidx.appcompat.R.attr.colorPrimary)
                songSinger.setTextColor(androidx.appcompat.R.attr.colorPrimary)
            }
            songItem.setOnClickListener{
                if (DataBaseUtils.getAllRef().contains(PlaylistSongCrossRef(startList[position].musicSongListId,songId))){
                    Toast.makeText(context,"已经添加到该歌单",Toast.LENGTH_LONG).show()
                } else {
                    DataBaseUtils.insertMusicSongRef(PlaylistSongCrossRef(startList[position].musicSongListId,songId))
                    songTitle.setTextColor(androidx.appcompat.R.attr.colorAccent)
                    songSinger.setTextColor(androidx.appcompat.R.attr.colorAccent)
                    songSinger.text = (startList[position].songNumber + 1).toString() + "首"
                    Toast.makeText(context,"已经成功添加到" + startList[position].songListTitle,Toast.LENGTH_LONG).show()
                    val songList =
                        DataBaseUtils.getMusicSongListById(startList[position].musicSongListId)
                    songList.songNumber++
                    DataBaseUtils.updateMusicSongList(songList)
                }
            }
        }
        Glide.with(context).load(R.drawable.card).centerCrop().into(holder.cardSongBinding.songRecord)

    }

    override fun getItemCount(): Int {
        return startList.size
    }
}
package com.example.softmusic.songList

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.softmusic.R
import com.example.softmusic.databinding.CardSongListBinding
import com.example.softmusic.entity.MusicSongList

class MusicSongListAdapter(
    private val context: Context,
    private var musicSongListList: List<MusicSongList>,
    private var selectId:Long
) : RecyclerView.Adapter<MusicSongListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardSongListBinding: CardSongListBinding = CardSongListBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolder(cardSongListBinding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        with(holder.cardSongListBinding)
        {
            songNumber.text = musicSongListList[position].songNumber.toString() + '首'
            songListTitle.text = musicSongListList[position].songListTitle
            val array =
                intArrayOf(R.drawable.card, R.drawable.card2, R.drawable.card3, R.drawable.card3)
            if (musicSongListList[position].imageFileUri == "like"){
                imageSongList.setBackgroundResource(array[0])
            }else {
                imageSongList.setBackgroundResource(array[1])
            }
            if (musicSongListList[position].musicSongListId == selectId){
                Log.d("TAG", "onBindViewHolder: selected")
//                songListTitle.setTextColor(androidx.appcompat.R.attr.colorAccent)
//                songNumber.setTextColor(androidx.appcompat.R.attr.colorAccent)
            }
            songListItem.setOnClickListener { view: View ->
                val controller: NavController = findNavController(view)
                val bundle = Bundle()
                bundle.putLong("key", musicSongListList[position].musicSongListId)
                controller.navigate(R.id.action_to_song_fragment, bundle)
            }
        }

    }

    override fun getItemCount(): Int {
        return musicSongListList.size
    }

    class ViewHolder(var cardSongListBinding: CardSongListBinding) : RecyclerView.ViewHolder(
        cardSongListBinding.root
    )

    @SuppressLint("NotifyDataSetChanged")
    fun setMusicSongListList(musicSongListList: List<MusicSongList>) {
        this.musicSongListList = musicSongListList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedId(id:Long){
        selectId = id
        notifyDataSetChanged()
    }


}
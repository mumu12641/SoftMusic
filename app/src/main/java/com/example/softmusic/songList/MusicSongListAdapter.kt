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
    private var musicSongListList: List<MusicSongList>
) : RecyclerView.Adapter<MusicSongListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardSongListBinding: CardSongListBinding = CardSongListBinding.inflate(LayoutInflater.from(context))
        return ViewHolder(cardSongListBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        with(holder.cardSongListBinding)
        {
            songNumber.text = musicSongListList[position].songNumber.toString()
            songListTitle.text = musicSongListList[position].songListTitle
            songListBuilder.text = musicSongListList[position].builder
            val array =
                intArrayOf(R.drawable.card, R.drawable.card2, R.drawable.card3, R.drawable.card3)
            imageSongList.setBackgroundResource(array[position])
            songListItem.setOnClickListener { view: View ->
                val controller: NavController = findNavController(view)
                // TODO 传递Bundle（Room中的索引）
                val bundle = Bundle()
                bundle.putLong("key", musicSongListList[position].musicSongListId)
                Log.d(TAG, "onBindViewHolder: " + musicSongListList[position].songListTitle)
                controller.navigate(R.id.action_musicSongListFragment2_to_musicSongFragment2, bundle)
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

    companion object {
        private const val TAG = "MusicSongListAdapter"
    }
}
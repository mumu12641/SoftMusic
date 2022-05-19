package com.example.softmusic.songList

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
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
            imageSongList.setBackgroundResource(array[0])
            if (musicSongListList[position].musicSongListId == selectId){
                songListTitle.setTextColor(context.resolveColorAttr(androidx.appcompat.R.attr.colorPrimaryDark))
                songNumber.setTextColor(context.resolveColorAttr(androidx.appcompat.R.attr.colorPrimaryDark))
            } else {
                songListTitle.setTextColor(context.resolveColorAttr(androidx.appcompat.R.attr.colorAccent))
                songNumber.setTextColor(context.resolveColorAttr(androidx.appcompat.R.attr.colorAccent))
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
}
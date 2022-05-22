package com.example.softmusic.playMusic

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.softmusic.R
import com.example.softmusic.databinding.MusicRecordBinding
import com.example.softmusic.entity.MusicSong
import com.example.softmusic.entity.PlaylistSongCrossRef
import com.example.softmusic.listener.DoubleClickListener
import com.example.softmusic.room.DataBaseUtils

class MusicRecordAdapter(private var nowPlayList:List<MusicSong>,
                         private val context:Context,
                         private var listId:Long
) : RecyclerView.Adapter<MusicRecordAdapter.ViewHolder>() {

    class ViewHolder(var musicRecordBinding: MusicRecordBinding) :
        RecyclerView.ViewHolder(musicRecordBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val musicRecordBinding: MusicRecordBinding =
            MusicRecordBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(musicRecordBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        Glide.with(context).load(nowPlayList[position].songAlbum).placeholder(R.drawable.music_note_150)
            .into(holder.musicRecordBinding.recordImage)
        holder.musicRecordBinding.recordImage.setOnClickListener(object : DoubleClickListener() {
            override fun onClick(v: View?) {
                super.onClick(v)
            }
            override fun onDoubleClick() {
                holder.musicRecordBinding.lottie.visibility = View.VISIBLE
                holder.musicRecordBinding.lottie.playAnimation()
                holder.musicRecordBinding.lottie.addAnimatorListener(object :
                    Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator?) {
                    }
                    override fun onAnimationEnd(p0: Animator?) {
                        holder.musicRecordBinding.lottie.visibility = View.INVISIBLE
                    }
                    override fun onAnimationCancel(p0: Animator?) {
                    }
                    override fun onAnimationRepeat(p0: Animator?) {
                    }
                })
                if (listId != 1L && !DataBaseUtils.getAllRef().contains(PlaylistSongCrossRef(1L,nowPlayList[position].musicSongId))){
                    DataBaseUtils.insertMusicSongRef(PlaylistSongCrossRef(listId,nowPlayList[position].musicSongId))
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return nowPlayList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setRecordList(list: List<MusicSong>) {
        this.nowPlayList = list
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setListId(listId: Long){
        this.listId = listId
        notifyDataSetChanged()
    }
}
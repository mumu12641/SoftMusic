package com.example.softmusic.playMusic

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.softmusic.R
import com.example.softmusic.databinding.MusicRecordBinding

class MusicRecordAdapter(private var imageUriList:List<String>,
                         private val context:Context
) : RecyclerView.Adapter<MusicRecordAdapter.ViewHolder>(){

    class ViewHolder(var musicRecordBinding: MusicRecordBinding):
        RecyclerView.ViewHolder(musicRecordBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val musicRecordBinding:MusicRecordBinding =
            MusicRecordBinding.inflate(LayoutInflater.from(context),parent,false)

        return ViewHolder(musicRecordBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(imageUriList[position]).placeholder(R.drawable.music_note_150).into(holder.musicRecordBinding.recordImage)
    }

    override fun getItemCount(): Int {
        return imageUriList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setRecordList(list:List<String>){
        this.imageUriList = list
        notifyDataSetChanged()
    }
    override fun onViewAttachedToWindow(holder: MusicRecordAdapter.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        addAnimation(holder)
    }
    private fun getAnimators(v: View) : Animator{
        return ObjectAnimator.ofFloat(v,"alpha",0f,1f)
    }
    private fun addAnimation(holder:MusicRecordAdapter.ViewHolder){
        getAnimators(holder.musicRecordBinding.recordImage).setDuration(200).start()
    }
}
package com.cha.auctionapp.adapters

import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.cha.auctionapp.R
import com.cha.auctionapp.activities.AuctionDetailActivity
import com.cha.auctionapp.activities.MainActivity
import com.cha.auctionapp.databinding.FragmentAuctionBinding
import com.cha.auctionapp.databinding.FragmentAuctionDetailBottomSheet2Binding
import com.cha.auctionapp.databinding.FragmentAuctionDetailBottomSheetBinding
import com.cha.auctionapp.databinding.RecyclerAuctionItemBinding
import com.cha.auctionapp.model.AuctionPagerItem
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.material.bottomsheet.BottomSheetDialog

class AuctionPagerAdapter(var context: Context,var items: MutableList<AuctionPagerItem>) : Adapter<AuctionPagerAdapter.VH>() {



    inner class VH(var binding: RecyclerAuctionItemBinding) : ViewHolder(binding.root){
        var exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerAuctionItemBinding.inflate(LayoutInflater.from(context),parent,false))


    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holder: VH, position: Int) {
        var item: AuctionPagerItem = items[position]




        holder.binding.tvId.text = item.id
        holder.binding.tvDescription.text = item.description
        Glide.with(context).load(item.image).into(holder.binding.civProfile)


        /*
        *       BottomSheet 1번 구현
        * */
        holder.binding.btnBid.setOnClickListener { clickBidBtn(holder) }

        /*
        *      Exoplayer 구현
        * */
        exoPlayer(item,holder)
    }

    private fun clickBidBtn(holder: VH){

        context.startActivity(Intent(context,AuctionDetailActivity::class.java))


    }










    /*
    *
    *       ExoPlayer 구현
    *
    * */


    private fun exoPlayer(item: AuctionPagerItem,holder: VH){
        Log.i("pagerExo","${holder.layoutPosition} 번 뷰홀더")
        var mediaItem: MediaItem = MediaItem.fromUri(item.video)
        holder.binding.videoview.player = holder.exoPlayer
        holder.exoPlayer.prepare()
        holder.exoPlayer.setMediaItem(mediaItem)
        holder.exoPlayer.repeatMode = ExoPlayer.REPEAT_MODE_ALL
    }


    override fun onViewDetachedFromWindow(holder: VH) {
        super.onViewDetachedFromWindow(holder)
        Log.i("pagerExo","${holder.layoutPosition} 번 뷰홀더 Detached")
        holder.exoPlayer.pause()
    }

    override fun onViewAttachedToWindow(holder: VH) {
        super.onViewAttachedToWindow(holder)
        Log.i("pagerExo","${holder.layoutPosition} 번 뷰홀더 Attached")
        holder.exoPlayer.play()
        holder.binding.videoview.setOnClickListener {
            when(holder.exoPlayer.isPlaying){
                true->holder.exoPlayer.pause()
                false->holder.exoPlayer.play()
            }
        }
    }
}


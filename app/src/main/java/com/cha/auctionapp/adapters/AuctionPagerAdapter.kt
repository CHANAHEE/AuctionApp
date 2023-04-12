package com.cha.auctionapp.adapters

import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.cha.auctionapp.activities.AuctionEditActivity
import com.cha.auctionapp.activities.AuctionDetailActivity
import com.cha.auctionapp.databinding.RecyclerAuctionItemBinding
import com.cha.auctionapp.model.AuctionPagerItem
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class AuctionPagerAdapter(var context: Context,var items: MutableList<AuctionPagerItem>) : Adapter<AuctionPagerAdapter.VH>() {



    inner class VH(var binding: RecyclerAuctionItemBinding) : ViewHolder(binding.root){
        var exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerAuctionItemBinding.inflate(LayoutInflater.from(context),parent,false))


    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holder: VH, position: Int) {
        var item: AuctionPagerItem = items[position]


        holder.binding.tvId.text = item.nickname
        holder.binding.tvDescription.text = item.description
        Glide.with(context).load(item.image).into(holder.binding.civProfile)
       // holder.binding.fabEdit.setOnClickListener {context.startActivity(Intent(context,AuctionEditActivity::class.java))}




        /*
        *       경매 남은 시간 타이머
        * */
        countDown(holder)

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




    private fun countDown(holder: VH){
        object : CountDownTimer(43200000,1000) {

            /*
            *
            *       게시글을 올린 시간을 기준으로 해야할듯. (12시간 - 게시글 올린 시간) -> 요게 남은 시간
            *       올린 시간을 기준으로 12시간 후에는 게시글을 삭제시키기.
            *
            * */
            override fun onTick(millisUntilFinished: Long) {
                var hour    = (millisUntilFinished/1000/3600)
                var min     = (millisUntilFinished/1000/60%60)
                var second  = (millisUntilFinished/1000%60)

                var time = String.format("%02d : %02d : %02d",hour,min,second)

                holder.binding.btnBid.text = "입 찰 하 기\n\n${time}"
            }

            override fun onFinish() {
                /*
                *       게시글 삭제
                * */
            }
        }.start()
    }





    /*
    *
    *       ExoPlayer 구현
    *
    * */


    private fun exoPlayer(item: AuctionPagerItem,holder: VH){
        Log.i("pagerExo","${holder.layoutPosition} 번 뷰홀더")
        var mediaItem: MediaItem = MediaItem.fromUri(item.video!!)
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



    /*
    *
    *       이미지를 비디오로 변환하는 작업
    *
    * */

}


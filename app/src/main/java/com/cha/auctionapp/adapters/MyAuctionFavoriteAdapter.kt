package com.cha.auctionapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.cha.auctionapp.R
import com.cha.auctionapp.activities.AuctionDetailActivity
import com.cha.auctionapp.activities.HomeDetailActivity
import com.cha.auctionapp.databinding.RecyclerCommunityItemBinding
import com.cha.auctionapp.databinding.RecyclerSearchByCategoryItemBinding
import com.cha.auctionapp.model.MainItem
import com.cha.auctionapp.model.MyAuctionFavListItem
import com.cha.auctionapp.model.MyAuctionPostList
import com.cha.auctionapp.model.MyFavListItem

class MyAuctionFavoriteAdapter(var context:Context, var items:MutableList<MyAuctionFavListItem>) : Adapter<MyAuctionFavoriteAdapter.VH>(){

    inner class VH(var binding: RecyclerCommunityItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerCommunityItemBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        var item:MyAuctionFavListItem = items[position]

        holder.binding.relativeBidTime.visibility = View.VISIBLE
        countDown(item,holder)
        holder.binding.tvTitle.text = item.title
        holder.binding.tvLocation.text = item.location
        holder.binding.tvContents.text = item.description

        holder.itemView.setOnClickListener { context.startActivity(Intent(context,AuctionDetailActivity::class.java).putExtra("index",item.indexProduct.toString())) }
    }


    private fun countDown(item: MyAuctionFavListItem, holder: VH){
        var remainTime = 43200000 + item.now.toLong() - System.currentTimeMillis()
        object : CountDownTimer(remainTime,1000) {


            override fun onTick(millisUntilFinished: Long) {
                var hour    = (millisUntilFinished/1000/3600)
                var min     = (millisUntilFinished/1000/60%60)
                var second  = (millisUntilFinished/1000%60)

                var time = String.format("%02d : %02d : %02d",hour,min,second)

                holder.binding.tvBidTime.text = time
            }

            @SuppressLint("ResourceAsColor")
            override fun onFinish() {
                /*
                *       게시글 삭제
                * */
                holder.binding.tvBidTime.text = "경매 종료"
                holder.binding.tvBidTime.backgroundTintList = ColorStateList.valueOf(R.color.unable)
            }
        }.start()
    }

}
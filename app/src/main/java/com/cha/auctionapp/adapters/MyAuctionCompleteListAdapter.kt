package com.cha.auctionapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.cha.auctionapp.activities.HomeDetailActivity
import com.cha.auctionapp.databinding.RecyclerCommunityItemBinding
import com.cha.auctionapp.databinding.RecyclerSearchByCategoryItemBinding
import com.cha.auctionapp.model.CommunityPostItem
import com.cha.auctionapp.model.HomeDetailItem
import com.cha.auctionapp.model.MyAuctionPostList
import com.cha.auctionapp.model.MyCommunityPostList
import com.cha.auctionapp.model.MyPostListItem

class MyAuctionCompleteListAdapter(var context:Context, var items:MutableList<MyAuctionPostList>) : Adapter<MyAuctionCompleteListAdapter.VH>() {

    inner class VH(var binding:RecyclerCommunityItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerCommunityItemBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        var item = items[position]

        holder.binding.tvTitle.text = item.title
        holder.binding.tvLocation.text = item.location
        holder.binding.tvContents.text = item.description
        holder.binding.root.tag = item.idx

        /*
        *
        *       CommunityDetail 로 이동
        *
        * */
        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, HomeDetailActivity::class.java).putExtra("index",holder.binding.root.tag.toString()))
        }
    }
}
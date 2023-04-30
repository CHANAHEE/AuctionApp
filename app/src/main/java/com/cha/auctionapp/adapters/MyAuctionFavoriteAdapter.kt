package com.cha.auctionapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.cha.auctionapp.activities.AuctionDetailActivity
import com.cha.auctionapp.activities.HomeDetailActivity
import com.cha.auctionapp.databinding.RecyclerCommunityItemBinding
import com.cha.auctionapp.databinding.RecyclerSearchByCategoryItemBinding
import com.cha.auctionapp.model.MainItem
import com.cha.auctionapp.model.MyAuctionFavListItem
import com.cha.auctionapp.model.MyFavListItem

class MyAuctionFavoriteAdapter(var context:Context, var items:MutableList<MyAuctionFavListItem>) : Adapter<MyAuctionFavoriteAdapter.VH>(){

    inner class VH(var binding: RecyclerCommunityItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerCommunityItemBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        var item:MyAuctionFavListItem = items[position]

        holder.binding.tvTitle.text = item.title
        holder.binding.tvLocation.text = item.location
        holder.binding.tvContents.text = item.description

        holder.itemView.setOnClickListener { context.startActivity(Intent(context,AuctionDetailActivity::class.java).putExtra("index",item.indexProduct.toString())) }
    }

}
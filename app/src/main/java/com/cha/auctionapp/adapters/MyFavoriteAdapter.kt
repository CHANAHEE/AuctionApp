package com.cha.auctionapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.cha.auctionapp.activities.HomeDetailActivity
import com.cha.auctionapp.databinding.RecyclerSearchByCategoryItemBinding
import com.cha.auctionapp.model.MainItem
import com.cha.auctionapp.model.MyFavListItem

class MyFavoriteAdapter(var context:Context, var items:MutableList<MyFavListItem>) : Adapter<MyFavoriteAdapter.VH>(){

    inner class VH(var binding: RecyclerSearchByCategoryItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerSearchByCategoryItemBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        var item:MyFavListItem = items[position]

        var baseAddr = "http://tjdrjs0803.dothome.co.kr/Server/" + item.image.split(",")[0]
        Glide.with(context).load(baseAddr).into(holder.binding.ivMainImg)
        holder.binding.tvTitle.text = item.title
        holder.binding.tvLocationName.text = item.location
        holder.binding.tvPrice.text = item.price

        holder.itemView.setOnClickListener { context.startActivity(Intent(context,HomeDetailActivity::class.java)) }
    }

}
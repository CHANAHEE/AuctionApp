package com.example.auctionapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.auctionapp.databinding.RecyclerProductItemBinding
import com.example.auctionapp.model.MainItem

class ProductAdapter(var context:Context,var mainItems:MutableList<MainItem>) : Adapter<ProductAdapter.VH>(){


    inner class VH(var binding:RecyclerProductItemBinding) : ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerProductItemBinding.inflate(LayoutInflater.from(context),parent,false))
    override fun getItemCount(): Int = mainItems.size
    override fun onBindViewHolder(holder: VH, position: Int) {
        var mainItem : MainItem = mainItems[position]

        Glide.with(context).load(mainItem.image).into(holder.binding.ivMainImg)
        holder.binding.tvTitle.text = mainItem.title
        holder.binding.tvLocationName.text = mainItem.location
        holder.binding.tvPrice.text = mainItem.price
    }
}
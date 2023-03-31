package com.example.auctionapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.auctionapp.databinding.RecyclerProductItemBinding
import com.example.auctionapp.model.Item

class ProductAdapter(var context:Context,var items:MutableList<Item>) : Adapter<ProductAdapter.VH>(){


    inner class VH(var binding:RecyclerProductItemBinding) : ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerProductItemBinding.inflate(LayoutInflater.from(context),parent,false))
    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: VH, position: Int) {
        var item : Item = items[position]

        Glide.with(context).load(item.image).into(holder.binding.ivMainImg)
        holder.binding.tvTitle.text = item.title
        holder.binding.tvLocationName.text = item.location
        holder.binding.tvPrice.text = item.price
    }
}
package com.example.auctionapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.auctionapp.activities.HomeDetailActivity
import com.example.auctionapp.databinding.ActivitySearchItemByCategoryBinding
import com.example.auctionapp.databinding.RecyclerSearchByCategoryItemBinding
import com.example.auctionapp.model.MainItem

class CategorySearchAdapter(var context:Context,var items:MutableList<MainItem>) : Adapter<CategorySearchAdapter.VH>(){

    inner class VH(var binding: RecyclerSearchByCategoryItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerSearchByCategoryItemBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        var item:MainItem = items[position]

        Glide.with(context).load(item.image).into(holder.binding.ivMainImg)
        holder.binding.tvTitle.text = item.title
        holder.binding.tvLocationName.text = item.location
        holder.binding.tvPrice.text = item.price

        holder.binding.root.setOnClickListener { clickItem() }
    }

    private fun clickItem() {
        context.startActivity(Intent(context,HomeDetailActivity::class.java))
    }


}
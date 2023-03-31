package com.example.auctionapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.auctionapp.databinding.ActivityHomeDetailBinding
import com.example.auctionapp.databinding.ViewpagerItemBinding
import com.example.auctionapp.model.PagerItem

class PagerAdapter(var context:Context,var items:MutableList<PagerItem>) : Adapter<PagerAdapter.VH>() {

    inner class VH(var binding: ViewpagerItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(ViewpagerItemBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        Glide.with(context).load(items[position].image).into(holder.binding.pagerItem)
    }
}
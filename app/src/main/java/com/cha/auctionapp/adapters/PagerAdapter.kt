package com.cha.auctionapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.cha.auctionapp.databinding.ViewpagerItemBinding
import com.cha.auctionapp.model.PagerItem

class PagerAdapter(var context:Context,var items:MutableList<PagerItem>) : Adapter<PagerAdapter.VH>() {

    inner class VH(var binding: ViewpagerItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(ViewpagerItemBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        var baseAddr = "http://tjdrjs0803.dothome.co.kr/Server/" + items[position].image
        Glide.with(context).load(baseAddr).into(holder.binding.pagerItem)


    }
}
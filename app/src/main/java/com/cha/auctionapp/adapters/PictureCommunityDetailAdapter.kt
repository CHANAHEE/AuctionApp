package com.cha.auctionapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.cha.auctionapp.activities.SellingEditActivity
import com.cha.auctionapp.databinding.RecyclerPictureCommunityDetailItemBinding
import com.cha.auctionapp.databinding.RecyclerPictureItemBinding
import com.cha.auctionapp.model.PictureCommunityDetailItem
import com.cha.auctionapp.model.PictureItem

class PictureCommunityDetailAdapter(var context: Context, var items: MutableList<PictureCommunityDetailItem>) : Adapter<PictureCommunityDetailAdapter.VH>() {

    inner class VH(var binding: RecyclerPictureCommunityDetailItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerPictureCommunityDetailItemBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        var baseAddr = "http://tjdrjs0803.dothome.co.kr/Server/" + items[position].path.split(",")[0]
        Glide.with(context).load(baseAddr).into(holder.binding.ivPicture)
    }
}
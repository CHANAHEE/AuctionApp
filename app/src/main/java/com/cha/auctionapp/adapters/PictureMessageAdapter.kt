package com.cha.auctionapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.cha.auctionapp.activities.SellingEditActivity
import com.cha.auctionapp.databinding.RecyclerChattingPictureMessageBinding
import com.cha.auctionapp.databinding.RecyclerPictureCommunityDetailItemBinding
import com.cha.auctionapp.databinding.RecyclerPictureItemBinding
import com.cha.auctionapp.model.PictureCommunityDetailItem
import com.cha.auctionapp.model.PictureItem

class PictureMessageAdapter(var context: Context, var items: MutableList<PictureItem>) : Adapter<PictureMessageAdapter.VH>() {

    inner class VH(var binding: RecyclerChattingPictureMessageBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerChattingPictureMessageBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {

        Glide.with(context).load(items[position].uri).into(holder.binding.ivChatPictureMessage)
    }
}
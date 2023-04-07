package com.cha.auctionapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.cha.auctionapp.databinding.RecyclerCommentsItemBinding
import com.cha.auctionapp.model.CommentsItem

class CommentsAdapter(var context: Context, var items: MutableList<CommentsItem>) : Adapter<CommentsAdapter.VH>(){

    inner class VH(var binding: RecyclerCommentsItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerCommentsItemBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {

        var item = items[position]
        holder.binding.tvCommentsDetail.text = item.description
        holder.binding.tvOtherId.text = item.id
        holder.binding.tvOtherTownName.text = item.town
        if(item.location != null){
            holder.binding.relativeLocation.visibility = View.VISIBLE
            holder.binding.tvLocationName.text = item.location
        }
        Glide.with(context).load(item.image).into(holder.binding.civOtherProfile)
    }
}
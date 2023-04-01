package com.example.auctionapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.auctionapp.databinding.RecyclerCommunityItemBinding
import com.example.auctionapp.model.CommunityPostItem

class MyPostListAdapter(var context:Context, var items:MutableList<CommunityPostItem>) : Adapter<MyPostListAdapter.VH>() {

    inner class VH(var binding:RecyclerCommunityItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerCommunityItemBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.tvTitle.text = items[position].title
        holder.binding.tvContents.text = items[position].contents
        holder.binding.tvLocation.text = items[position].location


        /*
        *
        *       CommunityDetail 로 이동
        *
        * */
    }
}
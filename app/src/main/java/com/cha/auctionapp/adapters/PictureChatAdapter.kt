package com.cha.auctionapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.cha.auctionapp.activities.ChattingActivity
import com.cha.auctionapp.activities.SellingEditActivity
import com.cha.auctionapp.databinding.RecyclerPictureChatItemBinding
import com.cha.auctionapp.databinding.RecyclerPictureCommunityDetailItemBinding
import com.cha.auctionapp.databinding.RecyclerPictureItemBinding
import com.cha.auctionapp.model.PictureItem

class PictureChatAdapter(var context: Context, var items: MutableList<PictureItem>) : Adapter<PictureChatAdapter.VH>() {

    inner class VH(var binding: RecyclerPictureChatItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerPictureChatItemBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        Glide.with(context).load(items[position].uri).into(holder.binding.ivPicture)
        holder.binding.btnCancel.setOnClickListener {

            items.removeAt(position)
            if(items.size == 0) {
                (context as ChattingActivity).binding.cvPicture.visibility = View.GONE
            }
            this.notifyDataSetChanged()
        }
    }
}
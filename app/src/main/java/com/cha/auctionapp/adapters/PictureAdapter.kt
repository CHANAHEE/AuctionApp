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
import com.cha.auctionapp.databinding.RecyclerPictureItemBinding
import com.cha.auctionapp.model.PictureItem

class PictureAdapter(var context: Context, var items: MutableList<PictureItem>) : Adapter<PictureAdapter.VH>() {

    inner class VH(var binding: RecyclerPictureItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerPictureItemBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        Glide.with(context).load(items[position].uri).into(holder.binding.ivPicture)
        holder.binding.btnCancel.setOnClickListener {

            items.removeAt(position)
            if(context is SellingEditActivity){
                (context as SellingEditActivity).binding.btnImage.text = "${items.size} / 10"
            }

            this.notifyDataSetChanged()
        }
    }
}
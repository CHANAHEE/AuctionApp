package com.example.auctionapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.auctionapp.activities.CommunityEditActivity
import com.example.auctionapp.activities.SellingEditActivity
import com.example.auctionapp.databinding.ActivityHomeDetailBinding
import com.example.auctionapp.databinding.ActivitySellingEditBinding
import com.example.auctionapp.databinding.RecyclerPictureItemBinding
import com.example.auctionapp.databinding.ViewpagerItemBinding
import com.example.auctionapp.model.PagerItem
import com.example.auctionapp.model.PictureItem

class PictureAdapter(var context: Context, var items: MutableList<PictureItem>) : Adapter<PictureAdapter.VH>() {

    inner class VH(var binding: RecyclerPictureItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerPictureItemBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun getItemCount(): Int {
        Log.i("Hello","${items.size}")
        return items.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        Log.i("Hello","onBindViewHolder")
        Glide.with(context).load(items[position].uri).into(holder.binding.ivPicture)
        holder.binding.btnCancel.setOnClickListener {

            items.removeAt(position)
            Log.i("removePicture","${position} removePicture")

            if(context is SellingEditActivity){
                (context as SellingEditActivity).binding.btnImage.text = "${items.size} / 10"
            }
            this.notifyDataSetChanged()

        }
    }
}
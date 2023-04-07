package com.cha.auctionapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.cha.auctionapp.activities.SellingEditActivity
import com.cha.auctionapp.databinding.RecyclerPictureItemBinding
import com.cha.auctionapp.model.PictureItem

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
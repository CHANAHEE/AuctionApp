package com.cha.auctionapp.adapters

import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.cha.auctionapp.activities.ExpandImageActivity
import com.cha.auctionapp.databinding.RecyclerPictureCommunityDetailItemBinding
import com.cha.auctionapp.model.PictureCommunityDetailItem

class PictureCommunityDetailAdapter(var context: Context, var items: MutableList<PictureCommunityDetailItem>) : Adapter<PictureCommunityDetailAdapter.VH>() {

    var imageString = ""

    inner class VH(var binding: RecyclerPictureCommunityDetailItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerPictureCommunityDetailItemBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {

        var baseAddr = "http://tjdrjs0803.dothome.co.kr/Server/" + items[position].path
        Glide.with(context).load(baseAddr).into(holder.binding.ivPicture)

        imageString += baseAddr
        if(position != items.size-1) imageString += ","
        holder.binding.ivPicture.setOnClickListener {
            context.startActivity(Intent(context, ExpandImageActivity::class.java)
                .putExtra("image",imageString)
                .putExtra("position",position)) }
    }
}
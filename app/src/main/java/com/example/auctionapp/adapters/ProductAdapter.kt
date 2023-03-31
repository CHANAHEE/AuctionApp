package com.example.auctionapp.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.auctionapp.activities.HomeDetailActivity
import com.example.auctionapp.databinding.RecyclerProductItemBinding
import com.example.auctionapp.model.MainItem

class ProductAdapter(var context:Context,var mainItems:MutableList<MainItem>) : Adapter<ProductAdapter.VH>(){


    inner class VH(var binding:RecyclerProductItemBinding) : ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerProductItemBinding.inflate(LayoutInflater.from(context),parent,false))
    override fun getItemCount(): Int = mainItems.size
    override fun onBindViewHolder(holder: VH, position: Int) {
        var mainItem : MainItem = mainItems[position]

        Glide.with(context).load(mainItem.image).into(holder.binding.ivMainImg)
        holder.binding.tvTitle.text = mainItem.title
        holder.binding.tvLocationName.text = mainItem.location
        holder.binding.tvPrice.text = mainItem.price

        holder.binding.root.setOnClickListener { clickItem(position) }
    }

    private fun clickItem(position: Int) {
        /*
        *       클릭된 아이템 정보를 넘겨주자. 넘겨주고 HomeDetail 에서
        *       인덱스를 기반으로 비교해서 DB 에서 값 가져오기
        * */
        var intent:Intent = Intent(context,HomeDetailActivity::class.java)
        intent.putExtra("index",position)
        context.startActivity(intent)
    }
}
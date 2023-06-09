package com.cha.auctionapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.cha.auctionapp.G
import com.cha.auctionapp.activities.HomeDetailActivity
import com.cha.auctionapp.databinding.RecyclerProductItemBinding
import com.cha.auctionapp.model.MainItem

class ProductAdapter(var context:Context,var mainItem:MutableList<MainItem>) : Adapter<ProductAdapter.VH>(){


    inner class VH(var binding:RecyclerProductItemBinding) : ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerProductItemBinding.inflate(LayoutInflater.from(context),parent,false))
    override fun getItemCount(): Int = mainItem.size
    override fun onBindViewHolder(holder: VH, position: Int) {
        if(mainItem[position].location == G.location){
            var mainItem : MainItem = mainItem[position]

            var baseAddr = "http://tjdrjs0803.dothome.co.kr/Server/" + mainItem.image.split(",")[0]
            Glide.with(context).load(baseAddr).into(holder.binding.ivMainImg)
            holder.binding.tvTitle.text = mainItem.title
            holder.binding.tvLocationName.text = mainItem.location
            holder.binding.tvPrice.text = "${mainItem.price} 원"
            holder.binding.root.tag = mainItem.idx
        }
        holder.binding.root.setOnClickListener { clickItem(holder) }
    }

    private fun clickItem(holder: VH) {
        /*
        *       클릭된 아이템 정보를 넘겨주자. 넘겨주고 HomeDetail 에서
        *       인덱스를 기반으로 비교해서 DB 에서 값 가져오기
        * */
        var intent:Intent = Intent(context,HomeDetailActivity::class.java)
        intent.putExtra("index",holder.binding.root.tag.toString())
        context.startActivity(intent)
    }
}
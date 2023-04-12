package com.cha.auctionapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.cha.auctionapp.G
import com.cha.auctionapp.activities.CommunityDetailActivity
import com.cha.auctionapp.activities.HomeDetailActivity
import com.cha.auctionapp.databinding.RecyclerCommunityItemBinding
import com.cha.auctionapp.databinding.RecyclerProductItemBinding
import com.cha.auctionapp.model.CommunityPostItem
import com.cha.auctionapp.model.MainItem

class CommunityAdapter(var context:Context, var communityItem:MutableList<CommunityPostItem>) : Adapter<CommunityAdapter.VH>(){


    inner class VH(var binding:RecyclerCommunityItemBinding) : ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerCommunityItemBinding.inflate(LayoutInflater.from(context),parent,false))
    override fun getItemCount(): Int = communityItem.size
    override fun onBindViewHolder(holder: VH, position: Int) {
        if(communityItem[position].location == G.location){
            var item : CommunityPostItem = communityItem[position]

            if(item.image != "") {
                holder.binding.cvImage.visibility = View.VISIBLE
                var baseAddr = "http://tjdrjs0803.dothome.co.kr/Server/" + item.image.split(",")[0]
                Glide.with(context).load(baseAddr).into(holder.binding.ivImage)
            }
            holder.binding.tvTitle.text = item.title
            holder.binding.tvContents.text = item.description
            holder.binding.tvLocation.text = item.location
            holder.binding.tvFavSize.text = item.fav.toString()
            holder.binding.tvCommentsSize.text = item.comments.toString()

            holder.binding.root.tag = item.idx
        }
        holder.binding.root.setOnClickListener { clickItem(holder) }
    }

    private fun clickItem(holder: VH) {
        /*
        *       클릭된 아이템 정보를 넘겨주자. 넘겨주고 HomeDetail 에서
        *       인덱스를 기반으로 비교해서 DB 에서 값 가져오기
        * */
        var intent:Intent = Intent(context, CommunityDetailActivity::class.java)
        intent.putExtra("index",holder.binding.root.tag.toString())
        context.startActivity(intent)
    }
}
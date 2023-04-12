package com.cha.auctionapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.cha.auctionapp.databinding.RecyclerCommunityItemBinding
import com.cha.auctionapp.model.CommunityPostItem

class MyPostListAdapter(var context:Context, var items:MutableList<CommunityPostItem>) : Adapter<MyPostListAdapter.VH>() {

    inner class VH(var binding:RecyclerCommunityItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerCommunityItemBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.tvTitle.text = items[position].title
        holder.binding.tvContents.text = items[position].description
        holder.binding.tvLocation.text = items[position].location
        holder.binding.tvFavSize.text = items[position].fav.toString()
        holder.binding.tvCommentsSize.text = items[position].comments.toString()
        Glide.with(context).load(items[position].image).into(holder.binding.ivImage)

        /*
        *
        *       CommunityDetail 로 이동
        *
        * */
        holder.itemView.setOnClickListener {
            /*
            *       Intent 에게 position 값을 넘겨줘야 할듯.
            *       넘겨준 position 값을 토대로, Detail 액티비티에서 서버에 저장된 커뮤니티 작성글을 불러오자.
            * */
            context.startActivity(Intent(context,
                com.cha.auctionapp.activities.CommunityDetailActivity::class.java))
        }
    }
}
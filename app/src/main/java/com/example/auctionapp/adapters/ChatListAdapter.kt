package com.example.auctionapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.auctionapp.activities.ChattingActivity
import com.example.auctionapp.databinding.RecyclerChatListItemBinding
import com.example.auctionapp.model.MessageItem

class ChatListAdapter(var context: Context, var items: MutableList<MessageItem>) : Adapter<ChatListAdapter.VH>(){

    inner class VH(var binding: RecyclerChatListItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerChatListItemBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        var item = items[position]
        holder.binding.tvNicknameChatList.text = item.id
        holder.binding.tvMsgChatList.text = item.message
        holder.binding.tvTimeChatList.text = item.time
        Glide.with(context).load(item.image).into(holder.binding.civProfileChatList)

        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context,ChattingActivity::class.java))
        }
    }


}
package com.cha.auctionapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.cha.auctionapp.activities.ChattingActivity
import com.cha.auctionapp.databinding.RecyclerChatListItemBinding
import com.cha.auctionapp.model.ChatListItem
import com.cha.auctionapp.model.MessageItem

class ChatListAdapter(var context: Context, var items: MutableList<ChatListItem>) : Adapter<ChatListAdapter.VH>(){

    inner class VH(var binding: RecyclerChatListItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(RecyclerChatListItemBinding.inflate(LayoutInflater.from(context),parent,false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        var item = items[position]
        holder.binding.tvNicknameChatList.text = item.nickname
        holder.binding.tvMsgChatList.text = item.lastMessage
        holder.binding.tvTimeChatList.text = item.time
        Glide.with(context).load(item.profileImage).into(holder.binding.civProfileChatList)

        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, ChattingActivity::class.java)
                .putExtra("otherNickname",item.nickname)
                .putExtra("otherProfile",item.profileImage)
                .putExtra("otherID",item.OtherID))

        }
    }


}
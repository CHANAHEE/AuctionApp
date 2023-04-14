package com.cha.auctionapp.adapters

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.model.MessageItem
import com.cha.auctionapp.model.PictureCommunityDetailItem
import com.cha.auctionapp.model.PictureItem
import com.cha.auctionapp.model.PictureMessageItem

class PictureMessageAdapter(var context: Context, var items: MutableList<PictureItem>,var viewTypeItem: MutableList<MessageItem>) : Adapter<PictureMessageAdapter.VH>() {

    private var TYPE_MY_MESSAGE = 0
    private var TYPE_OTHER_MESSAGE = 1

    inner class VH(itemView: View) : ViewHolder(itemView) {
        var picture = itemView.findViewById<ImageView>(R.id.iv_chat_picture_message)
    }

    override fun getItemViewType(position: Int): Int {
        Log.i("123rde","${viewTypeItem[position].id} : ${G.userAccount.id}")
        if(viewTypeItem[position].id == G.userAccount.id) return TYPE_MY_MESSAGE
        else return TYPE_OTHER_MESSAGE
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        if(viewType == TYPE_MY_MESSAGE) return VH(LayoutInflater.from(context).inflate(R.layout.recycler_chatting_my_picture_message,parent,false))
        else return VH(LayoutInflater.from(context).inflate(R.layout.recycler_chatting_other_picture_message,parent,false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        Glide.with(context).load(items[position].uri).into(holder.picture)
    }
}
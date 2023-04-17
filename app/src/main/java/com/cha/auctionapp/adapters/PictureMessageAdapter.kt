package com.cha.auctionapp.adapters

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.model.MessageItem
import com.cha.auctionapp.model.PictureCommunityDetailItem
import com.cha.auctionapp.model.PictureItem

class PictureMessageAdapter(var context: Context, var id: String, var item: MutableList<Uri>) : Adapter<ViewHolder>() {

    private var TYPE_MY_MESSAGE = 0
    private var TYPE_OTHER_MESSAGE = 1

    inner class MyVH(itemView: View) : ViewHolder(itemView) {
        var myPicture = itemView.findViewById<ImageView>(R.id.iv_chat_picture_message)
    }
    inner class OtherVH(itemView: View) : ViewHolder(itemView) {
        var otherPicture = itemView.findViewById<ImageView>(R.id.iv_chat_picture_message)
    }

    override fun getItemViewType(position: Int): Int {
        Log.i("pictureIssue","PictureMessage 어댑터 getItemView 이미지 사이즈정보 : ${item.size}")
        Log.i("123rde","${id} : ${G.userAccount.id}")
        if(id == G.userAccount.id) return TYPE_MY_MESSAGE
        else return TYPE_OTHER_MESSAGE
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.i("15ee","${viewType}")
        if(viewType == TYPE_MY_MESSAGE) return MyVH(LayoutInflater.from(context).inflate(R.layout.recycler_chatting_my_picture_message,parent,false))
        else return OtherVH(LayoutInflater.from(context).inflate(R.layout.recycler_chatting_other_picture_message,parent,false))
    }

    override fun getItemCount(): Int {
        Log.i("15ee","${item.size}")
        return item.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(holder is MyVH) {
            Log.i("15ee","MYVH : ${item[position]} ")
            Log.i("pictureIssue","PictureMessage 어댑터 onBind")
            Glide.with(context).load(item[position]).into(holder.myPicture)
        }
        else if(holder is OtherVH){
            Log.i("15ee","OTHER : ${item[position]}")
            Log.i("pictureIssue","PictureMessage 어댑터 onBind")
            Glide.with(context).load(item[position]).into(holder.otherPicture)
        }
    }
}
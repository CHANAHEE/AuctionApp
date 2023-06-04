package com.cha.auctionapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.activities.ChattingActivity
import com.cha.auctionapp.activities.MainActivity
import com.cha.auctionapp.databinding.RecyclerChatListItemBinding
import com.cha.auctionapp.model.ChatListItem
import com.google.firebase.firestore.FirebaseFirestore

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

        holder.itemView.setOnLongClickListener {setPopupMenuForExitChatRoom(holder,item,position)}
        holder.itemView.setOnClickListener { moveToChattingRoom(item) }
    }



    /*
    *
    *       채팅방으로 이동
    *
    * */
    private fun moveToChattingRoom(item: ChatListItem){
        context.startActivity(Intent(context, ChattingActivity::class.java)
            .putExtra("otherNickname",item.nickname)
            .putExtra("otherProfile",item.profileImage)
            .putExtra("otherID",item.OtherID)
            .putExtra("title",item.chatRoomInfo.titleProductInfo)
            .putExtra("price",item.chatRoomInfo.priceProductInfo)
            .putExtra("image",item.chatRoomInfo.imageProductInfo)
            .putExtra("location",item.chatRoomInfo.locationProductInfo)
            .putExtra("index",item.productIndex)
        )
    }



    /*
    *
    *       채팅방 나가기 버튼 활성화
    *
    * */
    private fun setPopupMenuForExitChatRoom(holder: VH,item: ChatListItem,position: Int): Boolean{
        var popupMenu = PopupMenu(context,holder.itemView,Gravity.END)
        (context as MainActivity).menuInflater.inflate(R.menu.popupmenu,popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener {
            deleteChatRoom(item,position)
            return@setOnMenuItemClickListener false
        }
        return false
    }



    /*
    *
    *       해당 채팅방 리스트에서 삭제
    *
    * */
    @SuppressLint("NotifyDataSetChanged")
    private fun deleteChatRoom(item: ChatListItem,position: Int){
        FirebaseFirestore.getInstance()
            .collection("chat")
            .document(getDocumentName(item))
            .delete().addOnSuccessListener {
                items.removeAt(position)
                notifyDataSetChanged()
            }
    }


    /*
    *
    *       컬렉션 이름 생성 : 컬렉션이 DB 내 채팅방의 이름 -> 채팅방을 유일하게 구별 가능
    *
    * */
    private fun getDocumentName(item: ChatListItem): String {
        var compareResult = G.userAccount.id.compareTo(item.OtherID)
        return if (compareResult > 0) G.userAccount.id + item.OtherID + item.productIndex
        else if (compareResult < 0) item.OtherID + G.userAccount.id + item.productIndex
        else ""
    }


}
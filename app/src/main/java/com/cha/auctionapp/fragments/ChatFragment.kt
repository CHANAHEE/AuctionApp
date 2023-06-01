package com.cha.auctionapp.fragments

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.adapters.ChatListAdapter
import com.cha.auctionapp.databinding.FragmentChatBinding
import com.cha.auctionapp.model.ChatListItem
import com.cha.auctionapp.model.ChatRoomInfo
import com.cha.auctionapp.model.CommunityDetailItem
import com.cha.auctionapp.model.MessageItem
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import okhttp3.internal.notify
import java.lang.NumberFormatException

class ChatFragment : Fragment() {

    lateinit var binding: FragmentChatBinding
    private val chatListItem: MutableList<ChatListItem> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getChattingInfoFromFirebase()
    }


    private fun getChattingInfoFromFirebase(){
        FirebaseFirestore.getInstance().collection("chat").get().addOnSuccessListener {
            var documentChange = it.documentChanges
            for(document in documentChange){
                val map = document.document.data

                val chatRoomInfo = map["chatRoomInfo"] as HashMap<*, *>
                val productIndex = map["productIndex"].toString()
                val time = map["time"].toString()
                var lastMessage = map["message"].toString()
                val locationInfo = map["location"].toString()
                val imageSize = map["imageSize"].toString()

                if(lastMessage == "" && imageSize != "0") lastMessage = "사진을 보냈습니다"
                else if(lastMessage == "" && locationInfo != "") lastMessage = "지도 : $locationInfo"

                if(G.userAccount.id == map["id"].toString()){

                    val otherID = map["otherID"].toString()
                    val nickname = map["otherNickname"].toString()
                    val profileImage = map["otherProfileImage"].toString()
                    addChatListItem(chatRoomInfo,productIndex,nickname, profileImage, lastMessage, time, otherID)

                }else if(G.userAccount.id == map["otherID"].toString()){

                    val otherID = map["id"].toString()
                    val nickname = map["nickname"].toString()
                    val profileImage = map["profileImage"].toString()
                    addChatListItem(chatRoomInfo,productIndex,nickname, profileImage, lastMessage, time, otherID)
                }
            }
        }
    }

    private fun addChatListItem(chatRoomInfo: HashMap<*,*>
                                ,productIndex: String
                                ,nickname: String
                                ,profileImage: String
                                ,lastMessage: String
                                ,time: String
                                ,otherID: String){
        chatListItem.add(ChatListItem(productIndex,nickname, profileImage, lastMessage, time,otherID,
            ChatRoomInfo(
                chatRoomInfo["titleProductInfo"].toString(),
                chatRoomInfo["locationProductInfo"].toString(),
                chatRoomInfo["priceProductInfo"].toString(),
                chatRoomInfo["imageProductInfo"].toString()
            )))
        binding.recycler.adapter = ChatListAdapter(requireContext(),chatListItem)
    }
}


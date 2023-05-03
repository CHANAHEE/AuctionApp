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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        getChattingInfoFromFirebase()
    }


    private fun getChattingInfoFromFirebase(){
        var chatListItem: MutableList<ChatListItem> = mutableListOf()
        var firestore = FirebaseFirestore.getInstance()
        var chatListRef = firestore.collection("chat").get().addOnSuccessListener {
            var documentChange = it.documentChanges
            for(document in documentChange){
                var snapshot = document.document
                var map = snapshot.data
                var productIndex = map.get("productIndex").toString()
                var lastMessage = map.get("message").toString()
                if(map.get("message").toString() == "" && map.get("imageSize").toString() != "0"){
                    lastMessage = "사진을 보냈습니다"
                }else if(map.get("message").toString() == "" && map.get("location").toString() != ""){
                    lastMessage = "지도 : ${map.get("location").toString()}"
                }

                var time = map.get("time").toString()
                var chatRoomInfo = map.get("chatRoomInfo") as HashMap<*, *>

                if(G.userAccount.id == map.get("id").toString()){

                    var otherID = map.get("otherID").toString()
                    var nickname = map.get("otherNickname").toString()
                    var profileImage = map.get("otherProfileImage").toString()

                    chatListItem.add(ChatListItem(productIndex,nickname, profileImage, lastMessage, time,otherID,
                        ChatRoomInfo(
                            chatRoomInfo["titleProductInfo"].toString(),
                            chatRoomInfo["locationProductInfo"].toString(),
                            chatRoomInfo["priceProductInfo"].toString(),
                            chatRoomInfo["imageProductInfo"].toString()
                        )
                    ))
                    //binding.recycler.adapter?.notifyItemInserted(chatListItem.size)
                    binding.recycler.adapter = ChatListAdapter(requireContext(),chatListItem)

                }else if(G.userAccount.id == map.get("otherID").toString()){

                    var otherID = map.get("id").toString()
                    var nickname = map.get("nickname").toString()
                    var profileImage = map.get("profileImage").toString()

                    chatListItem.add(ChatListItem(productIndex,nickname, profileImage, lastMessage, time,otherID,
                        ChatRoomInfo(
                            chatRoomInfo["titleProductInfo"].toString(),
                            chatRoomInfo["locationProductInfo"].toString(),
                            chatRoomInfo["priceProductInfo"].toString(),
                            chatRoomInfo["imageProductInfo"].toString()
                        )))
                    //binding.recycler.adapter?.notifyItemInserted(chatListItem.size)
                    binding.recycler.adapter = ChatListAdapter(requireContext(),chatListItem)

                }
            }
        }
    }
}


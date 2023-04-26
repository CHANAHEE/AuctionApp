package com.cha.auctionapp.fragments

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
import java.lang.NumberFormatException

class ChatFragment : Fragment() {

    lateinit var binding: FragmentChatBinding


    lateinit var chatListItem: MutableList<ChatListItem>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatListItem = mutableListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycler.adapter = ChatListAdapter(requireContext(),chatListItem)
        getChattingInfoFromFirebase()
    }



    private fun getChattingInfoFromFirebase(){
        var firestore = FirebaseFirestore.getInstance()
        var chatListRef = firestore.collection("chat").get().addOnSuccessListener {
            var documentChange = it.documentChanges
            for(document in documentChange){
                var snapshot = document.document
                var map = snapshot.data

                var productIndex = map.get("productIndex").toString()
                var lastMessage = map.get("message").toString()
                var nickname = map.get("otherNickname").toString()
                var profileImage = map.get("otherProfileImage").toString()
                var time = map.get("time").toString()
                var chatRoomInfo = map.get("chatRoomInfo") as HashMap<*, *>

                if(G.userAccount.id == map.get("id").toString()){
                    var otherID = map.get("otherID").toString()
                    chatListItem.add(ChatListItem(productIndex,nickname, profileImage, lastMessage, time,otherID,
                        ChatRoomInfo(
                            chatRoomInfo["titleProductInfo"].toString(),
                            chatRoomInfo["locationProductInfo"].toString(),
                            chatRoomInfo["priceProductInfo"].toString(),
                            chatRoomInfo["imageProductInfo"].toString()
                        )
                    ))
                    binding.recycler.adapter?.notifyItemInserted(chatListItem.size)
                }else if(G.userAccount.id == map.get("otherID").toString()){
                    var otherID = map.get("id").toString()
                    chatListItem.add(ChatListItem(productIndex,nickname, profileImage, lastMessage, time,otherID,
                        ChatRoomInfo(
                            chatRoomInfo["titleProductInfo"].toString(),
                            chatRoomInfo["locationProductInfo"].toString(),
                            chatRoomInfo["priceProductInfo"].toString(),
                            chatRoomInfo["imageProductInfo"].toString()
                        )))
                    binding.recycler.adapter?.notifyItemInserted(chatListItem.size)
                }
            }
        }

//        Log.i("chatList","chatListRef : ${chatListRef}")
//        chatListRef.get().addOnSuccessListener {
//            Log.i("chatList",it.documents.size.toString())
//        }.addOnFailureListener {
//            Log.i("chatList",it.message.toString())
//        }
    }
//    private fun createFirebaseCollectionName() {
//        var compareResult = G.userAccount.id.compareTo(otherID)
//        collectionName = if(compareResult > 0) G.userAccount.id + otherID
//        else if(compareResult < 0) otherID + G.userAccount.id
//        else null
//    }
}
//            for (documentChange in documentChanges) {
//
//                var snapshot = documentChange.document
//                var map = snapshot.data
//
//                var nickname = map.get("nickname").toString()
//                var id = map.get("id").toString()
//                var message = map.get("message").toString()
//                var time = map.get("time").toString()
//                var profileImage = Uri.parse(map.get("profileImage").toString())
//                var image = map.get("image") as MutableList<*>
//                var imageSize: String? = map.get("imageSize").toString()
//                var location = map.get("location").toString()
//                var messageIndex = map.get("messageIndex").toString()
//
//                for(i in 0 until image.size){
//                    pictureItem.add(Uri.parse(image[i].toString()))
//                }
//                var newPictureItem = pictureItem.toMutableList()
//
//                try{
//                    messageItem.add(MessageItem( nickname,id, message, time,profileImage,newPictureItem,imageSize?.toInt() ?: 0,location,messageIndex?.toInt() ?: 0,lastOtherMessageIndex))
//                }catch (e: NumberFormatException){
//                    messageItem.add(MessageItem( nickname,id, message, time,profileImage,newPictureItem,0,location, 0,lastOtherMessageIndex))
//                }
//                pictureItem.clear()
//            }
//            binding.recycler.adapter?.notifyItemInserted(messageItem.size)
//            binding.recycler.scrollToPosition(messageItem.size-1)



//
//    private fun loadProfileFromFirestore(item: ChatListItem){
//        var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
//        var userRef: CollectionReference = firestore.collection("user")
//
//        userRef.document(item.id).get().addOnSuccessListener {
//
//            return@addOnSuccessListener
//        }
//    }

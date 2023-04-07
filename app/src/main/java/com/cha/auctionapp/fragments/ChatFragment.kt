package com.cha.auctionapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cha.auctionapp.R
import com.cha.auctionapp.adapters.ChatListAdapter
import com.cha.auctionapp.databinding.FragmentChatBinding
import com.cha.auctionapp.model.MessageItem

class ChatFragment : Fragment() {

    lateinit var binding: FragmentChatBinding
    lateinit var chatItem: MutableList<MessageItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chatItem = mutableListOf()
        chatItem.add(MessageItem(R.drawable._0,"0번","0번 입니다.","오후 14:12"))
        chatItem.add(MessageItem(R.drawable._1,"1번","1번 입니다.","오후 13:57"))
        chatItem.add(MessageItem(R.drawable._2,"2번","2번 입니다.","오후 12:12"))
        chatItem.add(MessageItem(R.drawable._3,"3번","3번 입니다.","오전 11:23"))
        chatItem.add(MessageItem(R.drawable._4,"4번","4번 입니다.","4월 1일"))
        chatItem.add(MessageItem(R.drawable._5,"5번","5번 입니다.","4월 1일"))
        chatItem.add(MessageItem(R.drawable._6,"6번","6번 입니다.","4월 1일"))
        chatItem.add(MessageItem(R.drawable._7,"7번","7번 입니다.","3월 29일"))
        chatItem.add(MessageItem(R.drawable._8,"8번","8번 입니다.","3월 29일"))
        chatItem.add(MessageItem(R.drawable._9,"9번","9번 입니다.","3월 29일"))
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
        binding.recycler.adapter = ChatListAdapter(requireContext(),chatItem)
    }

}
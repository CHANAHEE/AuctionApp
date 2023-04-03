package com.example.auctionapp.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.auctionapp.adapters.AuctionPagerAdapter
import com.example.auctionapp.databinding.FragmentAuctionBinding
import com.example.auctionapp.model.AuctionPagerItem
import com.example.auctionapp.R

class AuctionFragment : Fragment() {

    lateinit var binding: FragmentAuctionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAuctionBinding.inflate(inflater,container,false)
        return binding.root
    }

    var videoUri =
        Uri.parse("https://www.shutterstock.com/shutterstock/videos/1084218295/preview/stock-footage-futuristic-animated-concept-big-data-center-chief-technology-officer-using-laptop-standing-in.webm")

    lateinit var items: MutableList<AuctionPagerItem>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        items = mutableListOf()
        items.add(AuctionPagerItem(videoUri,R.drawable._0,"1번","안녕하세요 1번 입니다."))
        items.add(AuctionPagerItem(videoUri,R.drawable._1,"2번","안녕하세요 2번 입니다."))
        items.add(AuctionPagerItem(videoUri,R.drawable._2,"3번","안녕하세요 3번 입니다."))
        items.add(AuctionPagerItem(videoUri,R.drawable._3,"4번","안녕하세요 4번 입니다."))
        items.add(AuctionPagerItem(videoUri,R.drawable._4,"5번","안녕하세요 5번 입니다."))
        items.add(AuctionPagerItem(videoUri,R.drawable._5,"6번","안녕하세요 6번 입니다."))
        items.add(AuctionPagerItem(videoUri,R.drawable._6,"7번","안녕하세요 7번 입니다."))
        binding.pager.adapter = AuctionPagerAdapter(requireContext(), items)
    }
}
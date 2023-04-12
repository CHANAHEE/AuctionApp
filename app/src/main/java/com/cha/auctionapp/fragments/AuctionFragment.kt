package com.cha.auctionapp.fragments

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cha.auctionapp.adapters.AuctionPagerAdapter
import com.cha.auctionapp.databinding.FragmentAuctionBinding
import com.cha.auctionapp.model.AuctionPagerItem
import com.cha.auctionapp.R
import com.cha.auctionapp.adapters.PictureCommunityDetailAdapter
import com.cha.auctionapp.model.CommunityDetailItem
import com.cha.auctionapp.model.PictureCommunityDetailItem
import com.cha.auctionapp.network.RetrofitHelper
import com.cha.auctionapp.network.RetrofitService
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        items.add(AuctionPagerItem(videoUri,R.drawable._0,"HELLO","안녕"))
        binding.pager.adapter = AuctionPagerAdapter(requireContext(), items)
    }

    override fun onResume() {
        super.onResume()
        //loadDataFromServer()
    }


    private fun loadDataFromServer(){
        val retrofit = RetrofitHelper.getRetrofitInstance("http://tjdrjs0803.dothome.co.kr")
        val retrofitService = retrofit.create(RetrofitService::class.java)
        val call: Call<MutableList<AuctionPagerItem>> = retrofitService.getDataFromServerForAuctionFragment(activity?.intent?.getStringExtra("index")!!)
        call.enqueue(object : Callback<MutableList<AuctionPagerItem>> {
            override fun onResponse(
                call: Call<MutableList<AuctionPagerItem>>,
                response: Response<MutableList<AuctionPagerItem>>
            ) {
                // 데이터 받아와서 어댑터에 뿌리기
                items = response.body()!!
                binding.pager.adapter = AuctionPagerAdapter(requireContext(), items)
            }

            override fun onFailure(call: Call<MutableList<AuctionPagerItem>>, t: Throwable) {
                Log.i("test01","${t.message}")
            }
        })
    }




}
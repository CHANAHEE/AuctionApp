package com.example.auctionapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.auctionapp.R
import com.example.auctionapp.activities.SellingEditActivity
import com.example.auctionapp.adapters.ProductAdapter
import com.example.auctionapp.databinding.FragmentHomeBinding
import com.example.auctionapp.model.MainItem


class HomeFragment : Fragment() {

    lateinit var binding : FragmentHomeBinding
    lateinit var mainItems : MutableList<MainItem>

    /*
    *
    *       원래 서버에 저장된, 동네 이름을 기반으로 기본값을 정해주어야 함.
    *       그리고 나서, 동네를 바꿀 때는 Activity->Fragment 로 정보전달이 이루어져야함.
    *       그래서 다른 Fragment 에서 HomeFragment 로 넘어올 때 기본 동네 설정이 안되어있어 에러가 남.
    * */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var placeInfo = arguments?.getString("place")

        if(placeInfo == null){
            setLocation("공릉 1동")
        }else{
            setLocation(placeInfo)
        }

    }

    fun setLocation(placeInfo: String) {
        mainItems = mutableListOf()
        if(placeInfo.equals("공릉 1동")){
            mainItems.clear()
            // 공릉 1동 데이터
            /*
            *       여기에서 서버에 저장된 동네데이터를 불러와야 할듯...
            * */
            mainItems.add(MainItem("MainItem 1",R.drawable._0,"공릉 1동","34000원"))
            mainItems.add(MainItem("MainItem 2",R.drawable._1,"공릉 1동","34000원"))
            mainItems.add(MainItem("MainItem 3",R.drawable._2,"공릉 1동","34000원"))
            mainItems.add(MainItem("MainItem 4",R.drawable._3,"공릉 1동","34000원"))
            mainItems.add(MainItem("MainItem 5",R.drawable._4,"공릉 1동","34000원"))
            mainItems.add(MainItem("MainItem 6",R.drawable._5,"공릉 1동","34000원"))
            mainItems.add(MainItem("MainItem 7",R.drawable._6,"공릉 1동","34000원"))
            mainItems.add(MainItem("MainItem 8",R.drawable._7,"공릉 1동","34000원"))
            mainItems.add(MainItem("MainItem 9",R.drawable._8,"공릉 1동","34000원"))
        }else if(placeInfo.equals("공릉 2동")){
            mainItems.clear()
            // 공릉 2동 데이터
            mainItems.add(MainItem("MainItem 10",R.drawable._9,"공릉 2동","34000원"))
            mainItems.add(MainItem("MainItem 11",R.drawable._6,"공릉 2동","34000원"))
            mainItems.add(MainItem("MainItem 12",R.drawable._9,"공릉 2동","34000원"))
            mainItems.add(MainItem("MainItem 13",R.drawable._9,"공릉 2동","34000원"))
            mainItems.add(MainItem("MainItem 14",R.drawable._4,"공릉 2동","34000원"))
            mainItems.add(MainItem("MainItem 15",R.drawable._6,"공릉 2동","34000원"))
            mainItems.add(MainItem("MainItem 16",R.drawable._2,"공릉 2동","34000원"))
            mainItems.add(MainItem("MainItem 17",R.drawable._6,"공릉 2동","34000원"))
            mainItems.add(MainItem("MainItem 18",R.drawable._1,"공릉 2동","34000원"))
        }


    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycler.adapter = ProductAdapter(requireContext(),mainItems)
        binding.fabEdit.setOnClickListener { startActivity(Intent(context,SellingEditActivity::class.java)) }



    }



}
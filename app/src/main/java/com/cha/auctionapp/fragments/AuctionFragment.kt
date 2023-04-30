package com.cha.auctionapp.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.cha.auctionapp.G
import com.cha.auctionapp.adapters.AuctionPagerAdapter
import com.cha.auctionapp.databinding.FragmentAuctionBinding
import com.cha.auctionapp.model.AuctionPagerItem
import com.cha.auctionapp.R
import com.cha.auctionapp.activities.MainActivity
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
    lateinit var items: MutableList<AuctionPagerItem>

    override fun onStart() {
        super.onStart()
        var activity = activity as MainActivity
        activity.window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.black)
        WindowInsetsControllerCompat(activity.window, activity.window.decorView).isAppearanceLightStatusBars = false
        activity.binding.bnv.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#000000"))

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuctionBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        items = mutableListOf()
        items.add(AuctionPagerItem(0,"https://www.shutterstock.com/shutterstock/videos/1073719175/preview/stock-footage-adorable-white-cat-in-sunglasses-and-an-shirt-lies-on-a-fabric-hammock-on-a-yellow-background.webm","HI","공릉1동","첫번째 쇼츠를 올려주세요", G.userAccount.id,"0"))
        binding.pager.adapter = AuctionPagerAdapter(requireContext(), items)
        binding.refreshLayout.setOnRefreshListener { clickRefresh() }
    }

    override fun onResume() {
        super.onResume()
        loadDataFromServer()
    }


    /*
    *
    *       MainActivity 의 UI 흰색으로 변경
    *
    * */
    override fun onDestroy() {
        super.onDestroy()
        var activity = activity as MainActivity
        activity.window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        WindowInsetsControllerCompat(activity.window, activity.window.decorView).isAppearanceLightStatusBars = true
        activity.binding.bnv.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
    }




    /*
    *
    *       서버에서 데이터 받아오기
    *
    * */
    private fun loadDataFromServer(){
        val retrofit = RetrofitHelper.getRetrofitInstance("http://tjdrjs0803.dothome.co.kr")
        val retrofitService = retrofit.create(RetrofitService::class.java)
        val call: Call<MutableList<AuctionPagerItem>> = retrofitService.getDataFromServerForAuctionFragment()
        call.enqueue(object : Callback<MutableList<AuctionPagerItem>> {
            override fun onResponse(
                call: Call<MutableList<AuctionPagerItem>>,
                response: Response<MutableList<AuctionPagerItem>>
            ) {
                if(activity == null || !isAdded) return

                items = response.body()!!
                items.sortByDescending { it.idx }
                if(items.size != 0) binding.pager.adapter = AuctionPagerAdapter(requireContext(), items)
                binding.refreshLayout.isRefreshing = false
            }

            override fun onFailure(call: Call<MutableList<AuctionPagerItem>>, t: Throwable) {
                Log.i("test01","ERROR : ${t.message}")
            }
        })
    }


    /*
    *
    *       리프레시
    *
    * */
    private fun clickRefresh() = loadDataFromServer()
}
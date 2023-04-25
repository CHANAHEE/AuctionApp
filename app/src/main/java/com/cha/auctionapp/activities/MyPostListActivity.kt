package com.cha.auctionapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.adapters.MyPostListAdapter
import com.cha.auctionapp.adapters.ProductAdapter
import com.cha.auctionapp.databinding.ActivityMyPostListBinding
import com.cha.auctionapp.model.CommunityPostItem
import com.cha.auctionapp.model.MainItem
import com.cha.auctionapp.model.MyPostListItem
import com.cha.auctionapp.network.RetrofitHelper
import com.cha.auctionapp.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPostListActivity : AppCompatActivity() {
    lateinit var binding : ActivityMyPostListBinding
    lateinit var listItems : MutableList<MyPostListItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPostListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        listItems = mutableListOf()
        loadData()
    }

    /*
    *
    *       데이터 로드하기
    *
    * */
    private fun loadData() {
        val retrofit = RetrofitHelper.getRetrofitInstance("http://tjdrjs0803.dothome.co.kr")
        val retrofitService = retrofit.create(RetrofitService::class.java)
        val call: Call<MutableList<MyPostListItem>> = retrofitService.getDataFromServerForMyPostList(G.userAccount.id)
        Log.i("test19", G.location)
        call.enqueue(object : Callback<MutableList<MyPostListItem>> {
            override fun onResponse(
                call: Call<MutableList<MyPostListItem>>,
                response: Response<MutableList<MyPostListItem>>
            ) {
                listItems = response.body()!!
                if(listItems.isNotEmpty()) binding.tvNone.visibility = View.GONE
                binding.recycler.adapter = MyPostListAdapter(this@MyPostListActivity,listItems)
            }

            override fun onFailure(call: Call<MutableList<MyPostListItem>>, t: Throwable) {
                Log.i("test01","${t.message}")
            }
        })
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
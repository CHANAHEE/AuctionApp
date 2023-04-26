package com.cha.auctionapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.cha.auctionapp.G
import com.cha.auctionapp.adapters.MyAuctionCompleteListAdapter
import com.cha.auctionapp.adapters.MyAuctionPostListAdapter
import com.cha.auctionapp.adapters.MyCommunityPostListAdapter
import com.cha.auctionapp.adapters.MyPostListAdapter
import com.cha.auctionapp.databinding.ActivityMyPostListBinding
import com.cha.auctionapp.model.MyAuctionPostList
import com.cha.auctionapp.model.MyCommunityPostList
import com.cha.auctionapp.model.MyPostListItem
import com.cha.auctionapp.network.RetrofitHelper
import com.cha.auctionapp.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPostListActivity : AppCompatActivity() {
    lateinit var binding : ActivityMyPostListBinding
    lateinit var myPostlistItems : MutableList<MyPostListItem>
    lateinit var myCommunityPostlistItems : MutableList<MyCommunityPostList>
    lateinit var myAuctionPostlistItems : MutableList<MyAuctionPostList>

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

        myPostlistItems = mutableListOf()
        myCommunityPostlistItems = mutableListOf()
        myAuctionPostlistItems = mutableListOf()
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
        when(intent.getStringExtra("navigation")){
            "mypost"        -> loadMyPostList(retrofitService)
            "mycommunity"   -> loadMyCommunityPostList(retrofitService)
            "mybidpost"     -> loadMyBidPostList(retrofitService)
            "mybidcomplete" -> Toast.makeText(this, "낙찰목록 구현 예정", Toast.LENGTH_SHORT).show()
        }
    }

    /*
    *
    *       내 판매글 로드하기
    *
    * */
    private fun loadMyPostList(retrofitService: RetrofitService) {
        val call: Call<MutableList<MyPostListItem>> = retrofitService.getDataFromServerForMyPostList(G.userAccount.id)
        call.enqueue(object : Callback<MutableList<MyPostListItem>> {
            override fun onResponse(
                call: Call<MutableList<MyPostListItem>>,
                response: Response<MutableList<MyPostListItem>>
            ) {
                myPostlistItems = response.body()!!
                if(myPostlistItems.isNotEmpty()) binding.tvNone.visibility = View.GONE
                binding.recycler.adapter = MyPostListAdapter(this@MyPostListActivity,myPostlistItems)
            }

            override fun onFailure(call: Call<MutableList<MyPostListItem>>, t: Throwable) {
                Log.i("test01","${t.message}")
            }
        })
    }



    /*
    *
    *       내 커뮤니티 글 로드하기
    *
    * */
    private fun loadMyCommunityPostList(retrofitService: RetrofitService) {
        val call: Call<MutableList<MyCommunityPostList>> = retrofitService.getDataFromServerForMyCommunityPostList(G.userAccount.id)
        call.enqueue(object : Callback<MutableList<MyCommunityPostList>> {
            override fun onResponse(
                call: Call<MutableList<MyCommunityPostList>>,
                response: Response<MutableList<MyCommunityPostList>>
            ) {
                myCommunityPostlistItems = response.body()!!
                if(myCommunityPostlistItems.isNotEmpty()) binding.tvNone.visibility = View.GONE
                binding.recycler.adapter = MyCommunityPostListAdapter(this@MyPostListActivity,myCommunityPostlistItems)
            }

            override fun onFailure(call: Call<MutableList<MyCommunityPostList>>, t: Throwable) {
                Log.i("test01","${t.message}")
            }
        })
    }



    /*
    *
    *       내 경매 글 로드하기
    *
    * */
    private fun loadMyBidPostList(retrofitService: RetrofitService) {
        val call: Call<MutableList<MyAuctionPostList>> = retrofitService.getDataFromServerForMyAuctionPostList(G.userAccount.id)
        call.enqueue(object : Callback<MutableList<MyAuctionPostList>> {
            override fun onResponse(
                call: Call<MutableList<MyAuctionPostList>>,
                response: Response<MutableList<MyAuctionPostList>>
            ) {
                myAuctionPostlistItems = response.body()!!
                if(myAuctionPostlistItems.isNotEmpty()) binding.tvNone.visibility = View.GONE
                binding.recycler.adapter = MyAuctionPostListAdapter(this@MyPostListActivity,myAuctionPostlistItems)
            }

            override fun onFailure(call: Call<MutableList<MyAuctionPostList>>, t: Throwable) {
                Log.i("test01","${t.message}")
            }
        })
    }




    /*
    *
    *       내 낙찰 물건 로드하기 -> 낙찰이 되었다는건, 시간이 종료되었을 때.. 서버에 데이터 올려놓기. 올려진 데이터에서 낙찰 물건 로드.
    *
    * */
    private fun loadMyBidCompleteList(retrofitService: RetrofitService) {
        val call: Call<MutableList<MyAuctionPostList>> = retrofitService.getDataFromServerForMyAuctionPostList(G.userAccount.id)
        call.enqueue(object : Callback<MutableList<MyAuctionPostList>> {
            override fun onResponse(
                call: Call<MutableList<MyAuctionPostList>>,
                response: Response<MutableList<MyAuctionPostList>>
            ) {
                myAuctionPostlistItems = response.body()!!
                if(myAuctionPostlistItems.isNotEmpty()) binding.tvNone.visibility = View.GONE
                binding.recycler.adapter = MyAuctionCompleteListAdapter(this@MyPostListActivity,myAuctionPostlistItems)
            }

            override fun onFailure(call: Call<MutableList<MyAuctionPostList>>, t: Throwable) {
                Log.i("test01","${t.message}")
            }
        })
    }























    /*
    *
    *       뒤로 가기 버튼
    *
    * */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
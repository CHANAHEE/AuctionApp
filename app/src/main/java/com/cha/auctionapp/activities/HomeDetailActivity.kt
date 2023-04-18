package com.cha.auctionapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.adapters.PagerAdapter
import com.cha.auctionapp.databinding.ActivityHomeDetailBinding
import com.cha.auctionapp.model.HomeDetailItem
import com.cha.auctionapp.model.PagerItem
import com.cha.auctionapp.network.RetrofitHelper
import com.cha.auctionapp.network.RetrofitService
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeDetailBinding
    lateinit var items: MutableList<HomeDetailItem>

    lateinit var otherID: String
    lateinit var otherProfile: String

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        binding.btnBack.setOnClickListener { finish() }
        binding.ibFav.setOnClickListener { clickFavoriteBtn() }
        binding.btnChat.setOnClickListener { clickChatBtn() }
        //binding.pager.adapter = PagerAdapter(this,items)
        loadDataFromServer()

        // status bar 투명으로 만들기 : theme.xml , manifest 파일
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        else window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }


    /*
    *
    *       load data from server
    *
    * */
    private fun loadDataFromServer(){
        val retrofit = RetrofitHelper.getRetrofitInstance("http://tjdrjs0803.dothome.co.kr")
        val retrofitService = retrofit.create(RetrofitService::class.java)
        Log.i("test001",intent.getStringExtra("index")!!)
        val call: Call<MutableList<HomeDetailItem>> = retrofitService.getDataFromServerForHomeDetail(intent.getStringExtra("index")!!)
        call.enqueue(object : Callback<MutableList<HomeDetailItem>> {
            override fun onResponse(
                call: Call<MutableList<HomeDetailItem>>,
                response: Response<MutableList<HomeDetailItem>>
            ) {
                items = mutableListOf()
                items = response.body()!!
                var item = items[0]

                //loadProfileFromFirestore(item.id)
                binding.tvTownInfo.text = item.location
                binding.tvItemName.text = item.title
                binding.tvCategory.text = item.category
                binding.tvDescription.text = item.description
                binding.tvPrice.text = "${item.price} 원"

                if(item.tradingplace != ""){
                    binding.relativeLocation.visibility = View.VISIBLE
                    binding.tvLocationName.text = item.tradingplace
                }

                var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
                var userRef: CollectionReference = firestore.collection("user")

                otherID = items[0].id
                userRef.document(items[0].id).get().addOnSuccessListener {
                    binding.tvId.text = it.get("nickname").toString()
                    Glide.with(this@HomeDetailActivity)
                        .load(it.get("profileImage"))
                        .error(R.drawable.default_profile)
                        .into(binding.civProfile)
                    if(binding.tvId.text == G.nickName){
                        binding.btnChat.visibility = View.GONE
                        /*
                        *          if 문 조건이 내글인지 판단하는 내용이므로,
                        *          채팅버튼 대신 상품에 대해 채팅하고 있는 채팅 내역 보여주는 버튼 만들기
                        * */
                    }
                    otherProfile = it.get("profileImage").toString()
                    return@addOnSuccessListener
                }

                var imageListString = item.image.split(",")
                var imageListUri: MutableList<PagerItem> = mutableListOf()
                for(i in imageListString.indices){
                    imageListUri.add(PagerItem(Uri.parse(imageListString[i])))
                }
                binding.pager.adapter = PagerAdapter(this@HomeDetailActivity,imageListUri)
                binding.dotsIndicator.attachTo(binding.pager)
            }

            override fun onFailure(call: Call<MutableList<HomeDetailItem>>, t: Throwable) {
            }
        })
    }


    /*
    *
    *       채팅하기 버튼 이벤트
    *
    * */
    private fun clickChatBtn() {
        Log.i("profilecheck", otherProfile)
        startActivity(Intent(this, ChattingActivity::class.java)
            .putExtra("otherNickname",binding.tvId.text.toString())
            .putExtra("otherProfile",otherProfile.toString())
            .putExtra("otherID",otherID)
        )
    }

    /*
    *
    *       찜 버튼 이벤트 : 찜을 하면 DB 에 정보를 저장시키고, 관심목록에 추가할 수 있도록 한다.
    *
    * */
    private fun clickFavoriteBtn() { binding.ibFav.isSelected = !binding.ibFav.isSelected }

}
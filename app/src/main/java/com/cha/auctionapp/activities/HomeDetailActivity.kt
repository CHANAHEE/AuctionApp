package com.cha.auctionapp.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.bumptech.glide.Glide
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.adapters.MyFavoriteAdapter
import com.cha.auctionapp.adapters.PagerAdapter
import com.cha.auctionapp.databinding.ActivityHomeDetailBinding
import com.cha.auctionapp.model.AppDatabase
import com.cha.auctionapp.model.HomeDetailItem
import com.cha.auctionapp.model.MyFavListItem
import com.cha.auctionapp.model.PagerItem
import com.cha.auctionapp.network.RetrofitHelper
import com.cha.auctionapp.network.RetrofitService
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
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

                binding.tvTownInfo.text = item.location
                binding.tvItemName.text = item.title
                binding.tvCategory.text = item.category
                binding.tvDescription.text = item.description
                binding.tvPrice.text = "${item.price} 원"

                // 장소 정보
                if(item.tradingplace != ""){
                    binding.relativeLocation.visibility = View.VISIBLE
                    binding.tvLocationName.text = item.tradingplace
                }
//adfh
                // 이미지 정보
                var imageListString = item.image.split(",")
                var imageListUri: MutableList<PagerItem> = mutableListOf()
                for(i in imageListString.indices){
                    imageListUri.add(PagerItem(Uri.parse(imageListString[i])))
                }
                binding.pager.adapter = PagerAdapter(this@HomeDetailActivity,imageListUri)
                binding.dotsIndicator.attachTo(binding.pager)

                loadProfileFromFirebase()
                loadMyFavItem()
            }
            override fun onFailure(call: Call<MutableList<HomeDetailItem>>, t: Throwable) {
            }
        })
    }

    /*
    *
    *       프로필 정보 로드해오기
    *
    * */
    private fun loadProfileFromFirebase(){
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
    }


    /*
    *
    *       Room DB 활용한 찜기능 구현
    *
    * */

    private fun loadMyFavItem(){
        val db = Room.databaseBuilder(
            this@HomeDetailActivity,
            AppDatabase::class.java, "fav-database"
        ).build()

        val r = Runnable {
            // Query 를 이용해서 가지고 있는 인덱스의 값이 현재 페이지와 같은지 체크해서 있으면 찜된 목록임.
            var myFavListItem = db.myFavListItemDAO().getAll()
            var myFavMutable = myFavListItem.toMutableList()
            val index = intent.getStringExtra("index")!!.toInt()
            for(i in 0 until myFavMutable.size){
                if("$index${G.userAccount.id}" == "${myFavMutable[i].idx}") {
                    binding.ibFav.isSelected = true
                    break
                }
            }
        }
        Thread(r).start()
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

    private fun clickFavoriteBtn() {
        val db = Room.databaseBuilder(
            this,
            AppDatabase::class.java, "fav-database"
        ).build()
        when(binding.ibFav.isSelected){
            true->{
                binding.ibFav.isSelected = false
                Snackbar.make(binding.root,"관심목록에서 삭제되었습니다.",Snackbar.LENGTH_SHORT).show()
                deleteMyFavData(db)
            }
            else->{
                binding.ibFav.isSelected = true
                Snackbar.make(binding.root,"관심목록에 추가되었습니다.",Snackbar.LENGTH_SHORT).show()
                insertMyFavData(db)
            }
        }
    }

    private fun deleteMyFavData(db: AppDatabase){
        val r = Runnable {
            db.myFavListItemDAO()
                .delete(MyFavListItem(
                    "${intent.getStringExtra("index")!!.toInt()}${G.userAccount.id}",
                    intent.getStringExtra("index")!!.toInt(),
                    items[0].title,
                    items[0].location,
                    items[0].price,
                    items[0].image)
                )
        }
        Thread(r).start()
    }

    private fun insertMyFavData(db: AppDatabase){
        val r = Runnable{
            db.myFavListItemDAO()
                .insert(MyFavListItem(
                    "${intent.getStringExtra("index")!!.toInt()}${G.userAccount.id}",
                    intent.getStringExtra("index")!!.toInt(),
                    items[0].title,
                    items[0].location,
                    items[0].price,
                    items[0].image)
                )
        }
        Thread(r).start()
    }
}
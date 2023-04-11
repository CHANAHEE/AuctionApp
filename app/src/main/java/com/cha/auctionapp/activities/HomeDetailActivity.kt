package com.cha.auctionapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.content.CursorLoader
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
import com.google.firebase.storage.FirebaseStorage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeDetailBinding
    lateinit var items: MutableList<HomeDetailItem>

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.ibFav.setOnClickListener { clickFavoriteBtn() }
        binding.btnChat.setOnClickListener { clickChatBtn() }

        //binding.pager.adapter = PagerAdapter(this,items)
        loadDataFromServer()

        // status bar 투명으로 만들기
        /*
        *       theme.xml , manifest 파일
        * */
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
        Log.i("test001",retrofitService.toString())
        val call: Call<MutableList<HomeDetailItem>> = retrofitService.getDataFromServerForHomeDetail(intent.getStringExtra("index")!!)
        call.enqueue(object : Callback<MutableList<HomeDetailItem>> {
            override fun onResponse(
                call: Call<MutableList<HomeDetailItem>>,
                response: Response<MutableList<HomeDetailItem>>
            ) {
                Log.i("test143",response.body().toString())
                items = mutableListOf()
                items = response.body()!!
                loadProfileFromFirestore()
                binding.tvId.text = items[0].nickname
                binding.tvTownInfo.text = items[0].location
                binding.tvItemName.text = items[0].title
                binding.tvCategory.text = items[0].category
                binding.tvDescription.text = items[0].description
                binding.tvLocationName.text = items[0].tradingplace
                binding.tvPrice.text = items[0].price
                var imageListString = items[0].image.split(",")

                var imageListUri: MutableList<PagerItem> = mutableListOf()
                for(i in imageListString.indices){
                    imageListUri.add(PagerItem(Uri.parse(imageListString[i])))
                }
                binding.pager.adapter = PagerAdapter(this@HomeDetailActivity,imageListUri)
                binding.dotsIndicator.attachTo(binding.pager)
            }

            override fun onFailure(call: Call<MutableList<HomeDetailItem>>, t: Throwable) {
                Log.i("test01","${t.message}")
            }
        })
    }

    private fun loadProfileFromFirestore(){
        val firebaseStorage = FirebaseStorage.getInstance()

        // 저장소의 최상위 위치를 참조하는 참조객체를 얻어오자.
        val rootRef = firebaseStorage.reference

        // 읽어오길 원하는 파일의 참조객체를 얻어오자.
        val imgRef = rootRef.child( "IMG_" + G.userAccount.id + ".jpg")
        Log.i("test12344","${imgRef} : ${G.userAccount.id}")
        if (imgRef != null) {
            // 파일 참조 객체로 부터 이미지의 다운로드 URL 얻어오자.
            imgRef.downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri?> {

                override fun onSuccess(p0: Uri?) {
                    Glide.with(this@HomeDetailActivity).load(p0).error(R.drawable.default_profile).into(binding.civProfile)
                }
            }).addOnFailureListener {
                Log.i("test12344",it.toString())
            }
        }
    }
    private fun clickChatBtn() {
        startActivity(Intent(this, ChattingActivity::class.java))
    }

    private fun clickFavoriteBtn() { binding.ibFav.isSelected = !binding.ibFav.isSelected }


    fun getFilePathFromUri(uri: Uri?): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        Log.i("dataPath", MediaStore.Images.Media.DATA)
        Log.i("dataPath",proj.toString())
        val loader = CursorLoader(
            this,
            uri!!, proj, null, null, null
        )
        val cursor = loader.loadInBackground()
        Log.i("dataPath",cursor.toString())

        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val result = cursor.getString(column_index)
        cursor.close()
        return result
    }
}
package com.cha.auctionapp.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.adapters.CommentsAdapter
import com.cha.auctionapp.adapters.PagerAdapter
import com.cha.auctionapp.adapters.PictureAdapter
import com.cha.auctionapp.adapters.PictureCommunityDetailAdapter
import com.cha.auctionapp.databinding.ActivityCommunityDetailBinding
import com.cha.auctionapp.model.CommentsItem
import com.cha.auctionapp.model.CommunityDetailItem
import com.cha.auctionapp.model.HomeDetailItem
import com.cha.auctionapp.model.PagerItem
import com.cha.auctionapp.model.PictureCommunityDetailItem
import com.cha.auctionapp.model.PictureItem
import com.cha.auctionapp.network.RetrofitHelper
import com.cha.auctionapp.network.RetrofitService
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class CommunityDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityCommunityDetailBinding
    lateinit var items: MutableList<CommunityDetailItem>
    lateinit var commentsItem: MutableList<CommentsItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        binding.recycler2.adapter = CommentsAdapter(this,commentsItem)


        loadDataFromServer()
        binding.btnSend.setOnClickListener { clickSendBtn() }
    }

    private fun loadDataFromServer(){
        val retrofit = RetrofitHelper.getRetrofitInstance("http://tjdrjs0803.dothome.co.kr")
        val retrofitService = retrofit.create(RetrofitService::class.java)
        val call: Call<MutableList<CommunityDetailItem>> = retrofitService.getDataFromServerForCommunityDetail(intent.getStringExtra("index")!!)
        call.enqueue(object : Callback<MutableList<CommunityDetailItem>> {
            override fun onResponse(
                call: Call<MutableList<CommunityDetailItem>>,
                response: Response<MutableList<CommunityDetailItem>>
            ) {
                items = mutableListOf()
                items = response.body()!!

                loadProfileFromFirestore()
                binding.tvMainTitle.text = items[0].title
                binding.tvDescription.text = items[0].description
                binding.tvMyTownName.text = items[0].location
                binding.tvLocationNameCommunityDetail.text = items[0].placeinfo
                binding.tvMyId.text = items[0].nickname
                var imageListString = items[0].image.split(",")

                var imageListPath: MutableList<PictureCommunityDetailItem> = mutableListOf()
                for(i in imageListString.indices){
                    imageListPath.add(PictureCommunityDetailItem(imageListString[i]))
                }
                binding.recycler.adapter = PictureCommunityDetailAdapter(this@CommunityDetailActivity,imageListPath)
            }

            override fun onFailure(call: Call<MutableList<CommunityDetailItem>>, t: Throwable) {
                Log.i("test01","${t.message}")
            }
        })
    }


    /*
    *
    *       프로필 사진 받아오기
    *
    * */
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
                    Glide.with(this@CommunityDetailActivity).load(p0).error(R.drawable.default_profile).into(binding.civMyProfile)
                }
            }).addOnFailureListener {
                Log.i("test12344",it.toString())
            }
        }
    }


    /*
    *
    *           댓글 작성 기능
    *
    * */
    private fun clickSendBtn() {
        // 보낼 일반 String 데이터
        var description = binding.etMsg.text.toString()
        var placeInfo = binding.tvLocationNameCommunityDetailComments.text.toString()

        var dataPart: HashMap<String,String> = hashMapOf()
        dataPart.put("description",description)
        dataPart.put("placeinfo",placeInfo)
        dataPart.put("nickname",G.nickName)
        dataPart.put("location",G.location)

        /*
        *       Retrofit 작업 시작
        * */
        var retrofit = RetrofitHelper.getRetrofitInstance("http://tjdrjs0803.dothome.co.kr")
        var retrofitService = retrofit.create(RetrofitService::class.java)
        var call: Call<String> = retrofitService.postDataToServerForCommunityDetailComments(dataPart)
        call.enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                finish()
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Snackbar.make(binding.root,"서버 작업에 오류가 생겼습니다.", Snackbar.LENGTH_SHORT)
            }
        })
        //items.add(CommentsItem(R.drawable._0,"1번","공릉 1동",binding.etMsg.text.toString(),"미래IT"))
//        items.add(CommentsItem(R.drawable._1,"2번","공릉 2동","안녕하세요",null))
//        items.add(CommentsItem(R.drawable._2,"3번","공릉 1동","안녕하세요",null))
//        items.add(CommentsItem(R.drawable._3,"4번","공릉 2동","안녕하세요",null))
//        items.add(CommentsItem(R.drawable._4,"5번","공릉 1동","안녕하세요",null))
//        items.add(CommentsItem(R.drawable._5,"6번","공릉 2동","안녕하세요",null))
//        items.add(CommentsItem(R.drawable._6,"7번","공릉 1동","안녕하세요","미래IT"))
        binding.recycler2.adapter?.notifyDataSetChanged()



        val imm : InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etMsg.windowToken,0)
        binding.etMsg.setText("")
        binding.etMsg.clearFocus()
    }

    /*
    *
    *       장소 버튼 ->  댓글 어댑터에서 실행
    *
    * */



    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
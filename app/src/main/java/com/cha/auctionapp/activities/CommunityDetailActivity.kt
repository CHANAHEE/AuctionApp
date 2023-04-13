package com.cha.auctionapp.activities

import android.annotation.SuppressLint
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
import com.cha.auctionapp.adapters.PictureCommunityDetailAdapter
import com.cha.auctionapp.databinding.ActivityCommunityDetailBinding
import com.cha.auctionapp.model.CommentsItem
import com.cha.auctionapp.model.CommunityDetailItem
import com.cha.auctionapp.model.PictureCommunityDetailItem
import com.cha.auctionapp.network.RetrofitHelper
import com.cha.auctionapp.network.RetrofitService
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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


        //binding.recycler2.adapter = CommentsAdapter(this,commentsItem)


        loadDataFromServer()
        binding.btnSend.setOnClickListener { clickSendBtn() }
        binding.btnLocation.setOnClickListener { clickLocationBtn() }
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
                var item = items[0]

                binding.root.tag = item.idx // 댓글 저장용 인덱스

                // 프로필 로드.
//                if(items[0].profile != G.userAccount.id){
//                    loadProfileFromFirestore(items[0].profile)
//                }
//                else{
//                    loadProfileFromFirestore(G.userAccount.id)
//                }
                loadProfileFromFirestore(item.id)
                binding.tvMainTitle.text = item.title
                binding.tvDescription.text = item.description
                binding.tvMyTownName.text = item.location
                binding.tvLocationNameCommunityDetail.text = item.placeinfo
                //binding.tvMyId.text = item.nickname


                var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
                var userRef: CollectionReference = firestore.collection("user")

                userRef.document(items[0].id).get().addOnSuccessListener {
                    binding.tvMyId.text = it.get("nickname").toString()
                    return@addOnSuccessListener
                }


                var imageListString = items[0].image.split(",")
                Log.i("iii",items[0].image.length.toString())
                if(items[0].image.isNotEmpty()) {
                    var imageListPath: MutableList<PictureCommunityDetailItem> = mutableListOf()
                    for (i in imageListString.indices) {
                        imageListPath.add(PictureCommunityDetailItem(imageListString[i]))
                    }
                    binding.recycler.adapter =
                        PictureCommunityDetailAdapter(this@CommunityDetailActivity, imageListPath)
                }

                loadCommentsDataFromServer()
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
    private fun loadProfileFromFirestore(profile: String){
        val firebaseStorage = FirebaseStorage.getInstance()

        // 저장소의 최상위 위치를 참조하는 참조객체를 얻어오자.
        val rootRef = firebaseStorage.reference

        // 읽어오길 원하는 파일의 참조객체를 얻어오자.
        val imgRef = rootRef.child("IMG_$profile.jpg")
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

        Log.i("sendMSG",placeInfo)
        var dataPart: HashMap<String,String> = hashMapOf()
        dataPart.put("idx",binding.root.tag as String)
        dataPart.put("description",description)
        dataPart.put("placeinfo",placeInfo)
        dataPart.put("nickname",G.nickName)
        dataPart.put("location",G.location)
        dataPart.put("id",G.userAccount.id)

        /*
        *       Retrofit 작업 시작
        * */
        var retrofit = RetrofitHelper.getRetrofitInstance("http://tjdrjs0803.dothome.co.kr")
        var retrofitService = retrofit.create(RetrofitService::class.java)
        var call: Call<String> = retrofitService.postDataToServerForCommunityDetailComments(dataPart)
        call.enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                binding.relativeLocationComments.visibility = View.GONE
                /*
                *       서버에 올리는 작업이 성공한다면.. 바로 뿌려줘야 한다. 여기서 다시 서버에 있는 걸 불러와..? 그래야지 다른 사람들도 보지.
                *
                * */
                loadCommentsDataFromServer()
                Log.i("testco",response.body().toString() + "   dd")
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Snackbar.make(binding.root,"서버 작업에 오류가 생겼습니다.", Snackbar.LENGTH_SHORT).show()
                Log.i("testco",t.message.toString())
            }
        })

        //binding.recycler2.adapter?.notifyDataSetChanged()

        val imm : InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etMsg.windowToken,0)
        binding.etMsg.setText("")
        binding.etMsg.clearFocus()
    }


    /*
    *
    *       댓글 정보 서버에서 가져오기
    *
    * */
    private fun loadCommentsDataFromServer(){
        var retrofit = RetrofitHelper.getRetrofitInstance("http://tjdrjs0803.dothome.co.kr")
        var retrofitService = retrofit.create(RetrofitService::class.java)
        var call: Call<MutableList<CommentsItem>> = retrofitService.getDataFromServerForCommunityDetailComments(binding.root.tag.toString())
        call.enqueue(object : Callback<MutableList<CommentsItem>>{
            override fun onResponse(call: Call<MutableList<CommentsItem>>, response: Response<MutableList<CommentsItem>>) {
                Log.i("daasdfasdfasdf",response.body().toString())
                binding.recycler2.adapter = CommentsAdapter(this@CommunityDetailActivity,response.body()!!)
            }

            override fun onFailure(call: Call<MutableList<CommentsItem>>, t: Throwable) {
                Snackbar.make(binding.root,"서버 작업에 오류가 생겼습니다.", Snackbar.LENGTH_SHORT).show()
            }
        })
    }


    /*
    *
    *       장소 버튼 클릭 이벤트
    *
    * */
    @SuppressLint("SuspiciousIndentation")
    private fun clickLocationBtn(){
        var intent = Intent(this,SelectPositionActivity::class.java)
              launcherLocationSelect.launch(intent)
        }

        var launcherLocationSelect: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts
            .StartActivityForResult()
        ) {
            when(it.resultCode){
                RESULT_OK->{
                    binding.relativeLocationComments.visibility = View.VISIBLE
                    binding.tvLocationNameCommunityDetailComments.text = it.data?.getStringExtra("position")
                    binding.btnCancelCommentsLocation.setOnClickListener {
                        binding.relativeLocationComments.visibility = View.GONE
                        binding.tvLocationNameCommunityDetailComments.text = ""
                    }
                }
            }
    }


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
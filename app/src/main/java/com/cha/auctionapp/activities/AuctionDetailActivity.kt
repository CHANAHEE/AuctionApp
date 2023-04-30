package com.cha.auctionapp.activities

import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.bumptech.glide.Glide
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.databinding.ActivityAuctionDetailBinding
import com.cha.auctionapp.databinding.FragmentAuctionDetailBottomSheet2Binding
import com.cha.auctionapp.databinding.FragmentAuctionDetailBottomSheetBinding
import com.cha.auctionapp.model.AppDatabase
import com.cha.auctionapp.model.AuctionDetailItem
import com.cha.auctionapp.model.MyAuctionFavListItem
import com.cha.auctionapp.model.MyCommunityFavListItem
import com.cha.auctionapp.network.RetrofitHelper
import com.cha.auctionapp.network.RetrofitService
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AuctionDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityAuctionDetailBinding
    lateinit var dialog1: BottomSheetDialog
    lateinit var dialog2: BottomSheetDialog

    lateinit var otherID: String
    lateinit var otherProfile: String
    var items: MutableList<AuctionDetailItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuctionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }


    /*
    *
    *       초기화 작업
    *
    * */
    private fun init() {
        binding.btnBack.setOnClickListener { finish() }
        binding.ibFav.setOnClickListener { clickFavoriteBtn() }
        binding.btnBid.setOnClickListener { clickBidBtn() }
        binding.btnBack.setOnClickListener { finish() }

        bidTimer()
        loadDataFromServer()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        else window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }


    /*
    *
    *       서버에서 데이터 불러오기
    *
    * */
    private fun loadDataFromServer() {
        val retrofit = RetrofitHelper.getRetrofitInstance("http://tjdrjs0803.dothome.co.kr")
        val retrofitService = retrofit.create(RetrofitService::class.java)
        val call: Call<MutableList<AuctionDetailItem>> = retrofitService.getDataFromServerForAuctionDetail(intent.getStringExtra("index")!!)
        //val call: Call<MutableList<AuctionDetailItem>> = retrofitService.getDataFromServerForAuctionDetail("17")
        call.enqueue(object : Callback<MutableList<AuctionDetailItem>> {
            override fun onResponse(
                call: Call<MutableList<AuctionDetailItem>>,
                response: Response<MutableList<AuctionDetailItem>>
            ) {
                items = response.body()!!
                var item = items[0]

                binding.tvTownInfo.text = item.location
                binding.tvItemName.text = item.title
                binding.tvCategory.text = item.category
                binding.tvDescription.text = item.description
                binding.tvPrice.text = "${item.price} 원"

                binding.videoview.setVideoURI(Uri.parse(item.video))
                binding.videoview.start()
                // 장소 정보
                if(item.tradingplace != ""){
                    binding.relativeLocation.visibility = View.VISIBLE
                    binding.tvLocationName.text = item.tradingplace
                    binding.relativeLocation.setOnClickListener { clickLocation(item) }
                }

                loadProfileFromFirebase()
                loadMyFavItem()
            }
            override fun onFailure(call: Call<MutableList<AuctionDetailItem>>, t: Throwable) {
            }
        })
    }

    /*
    *
    *       장소 정보 클릭 이벤트
    *
    * */
    private fun clickLocation(item: AuctionDetailItem) {
        startActivity(
            Intent(this@AuctionDetailActivity,SelectPositionActivity::class.java)
            .putExtra("showLocation","showLocation")
            .putExtra("latitude",item.latitude)
            .putExtra("longitude",item.longitude)
            .putExtra("title",item.tradingplace))
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
            Glide.with(this@AuctionDetailActivity)
                .load(it.get("profileImage"))
                .error(R.drawable.default_profile)
                .into(binding.civProfile)
            if(binding.tvId.text == G.nickName){
                binding.btnBid.visibility = View.GONE
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
    *       경매 버튼
    *
    * */
    private fun clickBidBtn() {
        dialog1 = BottomSheetDialog(this)
        var binding = FragmentAuctionDetailBottomSheetBinding.inflate(layoutInflater,this.binding.containerBottomsheet,false)

        dialog1.setContentView(binding.root)
        dialog1.show()

        binding.btnCancel.setOnClickListener { clickBtnCancel(binding) }
        binding.btnPrice1.setOnClickListener { clickBtnPrice(it,binding) }
        binding.btnPrice2.setOnClickListener { clickBtnPrice(it,binding) }
        binding.btnPrice3.setOnClickListener { clickBtnPrice(it,binding) }

        binding.btnBidBottomsheet1.setOnClickListener {
            binding.btnBidBottomsheet1.isEnabled = false
            binding.btnBidBottomsheet1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#A1A0A0"))
            dialog1.dismiss()
            clickBtnBidBottomSheet1(binding.tvPrice.text.toString())
        }
    }

    private fun changeBtnEnable(binding: FragmentAuctionDetailBottomSheetBinding){
        binding.btnBidBottomsheet1.isEnabled = true
        binding.btnBidBottomsheet1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF96DB8A"))
    }
    private fun clickBtnCancel(binding: FragmentAuctionDetailBottomSheetBinding){
        dialog1.dismiss()
        binding.btnBidBottomsheet1.isEnabled = false
        binding.btnBidBottomsheet1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#A1A0A0"))
    }
    private fun clickBtnPrice(it: View,binding: FragmentAuctionDetailBottomSheetBinding){
        when(it){
            binding.btnPrice1->{
                binding.tvPrice.text = binding.btnPrice1.text
                changeBtnEnable(binding)
            }
            binding.btnPrice2->{
                binding.tvPrice.text = binding.btnPrice2.text
                changeBtnEnable(binding)
            }
            binding.btnPrice3->{
                binding.tvPrice.text = binding.btnPrice3.text
                changeBtnEnable(binding)
            }
        }
    }
    private fun clickBtnBidBottomSheet1(resultPrice: String){
        var binding = FragmentAuctionDetailBottomSheet2Binding.inflate(layoutInflater,this.binding.containerBottomsheet,false)
        /*
        *       경고문구를 알려줄 수 있는 다이얼로그 하나를 띄우자.
        *       다이얼로그 입찰 버튼을 누르면, bottomsheet2 가 띄워진다.
        * */
        var alertDialog = AlertDialog.Builder(this)
            .setMessage("입찰을 시작한 뒤 취소하면 불이익이 있을 수 있습니다. 입찰하시겠습니까?")
            .setPositiveButton("입찰") { dialog, which ->
                dialog2 = BottomSheetDialog(this)

                /*
                *       두번째 bottomsheet 띄우기
                * */
                dialog2.setContentView(binding.root)
                dialog2.show()

                binding.tvPrice.text = resultPrice
                binding.btnComplete.setOnClickListener {
                    dialog1.dismiss()
                    dialog2.dismiss()
                    ((binding.root.parent) as ViewGroup).removeView(binding.root)
                }
            }.setNegativeButton("취소", OnClickListener { dialog, which ->  }).create()
        alertDialog.show()
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(this.resources.getColor(R.color.brand,this.theme))
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(this.resources.getColor(R.color.brand,this.theme))
    }




    /*
    *
    *       남은 시간 타이머 기능
    *
    * */
    private fun bidTimer() {
        object : CountDownTimer(43200000,1000) {

            /*
            *
            *       게시글을 올린 시간을 기준으로 해야할듯. (12시간 - 게시글 올린 시간) -> 요게 남은 시간
            *       올린 시간을 기준으로 12시간 후에는 게시글을 삭제시키기.
            *
            * */
            override fun onTick(millisUntilFinished: Long) {
                var hour    = (millisUntilFinished/1000/3600)
                var min     = (millisUntilFinished/1000/60%60)
                var second  = (millisUntilFinished/1000%60)

                var time = String.format("%02d : %02d : %02d",hour,min,second)
                binding.tvBidTime.text = time
            }

            override fun onFinish() {
                /*
                *       게시글 삭제
                * */
            }

        }.start()
    }




    /*
        *
        *       찜 기능
        *
        * */
    private fun loadMyFavItem(){
        val db = Room.databaseBuilder(
            this@AuctionDetailActivity,
            AppDatabase::class.java, "fav-database"
        ).build()

        val r = Runnable {
            // Query 를 이용해서 가지고 있는 인덱스의 값이 현재 페이지와 같은지 체크해서 있으면 찜된 목록임.
            var myFavListItem = db.MyAuctionFavListItemDAO().getAll()
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
                Snackbar.make(binding.root,"관심목록에서 삭제되었습니다.", Snackbar.LENGTH_SHORT).show()
                deleteMyFavData(db)
            }
            else->{
                binding.ibFav.isSelected = true
                Snackbar.make(binding.root,"관심목록에 추가되었습니다.", Snackbar.LENGTH_SHORT).show()
                insertMyFavData(db)
            }
        }
    }

    private fun deleteMyFavData(db: AppDatabase){
        val r = Runnable {
            db.MyAuctionFavListItemDAO()
                .delete(
                    MyAuctionFavListItem(
                        "${intent.getStringExtra("index")!!.toInt()}${G.userAccount.id}",
                        intent.getStringExtra("index")!!.toInt(),
                        items[0].title,
                        items[0].location,
                        items[0].description)
                )
        }
        Thread(r).start()
    }

    private fun insertMyFavData(db: AppDatabase){
        val r = Runnable{
            db.MyAuctionFavListItemDAO()
                .insert(
                    MyAuctionFavListItem(
                        "${intent.getStringExtra("index")!!.toInt()}${G.userAccount.id}",
                        intent.getStringExtra("index")!!.toInt(),
                        items[0].title,
                        items[0].location,
                        items[0].description)
                )
        }
        Thread(r).start()
    }

    /*
    *
    *       뒤로 가기 버튼 : 쌓여있는 액티비티 Clear
    *
    * */



}
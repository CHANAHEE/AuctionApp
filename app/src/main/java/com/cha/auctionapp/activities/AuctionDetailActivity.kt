package com.cha.auctionapp.activities

import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.cha.auctionapp.R
import com.cha.auctionapp.adapters.PagerAdapter
import com.cha.auctionapp.databinding.ActivityAuctionDetailBinding
import com.cha.auctionapp.databinding.FragmentAuctionDetailBottomSheet2Binding
import com.cha.auctionapp.databinding.FragmentAuctionDetailBottomSheetBinding
import com.cha.auctionapp.model.HomeDetailItem
import com.cha.auctionapp.model.PagerItem
import com.cha.auctionapp.network.RetrofitHelper
import com.cha.auctionapp.network.RetrofitService
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AuctionDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityAuctionDetailBinding
    lateinit var dialog1: BottomSheetDialog
    lateinit var dialog2: BottomSheetDialog


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
        // status bar 투명으로 만들기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        else window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }


    /*
    *
    *       서버에서 데이터 불러오기
    *
    * */
    private fun loadDataFromServer() {
//        val retrofit = RetrofitHelper.getRetrofitInstance("http://tjdrjs0803.dothome.co.kr")
//        val retrofitService = retrofit.create(RetrofitService::class.java)
//        val call: Call<MutableList<HomeDetailItem>> = retrofitService.getDataFromServerForHomeDetail(intent.getStringExtra("index")!!)
//        call.enqueue(object : Callback<MutableList<HomeDetailItem>> {
//            override fun onResponse(
//                call: Call<MutableList<HomeDetailItem>>,
//                response: Response<MutableList<HomeDetailItem>>
//            ) {
//                items = mutableListOf()
//                items = response.body()!!
//                var item = items[0]
//
//                binding.tvTownInfo.text = item.location
//                binding.tvItemName.text = item.title
//                binding.tvCategory.text = item.category
//                binding.tvDescription.text = item.description
//                binding.tvPrice.text = "${item.price} 원"
//
//                // 장소 정보
//                if(item.tradingplace != ""){
//                    binding.relativeLocation.visibility = View.VISIBLE
//                    binding.tvLocationName.text = item.tradingplace
//                }
////adfh
//                // 이미지 정보
//                var imageListString = item.image.split(",")
//                var imageListUri: MutableList<PagerItem> = mutableListOf()
//                for(i in imageListString.indices){
//                    imageListUri.add(PagerItem(Uri.parse(imageListString[i])))
//                }
//                binding.pager.adapter = PagerAdapter(this@HomeDetailActivity,imageListUri)
//                binding.dotsIndicator.attachTo(binding.pager)
//
//                loadProfileFromFirebase()
//                loadMyFavItem()
//            }
//            override fun onFailure(call: Call<MutableList<HomeDetailItem>>, t: Throwable) {
//            }
//        })
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
    private fun clickFavoriteBtn() { binding.ibFav.isSelected = !binding.ibFav.isSelected }



    /*
    *
    *       뒤로 가기 버튼 : 쌓여있는 액티비티 Clear
    *
    * */



}
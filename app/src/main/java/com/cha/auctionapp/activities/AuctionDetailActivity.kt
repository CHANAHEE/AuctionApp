package com.cha.auctionapp.activities

import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.cha.auctionapp.R
import com.cha.auctionapp.adapters.AuctionPagerAdapter
import com.cha.auctionapp.adapters.PagerAdapter
import com.cha.auctionapp.databinding.ActivityAuctionDetailBinding
import com.cha.auctionapp.databinding.FragmentAuctionDetailBottomSheet2Binding
import com.cha.auctionapp.databinding.FragmentAuctionDetailBottomSheetBinding
import com.cha.auctionapp.model.PagerItem
import com.google.android.material.bottomsheet.BottomSheetDialog


class AuctionDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityAuctionDetailBinding
    lateinit var items: MutableList<PagerItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuctionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.ibFav.setOnClickListener { clickFavoriteBtn() }
        binding.btnBid.setOnClickListener { clickBidBtn() }

        items = mutableListOf()
        items.add(PagerItem(R.drawable._0))
        items.add(PagerItem(R.drawable._1))
        items.add(PagerItem(R.drawable._2))

        binding.pager.adapter = PagerAdapter(this,items)
        binding.dotsIndicator.attachTo(binding.pager)


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



        // status bar 투명으로 만들기
        /*
        *       theme.xml , manifest 파일
        * */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        else window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    lateinit var dialog1: BottomSheetDialog
    lateinit var dialog2: BottomSheetDialog
    private fun clickBidBtn() {
        dialog1 = BottomSheetDialog(this)
        var binding = FragmentAuctionDetailBottomSheetBinding.inflate(layoutInflater,this.binding.containerBottomsheet,false)
        /*
        *       만약, bottomsheet 의 부모뷰가 있다면.. 부모뷰를 제거해주자
        *       이건 첫번째 bottomsheet 에서 취소버튼을 누른뒤, 다시 입찰했을 때를 위한 작업!
        *       만일, 입찰하기 까지 눌렀다면 다이얼로그 입찰을 눌렀을 떄, 첫번째 bottomsheet 는 없어지므로..
        * */
        // if(binding.root.parent != null) ((binding.root.parent) as ViewGroup).removeView(binding.root)

        /*
        *       dialog 를 띄우는 작업
        * */
        dialog1.setContentView(binding.root)
        dialog1.show()

        /*
        *       bottomsheet1 에 들어있는 뷰들의 리스너들
        * */
        binding.btnCancel.setOnClickListener { clickBtnCancel(binding) }
        binding.btnPrice1.setOnClickListener { clickBtnPrice(it,binding) }
        binding.btnPrice2.setOnClickListener { clickBtnPrice(it,binding) }
        binding.btnPrice3.setOnClickListener { clickBtnPrice(it,binding) }

        /*
        *       bottomsheet1 입찰 버튼 클릭 리스너
        * */
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
















    private fun clickFavoriteBtn() { binding.ibFav.isSelected = !binding.ibFav.isSelected }

}
package com.cha.auctionapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.cha.auctionapp.R
import com.cha.auctionapp.adapters.PagerAdapter
import com.cha.auctionapp.databinding.ActivityHomeDetailBinding
import com.cha.auctionapp.model.PagerItem

class HomeDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeDetailBinding
    lateinit var items: MutableList<PagerItem>

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.ibFav.setOnClickListener { clickFavoriteBtn() }
        binding.btnChat.setOnClickListener { clickChatBtn() }

        items = mutableListOf()
        items.add(PagerItem(R.drawable._0))
        items.add(PagerItem(R.drawable._1))
        items.add(PagerItem(R.drawable._2))

        binding.pager.adapter = PagerAdapter(this,items)
        binding.dotsIndicator.attachTo(binding.pager)

        // status bar 투명으로 만들기
        /*
        *       theme.xml , manifest 파일
        * */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        else window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN


    }



    private fun clickChatBtn() {
        startActivity(Intent(this, ChattingActivity::class.java))
    }

    private fun clickFavoriteBtn() { binding.ibFav.isSelected = !binding.ibFav.isSelected }



}
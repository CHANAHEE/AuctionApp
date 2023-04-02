package com.example.auctionapp.activities

import android.app.ActionBar.LayoutParams
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.auctionapp.R
import com.example.auctionapp.adapters.PagerAdapter
import com.example.auctionapp.databinding.ActivityHomeDetailBinding
import com.example.auctionapp.model.MainItem
import com.example.auctionapp.model.PagerItem

class HomeDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeDetailBinding
    lateinit var items: MutableList<PagerItem>

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }


    }



    private fun clickChatBtn() {
        startActivity(Intent(this,ChattingActivity::class.java))
    }

    private fun clickFavoriteBtn() { binding.ibFav.isSelected = !binding.ibFav.isSelected }



}
package com.example.auctionapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.auctionapp.R
import com.example.auctionapp.adapters.PagerAdapter
import com.example.auctionapp.databinding.ActivityHomeDetailBinding
import com.example.auctionapp.model.MainItem
import com.example.auctionapp.model.PagerItem

class HomeDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeDetailBinding
    lateinit var items: MutableList<PagerItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.ibFav.setOnClickListener { clickFavoriteBtn() }
        binding.btnChat.setOnClickListener { clickChatBtn() }

        items = mutableListOf()
        items.add(PagerItem(R.drawable.bg_one05))
        items.add(PagerItem(R.drawable.bg_one05))
        items.add(PagerItem(R.drawable.bg_one05))
        binding.pager.adapter = PagerAdapter(this,items)
    }

    private fun clickChatBtn() {
        startActivity(Intent(this,ChattingActivity::class.java))
    }

    private fun clickFavoriteBtn() { binding.ibFav.isSelected = !binding.ibFav.isSelected }



}
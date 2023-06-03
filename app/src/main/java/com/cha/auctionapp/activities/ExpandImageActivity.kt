package com.cha.auctionapp.activities

import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bumptech.glide.Glide
import com.cha.auctionapp.R
import com.cha.auctionapp.adapters.PagerAdapter
import com.cha.auctionapp.databinding.ActivityExpandImageBinding
import com.cha.auctionapp.model.PagerItem

class ExpandImageActivity : AppCompatActivity() {
    lateinit var binding: ActivityExpandImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpandImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initial()
    }

    private fun initial(){
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
        setPagerImage()
    }

    private fun setPagerImage(){

        var image: List<String> = listOf()
        image = intent.getStringExtra("image")!!.split(",")

        var imageList: MutableList<PagerItem> = mutableListOf()
        for(i in image.indices) imageList.add(PagerItem(Uri.parse(image[i])))

        val position = intent.getIntExtra("position",0)
        binding.pagerExpand.adapter = PagerAdapter(this,imageList)
        binding.pagerExpand.currentItem = position
        binding.dotsIndicatorExpand.attachTo(binding.pagerExpand)
    }
}
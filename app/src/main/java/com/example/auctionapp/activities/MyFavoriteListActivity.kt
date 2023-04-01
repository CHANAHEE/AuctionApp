package com.example.auctionapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.auctionapp.R
import com.example.auctionapp.adapters.MyFavoriteAdapter
import com.example.auctionapp.adapters.ProductAdapter
import com.example.auctionapp.databinding.ActivityMyFavoriteListBinding
import com.example.auctionapp.databinding.ActivityMyProfileEditBinding
import com.example.auctionapp.model.MainItem

class MyFavoriteListActivity : AppCompatActivity() {
    lateinit var binding : ActivityMyFavoriteListBinding
    lateinit var favorItems: MutableList<MainItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyFavoriteListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        favorItems = mutableListOf()
        /*
        *       favorItem 에 찜한 Item 만 넣기
        * */
        favorItems.add(MainItem("MainItem 1",R.drawable.bg_one05,"공릉동","34000원"))
        favorItems.add(MainItem("MainItem 2",R.drawable.bg_one05,"공릉동","34000원"))
        favorItems.add(MainItem("MainItem 3",R.drawable.bg_one05,"공릉동","34000원"))
        favorItems.add(MainItem("MainItem 4",R.drawable.bg_one05,"공릉동","34000원"))
        favorItems.add(MainItem("MainItem 5",R.drawable.bg_one05,"공릉동","34000원"))
        favorItems.add(MainItem("MainItem 6",R.drawable.bg_one05,"공릉동","34000원"))
        favorItems.add(MainItem("MainItem 7",R.drawable.bg_one05,"공릉동","34000원"))
        favorItems.add(MainItem("MainItem 8",R.drawable.bg_one05,"공릉동","34000원"))
        favorItems.add(MainItem("MainItem 9",R.drawable.bg_one05,"공릉동","34000원"))
        favorItems.add(MainItem("MainItem 10s",R.drawable.bg_one05,"공릉동","34000원"))

        if(favorItems.size != 0) binding.tvNone.visibility = View.GONE
        binding.recycler.adapter = MyFavoriteAdapter(this, favorItems)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
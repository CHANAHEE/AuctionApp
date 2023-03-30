package com.example.auctionapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.auctionapp.R
import com.example.auctionapp.databinding.ActivityMyFavoriteListBinding
import com.example.auctionapp.databinding.ActivityMyProfileEditBinding

class MyFavoriteListActivity : AppCompatActivity() {
    lateinit var binding : ActivityMyFavoriteListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyFavoriteListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
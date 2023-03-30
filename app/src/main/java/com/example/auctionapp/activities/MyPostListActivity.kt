package com.example.auctionapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.auctionapp.R
import com.example.auctionapp.databinding.ActivityMyPostListBinding
import com.example.auctionapp.databinding.ActivityMyProfileEditBinding

class MyPostListActivity : AppCompatActivity() {
    lateinit var binding : ActivityMyPostListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPostListBinding.inflate(layoutInflater)
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
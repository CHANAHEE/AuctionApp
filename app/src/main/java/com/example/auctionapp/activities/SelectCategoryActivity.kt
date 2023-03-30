package com.example.auctionapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.auctionapp.R
import com.example.auctionapp.databinding.ActivitySelectCategoryBinding

class SelectCategoryActivity : AppCompatActivity() {
    lateinit var binding : ActivitySelectCategoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectCategoryBinding.inflate(layoutInflater)
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
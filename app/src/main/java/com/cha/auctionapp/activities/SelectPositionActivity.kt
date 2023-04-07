package com.cha.auctionapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cha.auctionapp.R
import com.cha.auctionapp.databinding.ActivitySelectPositionBinding

class SelectPositionActivity : AppCompatActivity() {
    lateinit var binding : ActivitySelectPositionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectPositionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.btnComplete.setOnClickListener { clickCompleteBtn() }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun clickCompleteBtn() {
        /*
        *
        *       선택된 장소를 전 액티비티로 보내주기. startActivityForResult 사용하기!! 
        *
        * */
        finish()
    }
}
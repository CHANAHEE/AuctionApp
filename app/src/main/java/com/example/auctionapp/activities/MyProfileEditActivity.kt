package com.example.auctionapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.auctionapp.R
import com.example.auctionapp.databinding.ActivityMyProfileEditBinding

class MyProfileEditActivity : AppCompatActivity() {

    lateinit var binding : ActivityMyProfileEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.btnComplete.setOnClickListener { clickCompleteBtn() }
    }


    /*
    *
    *       완료 버튼
    *
    * */
    private fun clickCompleteBtn() {
        /*
        *       변경된 프로필 정보 DB 에 저장.
        * */

        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
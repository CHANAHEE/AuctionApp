package com.example.auctionapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.auctionapp.R
import com.example.auctionapp.databinding.ActivitySellingEditBinding

class SellingEditActivity : AppCompatActivity() {
    lateinit var binding:ActivitySellingEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellingEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.btnComplete.setOnClickListener { clickCompleteBtn() }
        binding.categoryRelative.setOnClickListener { clickCategory() }
        binding.selectPosRelative.setOnClickListener { clickSelectPos() }
    }

    /*
    *
    *       카테고리 선택 버튼
    *
    * */
    private fun clickCategory() {
        /*
        *       카테고리 관련 정보 넣어서 다이얼로그 띄우기
        * */
        AlertDialog.Builder(this).setMessage("카테고리 선택 리스트").show()
    }


    /*
    *
    *       거래 희망 장소 선택
    *
    * */
    private fun clickSelectPos() {
        startActivity(Intent(this,SelectPositionActivity::class.java))
    }




    /*
    *
    *       완료 버튼
    *
    * */
    private fun clickCompleteBtn() {
        /*
        *
        *       DB 에 글 저장하기
        *
        * */
        finish()
    }
    override fun onSupportNavigateUp(): Boolean {
        /*
        *
        *       작성중이라면??
        * */
        finish()
        return super.onSupportNavigateUp()
    }
}
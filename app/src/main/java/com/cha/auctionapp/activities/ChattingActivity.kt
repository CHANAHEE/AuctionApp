package com.cha.auctionapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cha.auctionapp.R
import com.cha.auctionapp.databinding.ActivityChattingBinding

class ChattingActivity : AppCompatActivity() {

    lateinit var binding: ActivityChattingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSend.setOnClickListener { clickSendBtn() }
        binding.btnLocation.setOnClickListener { clickLocationBtn() }
        binding.btnImage.setOnClickListener { clickImageBtn() }
    }

    private fun clickImageBtn(){
        /*
        *       사진 선택 기능
        * */
    }

    private fun clickLocationBtn(){

    }

    private fun clickSendBtn(){
        /*
        *       서버에 메시지 저장.
        *       사진 정보는 어떻게 할지 생각해보자..
        * */
    }
}
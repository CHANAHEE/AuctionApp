package com.cha.auctionapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cha.auctionapp.R
import com.cha.auctionapp.databinding.ActivitySetUpMyPlaceListBinding

class SetUpMyPlaceListActivity : AppCompatActivity() {
    lateinit var binding : ActivitySetUpMyPlaceListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetUpMyPlaceListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)


    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    /*
    *
    *   리사이클러뷰 아이템 선택 리스너 -> 선택하면 SetUpPlaceActivity 에 버튼의 정보를 바꿔야함. 내 동네 DB 를 만들어야 할듯. 첫번째 동네, 두번째 동네
    *
    *
    * */
}
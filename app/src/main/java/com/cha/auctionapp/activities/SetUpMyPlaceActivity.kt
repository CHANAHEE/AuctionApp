package com.cha.auctionapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.cha.auctionapp.databinding.ActivitySetUpMyPlaceBinding

class SetUpMyPlaceActivity : AppCompatActivity() {

    lateinit var binding : ActivitySetUpMyPlaceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetUpMyPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        binding.btnMyTown.setOnClickListener { clickMyTownBtn() }
        binding.btnAdd.setOnClickListener { clickAddBtn() }
    }




    private fun clickAddBtn() {
        startActivity(Intent(this,SetUpMyPlaceListActivity::class.java))
    }

    private fun clickMyTownBtn() {
        AlertDialog.Builder(this).setMessage("동네는 기본으로 1개 이상 설정해야합니다. 변경하시겠습니까?").setPositiveButton("확인"
        ) { p0, p1 -> clickAddBtn()}.setNegativeButton("취소"
        ) { p0, p1 -> }.show()
    }

    /*
    *
    *       뒤로 가기 버튼
    *
    * */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
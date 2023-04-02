package com.example.auctionapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.auctionapp.G
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
        binding.civProfile.setOnClickListener { clickProfileImage() }
        binding.etNickname.setText("CHA")
    }

    var pickLauncher : ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()
    ) {
        if(it.resultCode == RESULT_OK) {
            Glide.with(this).load(it.data?.data).into(binding.civProfile)
            G.profileImage = it.data?.data.toString()
        }
    }

    private fun clickProfileImage() {
        var intent: Intent = Intent(MediaStore.ACTION_PICK_IMAGES)
        pickLauncher.launch(intent)
    }

    /*
    *
    *       완료 버튼
    *
    * */
    private fun clickCompleteBtn() {
        /*
        *       변경된 프로필 정보 DB 에 저장.
        *       G 클래스의 profileImage 를 DB 에 저장하자.
        * */
        if(binding.etNickname.text.length < 3){
            AlertDialog.Builder(this).setMessage("닉네임을 3글자 이상으로 설정해주세요").show()
            return
        }
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
package com.example.auctionapp.activities

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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

        Log.i("alertdl","다이얼로그 띄우기")
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
        Log.i("ale","다이얼로그 띄우기1")
        if(binding.etNickname.text.length < 3){
            Log.i("alertdl","다이얼로그 띄우기2")
            AlertDialog.Builder(this).setMessage("닉네임을 3글자 이상으로 설정해주세요").show()
            return
        }else {
            Log.i("alertdl","다이얼로그 띄우기3")
            var dialog = AlertDialog.Builder(this).setMessage("프로필을 변경하시겠습니까?").setPositiveButton("확인",
                DialogInterface.OnClickListener { dialog, which ->  
                    
                    /*
                    *       변경된 프로필 정보 DB 저장
                    * */

                    finish()
                }).setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->  }).create()

            dialog.show()
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.brand,theme))
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.brand,theme))
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
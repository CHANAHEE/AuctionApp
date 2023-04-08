package com.cha.auctionapp.activities


import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.databinding.ActivityMyProfileEditBinding


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

        binding.civProfile.tag = DEFAULT_PROFILE
    }

    val DEFAULT_PROFILE = 0
    val CHANGED_PROFILE = 1

    var pickLauncher : ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()
    ) {
        if(it.resultCode == RESULT_OK) {
            Glide.with(this).load(it.data?.data).into(binding.civProfile)
            G.profileImage = it.data?.data!!
            binding.civProfile.tag = CHANGED_PROFILE
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
            var dialog = AlertDialog.Builder(this).setMessage("프로필을 설정 하시겠습니까?").setPositiveButton("확인",
                DialogInterface.OnClickListener { dialog, which ->  
                    
                    /*
                    *       변경된 프로필 정보 G 클래스 저장
                    * */
                    G.nickName = binding.etNickname.text.toString()
                    if(binding.civProfile.tag == DEFAULT_PROFILE) G.profileImage = getURLForResource(R.drawable.default_profile)

                    setResult(RESULT_OK,getIntent())
                    startActivity(Intent(this,SetUpMyPlaceListActivity::class.java))
                    finish()
                }).setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->  }).create()

            dialog.show()
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.brand,theme))
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.brand,theme))
        }



    }

    /*
    *
    *       기본 프로필 이미지 : drawable -> Uri
    *
    * */
    private fun getURLForResource(resId: Int): Uri {
        return Uri.parse("android.resource://" + (R::class.java.getPackage()?.getName()) + "/" + resId)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
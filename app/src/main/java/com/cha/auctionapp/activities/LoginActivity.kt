package com.cha.auctionapp.activities

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentTransaction
import com.cha.auctionapp.R
import com.cha.auctionapp.databinding.ActivityLoginBinding
import com.cha.auctionapp.fragments.LoginMainFragment
import com.google.android.material.snackbar.Snackbar
import com.kakao.sdk.common.util.Utility

class LoginActivity : AppCompatActivity() {

    private val binding:ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val keyHash:String = Utility.getKeyHash(this)
        Log.i("keyhash",keyHash)

        // 내 위치 정보 제공에 대한 동적 퍼미션 요청
        if( checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){

            // 퍼미션 요청 대행사 이용 - 계약 체결
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        val tran:FragmentTransaction = supportFragmentManager
            .beginTransaction()
            .add(R.id.container_fragment,LoginMainFragment())
        tran.commit()


    }
    
    /*
    *
    *       내 위치 정보 퍼미션 받기
    *
    * */


    var permissionLauncher: ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.RequestPermission(), object : ActivityResultCallback<Boolean>{
        override fun onActivityResult(result: Boolean?) {
            if(!result!!) {
                Snackbar.make(
                    findViewById(android.R.id.content)!!,
                    "위치 정보 제공에 동의하지 않았습니다. 검색 기능이 제한됩니다.", Snackbar.LENGTH_SHORT).show()
            }
        }
    })


}
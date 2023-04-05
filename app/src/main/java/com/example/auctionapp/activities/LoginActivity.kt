package com.example.auctionapp.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.View.GONE
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.getSystemService
import androidx.fragment.app.FragmentTransaction
import com.example.auctionapp.R
import com.example.auctionapp.fragments.SignUpEmailInputFragment
import com.example.auctionapp.databinding.ActivityLoginBinding
import com.example.auctionapp.fragments.LoginMainFragment
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    private val binding:ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


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
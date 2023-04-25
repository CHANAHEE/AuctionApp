package com.cha.auctionapp.activities

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.cha.auctionapp.adapters.LocationListRecyclerAdapter
import com.cha.auctionapp.databinding.ActivitySetUpMyPlaceListBinding
import com.cha.auctionapp.model.KakaoSearchItemByAddress
import com.cha.auctionapp.network.RetrofitHelper
import com.cha.auctionapp.network.RetrofitService
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class SetUpMyPlaceListActivity : AppCompatActivity() {

    lateinit var binding : ActivitySetUpMyPlaceListBinding
    var searchPlaceByAddressResponse: KakaoSearchItemByAddress? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetUpMyPlaceListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }


    /*
    *
    *       초기화 작업
    *
    * */
    private fun init(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        checkPermissionLocation()
        editTextListener()
    }


    /*
    *
    *       위치 정보 퍼미션 체크
    *
    * */
    private fun checkPermissionLocation() {
        if( checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    var permissionLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        object : ActivityResultCallback<Boolean> {
            override fun onActivityResult(result: Boolean?) {
                if(!result!!){
                    Snackbar.make(
                        binding.root,
                        "위치 정보 제공에 동의하지 않았습니다. 검색 기능이 제한됩니다.", Snackbar.LENGTH_SHORT).show()
                }
            }
        })


    /*
    *
    *       검색어 기반 위치 찾아오기
    *
    * */
    private fun requestMyLocation(){
        val retrofit: Retrofit = RetrofitHelper.getRetrofitInstance("https://dapi.kakao.com")
        val retrofitService = retrofit.create(RetrofitService::class.java)
        retrofitService.getSearchPlaceByAddress(binding.etAddress.text.toString())
            .enqueue(object : Callback<KakaoSearchItemByAddress> {
                override fun onResponse(
                    call: Call<KakaoSearchItemByAddress>,
                    response: Response<KakaoSearchItemByAddress>
                ) {

                    searchPlaceByAddressResponse = response.body()

                    binding.recycler.adapter =
                        LocationListRecyclerAdapter(this@SetUpMyPlaceListActivity,
                        searchPlaceByAddressResponse?.documents!!)
                }
                override fun onFailure(call: Call<KakaoSearchItemByAddress>, t: Throwable) {
                    Log.i("errorRetrofit",t?.message ?: "")
                }
            })
    }

    /*
    *
    *       키보드 설정
    *
    * */
    private fun editTextListener(){
        binding.etAddress.setOnKeyListener { v , keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_ENTER)
            {
                val imm : InputMethodManager = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.etAddress.windowToken,0)
                requestMyLocation()
                true
            }
            false
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
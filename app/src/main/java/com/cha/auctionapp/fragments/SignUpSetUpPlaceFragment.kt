package com.cha.auctionapp.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.adapters.LocationListRecyclerAdapter
import com.cha.auctionapp.databinding.FragmentSignUpSetUpPlaceBinding
import com.cha.auctionapp.model.KakaoSearchItemByAddress
import com.cha.auctionapp.network.RetrofitHelper
import com.cha.auctionapp.network.RetrofitService
import com.google.android.material.snackbar.Snackbar
import com.kakao.sdk.common.util.Utility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class SignUpSetUpPlaceFragment : Fragment() {

    val binding by lazy { FragmentSignUpSetUpPlaceBinding.inflate(layoutInflater) }
    var searchPlaceByAddressResponse: KakaoSearchItemByAddress? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editTextListener()
        binding.btnBack.setOnClickListener { clickBackBtn() }
        binding.btnNext.setOnClickListener { clickNextBtn() }

        if( activity?.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }



    /*
    *
    *       위치 제공 퍼미션 런처
    *
    * */
    var permissionLauncher: ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.RequestPermission(),
        object : ActivityResultCallback<Boolean> {
            override fun onActivityResult(result: Boolean?) {
                if(!result!!){
                    Snackbar.make(
                        activity?.findViewById(android.R.id.content)!!,
                        "위치 정보 제공에 동의하지 않았습니다. 검색 기능이 제한됩니다.", Snackbar.LENGTH_SHORT).show()
                }
            }
        })



    /*
    *
    *      검색어 기반으로 위치 찾아오기
    *
    * */
    private fun requestMyLocation(){
        val retrofit: Retrofit = RetrofitHelper.getRetrofitInstance("https://dapi.kakao.com")
        val retrofitService = retrofit.create(RetrofitService::class.java)
        retrofitService.getSearchPlaceByAddress(binding.etAddress.text.toString())
            .enqueue(object : Callback<KakaoSearchItemByAddress>{
                override fun onResponse(
                    call: Call<KakaoSearchItemByAddress>,
                    response: Response<KakaoSearchItemByAddress>
                ) {
                    searchPlaceByAddressResponse = response.body()
                    binding.recycler.adapter = LocationListRecyclerAdapter(requireContext(),
                        searchPlaceByAddressResponse?.documents!!,binding)
                }

                override fun onFailure(call: Call<KakaoSearchItemByAddress>, t: Throwable) {
                    Log.i("errorRetrofit",t?.message ?: "")
                }
            })
    }


    /*
    *
    *       주소 입력 완료 시, 키보드 숨기기
    *
    * */
    private fun editTextListener(){
        binding.etAddress.setOnKeyListener { v , keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_ENTER)
            {
                val imm : InputMethodManager = context?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.etAddress.windowToken,0)
                requestMyLocation()
                true
            }
            false
        }
    }


    /*
    *
    *       동네 선택 후, 닉네임 설정 화면으로
    *
    * */
    private fun clickNextBtn(){
        
        if(binding.tvLocationSetUpPlace.text == "") {
            Snackbar.make(
                activity?.findViewById(android.R.id.content)!!,
                "동네를 선택 해주세요", Snackbar.LENGTH_SHORT).show()
            return
        }

        G.location = binding.tvLocationSetUpPlace.text.toString()
        val tran:FragmentTransaction? =
            activity?.
            supportFragmentManager?.
            beginTransaction()?.
            replace(R.id.container_fragment,SignUpSetNickNameFragment())?.addToBackStack(null)

        tran?.commit()
    }


    /*
    *
    *       뒤로가기 버튼
    *
    * */
    private fun clickBackBtn(){
        val fragment = activity?.supportFragmentManager
        fragment?.popBackStack()
    }
}
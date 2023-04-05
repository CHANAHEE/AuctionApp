package com.example.auctionapp.fragments

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
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
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentTransaction
import com.example.auctionapp.R
import com.example.auctionapp.adapters.LocationListRecyclerAdapter
import com.example.auctionapp.databinding.FragmentSignUpSetUpPlaceBinding
import com.example.auctionapp.model.KakaoSearchItemByAddress
import com.example.auctionapp.model.KakaoSearchItemByResionCode
import com.example.auctionapp.network.RetrofitHelper
import com.example.auctionapp.network.RetrofitService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import com.kakao.sdk.common.util.Utility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class SignUpSetUpPlaceFragment : Fragment() {

    val binding by lazy { FragmentSignUpSetUpPlaceBinding.inflate(layoutInflater) }
    val providerClient: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(requireActivity()) }
    var myLocation: Location? = null
    var searchPlaceByResionCodeResponse: KakaoSearchItemByResionCode? = null
    var searchPlaceByAddressResponse: KakaoSearchItemByAddress? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var keyHash = Utility.getKeyHash(requireContext())
        Log.i("keyHash",keyHash)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
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
    *       위치 제공 퍼미션 런처
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
    *      검색어 기반으로 내 위치 찾아오기
    *
    * */
    private fun requestMyLocation(){
        val retrofit: Retrofit = RetrofitHelper.getRetrofitInstance("http://dapi.kakao.com")
        val retrofitService = retrofit.create(RetrofitService::class.java)
        retrofitService.getSearchPlaceByAddress(binding.etAddress.text.toString())
            .enqueue(object : Callback<KakaoSearchItemByAddress>{
                override fun onResponse(
                    call: Call<KakaoSearchItemByAddress>,
                    response: Response<KakaoSearchItemByAddress>
                ) {
                    searchPlaceByAddressResponse = response.body()
                    myLocation?.longitude = searchPlaceByAddressResponse?.documents?.get(0)?.x?.toDouble()!!
                    myLocation?.latitude = searchPlaceByAddressResponse?.documents?.get(0)?.y?.toDouble()!!

                    getLocationList()
                }

                override fun onFailure(call: Call<KakaoSearchItemByAddress>, t: Throwable) {

                }

            })
    }


    /*
    *
    *       동네 관할구역 정보 리스트 가져오기 - Retrofit
    *
    * */
    private fun getLocationList() {
        val retrofit: Retrofit = RetrofitHelper.getRetrofitInstance("http://dapi.kakao.com")
        val retrofitService = retrofit.create(RetrofitService::class.java)
        retrofitService.getSearchPlaceByResionCode(myLocation?.longitude.toString(),myLocation?.latitude.toString())
            .enqueue(object : Callback<KakaoSearchItemByResionCode> {
                override fun onResponse(
                    call: Call<KakaoSearchItemByResionCode>,
                    response: Response<KakaoSearchItemByResionCode>
                ) {
                    searchPlaceByResionCodeResponse = response.body()
                    binding.recycler.adapter = LocationListRecyclerAdapter(requireContext(),
                        searchPlaceByResionCodeResponse!!.documents)
                }

                override fun onFailure(call: Call<KakaoSearchItemByResionCode>, t: Throwable) {

                }

            })
    }







    private fun clickBackBtn(){
        val fragment = activity?.supportFragmentManager
        fragment?.popBackStack()
    }



    private fun editTextListener(){
        binding.etAddress.setOnKeyListener { v , keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_ENTER)
            {
                val imm : InputMethodManager = context?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.etAddress.windowToken,0)
                /*
                *       여기에서 검색 결과 리사이클러뷰에 띄워주기 / 리사이클러뷰에서는 아이템 선택 리스너를 통해 tv 에 넣어주기
                * */
                requestMyLocation()
                true
            }

            false
        }
    }

    private fun clickNextBtn(){
        var fragment = SignUpSetNickNameFragment()
        var bundle = Bundle()
        bundle.putString("location",binding.tvLocationSetUpPlace.text.toString())
        bundle.putString("name",arguments?.getString("name"))
        bundle.putString("birth",arguments?.getString("birth"))
        bundle.putString("email",arguments?.getString("email"))
        bundle.putString("password",arguments?.getString("password"))
        fragment.arguments = bundle
        val tran:FragmentTransaction? =
            activity?.
            supportFragmentManager?.
            beginTransaction()?.
            replace(R.id.container_fragment,fragment)?.addToBackStack(null)

        tran?.commit()
    }
}
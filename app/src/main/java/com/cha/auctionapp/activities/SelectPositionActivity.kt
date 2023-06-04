package com.cha.auctionapp.activities

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.cha.auctionapp.R
import com.cha.auctionapp.databinding.ActivitySelectPositionBinding
import com.cha.auctionapp.databinding.FragmentSelectPositionBottomSheetBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar

class SelectPositionActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var binding : ActivitySelectPositionBinding
    var myLocation : Location? = null
    val providerClient: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(this) }
    lateinit var map: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectPositionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initial()
    }

    /*
    *
    *       초기화 작업
    *
    * */
    private fun initial() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        map = findViewById<View>(R.id.map)
        map.visibility = View.INVISIBLE

        if(intent.getStringExtra("showLocation") == "showLocation"){
            binding.tvAppbarTitle.text = "거래 장소"
            binding.btnComplete.visibility = View.GONE
            binding.ivMarker.visibility = View.GONE

        }else binding.ivMarker.bringToFront()

            locationCheckPermission()
            requestMyLocation()

    }
    private fun requestMyLocation(){

        var request: LocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000).build()
        Log.i("googleMapissue","requestMyLocation 실행")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        Log.i("googleMapissue","위치 퍼미션 허용완료")
        providerClient.requestLocationUpdates(request,locationCallback, Looper.getMainLooper())

    }

    val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            myLocation = p0.lastLocation
            Log.i("googleMapissue","로케이션 콜백받아오기 완료")

            providerClient.removeLocationUpdates(this)
            // 구글 맵 준비 메소드 : 취소할 경우 try-catch 로 예외 처리
            try {
                getReadyGoogleMap()
            }catch (_: Exception){

            }

        }
    }

    /*
    *
    *       google Map 준비 및 완료 콜백 메서드
    *
    * */

    private fun getReadyGoogleMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        Log.i("googleMapissue","getReadyGoogleMap 실행")
    }
    override fun onMapReady(googleMap: GoogleMap) {
        if(intent.getStringExtra("showLocation") == "showLocation") {
            val position = LatLng(intent.getStringExtra("latitude")!!.toDouble(),
                intent.getStringExtra("longitude")!!.toDouble())
            googleMap.addMarker(MarkerOptions()
                .position(position)
                .title(intent.getStringExtra("title"))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_location)))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,20.0f))
            map.visibility = View.VISIBLE
        }else{
            var location: LatLng = LatLng(myLocation?.latitude!!,myLocation?.longitude!!)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,20.0f))

            map.visibility = View.VISIBLE
            binding.btnComplete.setOnClickListener {
                myLocation?.latitude = googleMap.cameraPosition.target.latitude
                myLocation?.longitude = googleMap.cameraPosition.target.longitude
                clickCompleteBtn()
            }
        }
    }



    /*
    *
    *        위치 정보 제공 퍼미션 확인하기
    *
    * */
    private fun locationCheckPermission(){
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val launcher: ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.RequestPermission(),object : ActivityResultCallback<Boolean>{
        override fun onActivityResult(result: Boolean?) {
            if(!result!!) {
                Snackbar.make(binding.root,"위치 정보 제공에 동의하지 않았습니다. 검색이 제한됩니다.",Snackbar.LENGTH_SHORT).show()
                finish()
            }
        }
    })




    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    lateinit var bindingBottomsheet: FragmentSelectPositionBottomSheetBinding
    private fun clickCompleteBtn() {
        /*
        *
        *       선택된 장소를 전 액티비티로 보내주기. startActivityForResult 사용하기!! 
        *
        * */
        bindingBottomsheet = FragmentSelectPositionBottomSheetBinding.inflate(layoutInflater,binding.containerBottomsheet,false)
        var dialog = BottomSheetDialog(this,R.style.DialogStyle)
        dialog.setContentView(bindingBottomsheet.root)
        dialog.show()

        bindingBottomsheet.etSelectLocation.addTextChangedListener(watcher)
        bindingBottomsheet.btnSelectPosition.setOnClickListener {
            var intent = intent
            Log.i("cha1","${intent} ${myLocation?.latitude} ${myLocation?.longitude}")
            intent.putExtra("latitude",myLocation?.latitude.toString())
            intent.putExtra("longitude",myLocation?.longitude.toString())
            intent.putExtra("position",bindingBottomsheet.etSelectLocation.text.toString())

            setResult(RESULT_OK,intent)
            dialog.dismiss()
            finish()
        }
    }

    val watcher: TextWatcher = object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if(s.toString() != "") {
                bindingBottomsheet.btnSelectPosition.isEnabled = true
                bindingBottomsheet.btnSelectPosition.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.brand,theme))
            } else if(s.toString() == ""){
                bindingBottomsheet.btnSelectPosition.isEnabled = false
                bindingBottomsheet.btnSelectPosition.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.unable,theme))
            }
        }

        override fun afterTextChanged(s: Editable?) {

        }
    }



}
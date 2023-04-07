package com.cha.auctionapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cha.auctionapp.R
import com.cha.auctionapp.databinding.ActivitySelectPositionBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class SelectPositionActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var binding : ActivitySelectPositionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectPositionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.btnComplete.setOnClickListener { clickCompleteBtn() }



        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {


        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(37.123, 127.123))
                .title("Marker")
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun clickCompleteBtn() {
        /*
        *
        *       선택된 장소를 전 액티비티로 보내주기. startActivityForResult 사용하기!! 
        *
        * */
        finish()
    }
}
package com.example.auctionapp.activities

import android.content.ClipData
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.auctionapp.R
import com.example.auctionapp.adapters.PagerAdapter
import com.example.auctionapp.adapters.PictureAdapter
import com.example.auctionapp.databinding.ActivitySellingEditBinding
import com.example.auctionapp.model.PagerItem
import com.example.auctionapp.model.PictureItem

class SellingEditActivity : AppCompatActivity() {

    lateinit var binding:ActivitySellingEditBinding
    lateinit var items: MutableList<PictureItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellingEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.btnComplete.setOnClickListener { clickCompleteBtn() }
        binding.categoryRelative.setOnClickListener { clickCategory() }
        binding.selectPosRelative.setOnClickListener { clickSelectPos() }
        binding.btnImage.setOnClickListener { clickPicture() }

        items = mutableListOf()
        Log.i("Hello2","${items.size}")
        binding.recycler.adapter = PictureAdapter(this, items,binding)
    }



    var launcher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {
            if(it.resultCode == RESULT_OK){
                var clipData = it.data?.clipData!!
                var size = clipData.itemCount

                for(i in 0 until size){
                    Log.i("Hello2","$i")
                    items.add(PictureItem(clipData.getItemAt(i).uri))
                }
                binding.recycler.adapter?.notifyDataSetChanged()
                binding.btnImage.text = "${items.size} / 10"
                if(items.size == 10) binding.btnImage.visibility = View.GONE
            }
        })
    /*
    *
    *       사진 선택 버튼
    *
    * */
    private fun clickPicture() {
        /*
        *       앨범에서 선택하기
        * */

        var intent: Intent = Intent(MediaStore.ACTION_PICK_IMAGES).putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX,10-items.size)
        launcher.launch(intent)

    }


    /*
    *
    *       카테고리 선택 버튼
    *
    * */
    private fun clickCategory() {
        /*
        *       카테고리 관련 정보 넣어서 다이얼로그 띄우기
        * */
        AlertDialog.Builder(this).setMessage("카테고리 선택 리스트").show()
    }


    /*
    *
    *       거래 희망 장소 선택
    *
    * */
    private fun clickSelectPos() {
        startActivity(Intent(this,SelectPositionActivity::class.java))
    }




    /*
    *
    *       완료 버튼
    *
    * */
    private fun clickCompleteBtn() {
        /*
        *
        *       DB 에 글 저장하기
        *
        * */
        finish()
    }
    override fun onSupportNavigateUp(): Boolean {
        /*
        *
        *       작성중이라면??
        * */
        finish()
        return super.onSupportNavigateUp()
    }
}
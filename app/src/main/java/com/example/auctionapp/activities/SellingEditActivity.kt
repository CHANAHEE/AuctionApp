package com.example.auctionapp.activities

import android.content.ClipData
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
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

        binding.etPrice.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) binding.ivWon.imageTintList = ColorStateList.valueOf(Color.parseColor("#FF000000"))
            else binding.ivWon.imageTintList = ColorStateList.valueOf(Color.parseColor("#4A000000"))
        }

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
        AlertDialog.Builder(this).setTitle("카테고리 선택").setItems(resources.getStringArray(R.array.category_item),
            DialogInterface.OnClickListener { dialog, which ->
                Log.i("dialogClicked","$dialog $which")
                binding.tvCategory.text = resources.getStringArray(R.array.category_item)[which]
                binding.tvCategory.setTextColor(resources.getColor(R.color.black,theme))
            }).create().show()
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
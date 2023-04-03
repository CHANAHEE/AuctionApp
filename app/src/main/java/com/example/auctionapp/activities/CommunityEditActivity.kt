package com.example.auctionapp.activities

import android.content.DialogInterface
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
import com.example.auctionapp.adapters.PictureAdapter
import com.example.auctionapp.adapters.PictureAdapter2
import com.example.auctionapp.databinding.ActivityCommunityEditBinding
import com.example.auctionapp.model.PictureItem

class CommunityEditActivity : AppCompatActivity() {

    lateinit var binding: ActivityCommunityEditBinding
    lateinit var items: MutableList<PictureItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        items = mutableListOf()
        binding.btnComplete.setOnClickListener { clickCompleteBtn() }
        binding.linear1.setOnClickListener { clickPicture() }
        binding.linear2.setOnClickListener { clickLocation() }
        binding.recycler.adapter = PictureAdapter2(this, items)
    }

    private fun clickCompleteBtn(){
        /*
        *       DB 에 커뮤니티 글 저장 ( 제목, 내용, 사진, 장소, 작성자 id, 작성자 동네, 작성자 프로필 사진)
        * */
        finish()
    }



    var launcher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {
            if(it.resultCode == RESULT_OK){
                var clipData = it.data?.clipData!!
                var size = clipData.itemCount

                for(i in 0 until size){
                    items.add(PictureItem(clipData.getItemAt(i).uri))
                }
                binding.recycler.adapter?.notifyDataSetChanged()

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
    *       장소 선택 버튼
    *
    * */
    private fun clickLocation() {
        /*
        *       원래는 정보를 받아와야함. 받아와서 원래 액티비티에 장소 정보 뿌려주고,
        *       뷰의 visibility 도 visible 로 처리해주어야 함. 일단 액티비티 이동만 시키자.
        *       그리고 x 버튼 누르면 visibility 를 gone 처리 해주면 될듯.
        *       그리고 새로 선택할 때 새로운 장소로 text 를 바꿔주자.
        * */
        startActivity(Intent(this,SelectPositionActivity::class.java))
    }
    override fun onSupportNavigateUp(): Boolean {
        var dialog = AlertDialog.Builder(this).setMessage("작성 중인 글이 있습니다. 종료하시겠습니까?").setPositiveButton("확인",
            DialogInterface.OnClickListener { dialog, which ->

                finish()
            }).setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->  }).create()

        dialog.show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.brand,theme))
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.brand,theme))

        return super.onSupportNavigateUp()
    }
}
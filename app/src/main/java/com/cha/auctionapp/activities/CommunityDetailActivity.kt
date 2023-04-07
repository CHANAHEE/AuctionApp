package com.cha.auctionapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import com.cha.auctionapp.R
import com.cha.auctionapp.adapters.CommentsAdapter
import com.cha.auctionapp.databinding.ActivityCommunityDetailBinding
import com.cha.auctionapp.model.CommentsItem

class CommunityDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityCommunityDetailBinding
    lateinit var items: MutableList<CommentsItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        items = mutableListOf()
        binding.recycler2.adapter = CommentsAdapter(this,items)

        binding.btnLocation.setOnClickListener { clickLocation() }
        binding.btnSend.setOnClickListener {
            clickSendBtn()

            /*
            *       보내기 버튼을 하면 댓글 리사이클러뷰를 다시 어댑터가 뿌려줘야 할듯.
            * */
        }
    }

    /*
    *
    *           보내기 버튼
    *
    * */
    private fun clickSendBtn() {

        items.add(CommentsItem(R.drawable._0,"1번","공릉 1동",binding.etMsg.text.toString(),"미래IT"))
//        items.add(CommentsItem(R.drawable._1,"2번","공릉 2동","안녕하세요",null))
//        items.add(CommentsItem(R.drawable._2,"3번","공릉 1동","안녕하세요",null))
//        items.add(CommentsItem(R.drawable._3,"4번","공릉 2동","안녕하세요",null))
//        items.add(CommentsItem(R.drawable._4,"5번","공릉 1동","안녕하세요",null))
//        items.add(CommentsItem(R.drawable._5,"6번","공릉 2동","안녕하세요",null))
//        items.add(CommentsItem(R.drawable._6,"7번","공릉 1동","안녕하세요","미래IT"))
        binding.recycler2.adapter?.notifyDataSetChanged()



        val imm : InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etMsg.windowToken,0)
        binding.etMsg.setText("")
        binding.etMsg.clearFocus()


    }

    /*
    *
    *       장소 버튼
    *
    * */
    private fun clickLocation() {
        /*
        *       여기도 장소정보를 다시 받아와야함. 받아와서 gone 되어있는 뷰에 뿌려주어야 함.
        * */
        startActivity(Intent(this, com.cha.auctionapp.activities.SelectPositionActivity::class.java))
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
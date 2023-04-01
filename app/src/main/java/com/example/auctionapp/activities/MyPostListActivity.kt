package com.example.auctionapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.auctionapp.R
import com.example.auctionapp.adapters.MyPostListAdapter
import com.example.auctionapp.databinding.ActivityMyPostListBinding
import com.example.auctionapp.databinding.ActivityMyProfileEditBinding
import com.example.auctionapp.model.CommunityPostItem

class MyPostListActivity : AppCompatActivity() {
    lateinit var binding : ActivityMyPostListBinding
    lateinit var listItems : MutableList<CommunityPostItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPostListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        listItems = mutableListOf()
        listItems.add(CommunityPostItem("안녕하세요~~~","이번에 묵동 구길 쪽으로 이사를 가는데 집사님들 혹시 아시나요?","묵동"))
        listItems.add(CommunityPostItem("123456~~~","숫자놀이?","공릉 1동"))
        listItems.add(CommunityPostItem("에베ㅔㅔㅔ~~~","이상한가요?","공릉 2동"))
        listItems.add(CommunityPostItem("저녁에 같이 산책해요~~~같이 산책하면 좋을것같아요! 장소는 내일 알려드려요!!","최근에 날씨도 좋은데 산책 어때요?","월계동"))
        listItems.add(CommunityPostItem("노원 공릉 헬스장 추천~~~","헬스에 빠지게되었습니다","묵동"))
        listItems.add(CommunityPostItem("애플워치 잃어버렸어요 ㅠㅠ","어제(3/31) 에 애플워치를 잃어버렸어요","월계동"))
        listItems.add(CommunityPostItem("말티즈를 찾고있습니다.","최근에 강아지를 잃어버렸어요 위치는 저도 잘 모르겠습니다. 꼭 좀 연락 부탁드려요.","묵동"))
        listItems.add(CommunityPostItem("저와 식재료를 반띵하실 분을 찾습니다.","혼자 사는 자취생입니다","묵동"))
        listItems.add(CommunityPostItem("토요 산책","산책해요!! ","묵동"))
        listItems.add(CommunityPostItem("진삼 독서모임","독서모임 입니다.","묵동"))
        if(listItems.size != 0) binding.tvNone.visibility = View.GONE
        binding.recycler.adapter = MyPostListAdapter(this,listItems)

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
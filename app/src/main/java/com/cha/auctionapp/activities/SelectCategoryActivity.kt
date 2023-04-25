package com.cha.auctionapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cha.auctionapp.R
import com.cha.auctionapp.adapters.CategoryAdapter
import com.cha.auctionapp.databinding.ActivitySelectCategoryBinding
import com.cha.auctionapp.model.CategoryItem

class SelectCategoryActivity : AppCompatActivity() {
    lateinit var binding : ActivitySelectCategoryBinding
    lateinit var items : MutableList<CategoryItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        items = mutableListOf()
        items.add(CategoryItem(R.drawable._001computer,"디지털 기기"))
        items.add(CategoryItem(R.drawable._002furniture,"가구"))
        items.add(CategoryItem(R.drawable._003malecloth,"남성의류"))
        items.add(CategoryItem(R.drawable._004femalecloth,"여성의류"))
        items.add(CategoryItem(R.drawable._005sports,"스포츠/레저"))
        items.add(CategoryItem(R.drawable._006cosmetics,"뷰티/미용"))
        items.add(CategoryItem(R.drawable._007game,"취미/게임"))
        items.add(CategoryItem(R.drawable._008ticket,"티켓/교환권"))
        items.add(CategoryItem(R.drawable._009books,"도서"))
        items.add(CategoryItem(R.drawable._010pot,"생활가전"))
        items.add(CategoryItem(R.drawable._011plant,"식물"))
        items.add(CategoryItem(R.drawable._012food,"식품"))
        items.add(CategoryItem(R.drawable._013petfood,"반려동물"))
        items.add(CategoryItem(R.drawable._014baby,"유아"))
        items.add(CategoryItem(R.drawable._015kitchen,"생활/주방"))
        items.add(CategoryItem(R.drawable._016etc,"기타"))

        binding.recycler.adapter = CategoryAdapter(this,items)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
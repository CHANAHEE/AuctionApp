package com.example.auctionapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.auctionapp.R
import com.example.auctionapp.adapters.CategoryAdapter
import com.example.auctionapp.databinding.ActivitySelectCategoryBinding
import com.example.auctionapp.model.CategoryItem

class SelectCategoryActivity : AppCompatActivity() {
    lateinit var binding : ActivitySelectCategoryBinding
    lateinit var items : MutableList<CategoryItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        items = mutableListOf()
        items.add(CategoryItem(R.mipmap.ic_launcher_round,"장난감"))
        items.add(CategoryItem(R.mipmap.ic_launcher_round,"장난감"))
        items.add(CategoryItem(R.mipmap.ic_launcher_round,"장난감"))
        items.add(CategoryItem(R.mipmap.ic_launcher_round,"장난감"))
        items.add(CategoryItem(R.mipmap.ic_launcher_round,"장난감"))
        items.add(CategoryItem(R.mipmap.ic_launcher_round,"장난감"))
        items.add(CategoryItem(R.mipmap.ic_launcher_round,"장난감"))
        items.add(CategoryItem(R.mipmap.ic_launcher_round,"장난감"))
        items.add(CategoryItem(R.mipmap.ic_launcher_round,"장난감"))
        items.add(CategoryItem(R.mipmap.ic_launcher_round,"장난감"))
        items.add(CategoryItem(R.mipmap.ic_launcher_round,"장난감"))
        items.add(CategoryItem(R.mipmap.ic_launcher_round,"장난감"))
        items.add(CategoryItem(R.mipmap.ic_launcher_round,"장난감"))
        items.add(CategoryItem(R.mipmap.ic_launcher_round,"장난감"))
        items.add(CategoryItem(R.mipmap.ic_launcher_round,"장난감"))
        items.add(CategoryItem(R.mipmap.ic_launcher_round,"장난감"))
        items.add(CategoryItem(R.mipmap.ic_launcher_round,"장난감"))
        items.add(CategoryItem(R.mipmap.ic_launcher_round,"장난감"))
        items.add(CategoryItem(R.mipmap.ic_launcher_round,"장난감"))
        items.add(CategoryItem(R.mipmap.ic_launcher_round,"장난감"))
        items.add(CategoryItem(R.mipmap.ic_launcher_round,"장난감"))
        items.add(CategoryItem(R.mipmap.ic_launcher_round,"장난감"))
        items.add(CategoryItem(R.mipmap.ic_launcher_round,"장난감"))
        items.add(CategoryItem(R.mipmap.ic_launcher_round,"장난감"))

        binding.recycler.adapter = CategoryAdapter(this,items)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
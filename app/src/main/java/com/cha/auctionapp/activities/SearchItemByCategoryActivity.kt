package com.cha.auctionapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.adapters.CategorySearchAdapter
import com.cha.auctionapp.adapters.ProductAdapter
import com.cha.auctionapp.databinding.ActivitySearchItemByCategoryBinding
import com.cha.auctionapp.model.CategoryItem
import com.cha.auctionapp.model.CategorySearchItem
import com.cha.auctionapp.model.MainItem
import com.cha.auctionapp.network.RetrofitHelper
import com.cha.auctionapp.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchItemByCategoryActivity : AppCompatActivity() {

    lateinit var binding:ActivitySearchItemByCategoryBinding
    lateinit var categoryItems:MutableList<CategorySearchItem>
    lateinit var categoryName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchItemByCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.ibSearch.setOnClickListener { clickCategorySearch() }

        categoryName = intent.getStringExtra("category").toString()
        binding.tvAppbarTitle.text = categoryName

        categoryItems = mutableListOf()
        loadData()
        /*
        *       items 에 검색된 데이터 넣기. 근데 인덱스를 기반으로 HomeDetail 만들꺼니까.
        *       인덱스를 잘 생각하자.
        * */
//        mainItems.add(MainItem("MainItem 1",R.drawable._0,"공릉동","34000원"))
//        mainItems.add(MainItem("MainItem 2",R.drawable._1,"공릉동","34000원"))
//        mainItems.add(MainItem("MainItem 2",R.drawable._2,"공릉동","34000원"))
//        mainItems.add(MainItem("MainItem 2",R.drawable._3,"공릉동","34000원"))
//        mainItems.add(MainItem("MainItem 2",R.drawable._4,"공릉동","34000원"))
//        mainItems.add(MainItem("MainItem 2",R.drawable._5,"공릉동","34000원"))
//        mainItems.add(MainItem("MainItem 2",R.drawable._6,"공릉동","34000원"))
//        mainItems.add(MainItem("MainItem 2",R.drawable._7,"공릉동","34000원"))
//        mainItems.add(MainItem("MainItem 2",R.drawable._8,"공릉동","34000원"))
//        mainItems.add(MainItem("MainItem 2",R.drawable._9,"공릉동","34000원"))
//        mainItems.add(MainItem("MainItem 2",R.drawable._0,"공릉동","34000원"))
//        mainItems.add(MainItem("MainItem 2",R.drawable._1,"공릉동","34000원"))
//        mainItems.add(MainItem("MainItem 2",R.drawable._2,"공릉동","34000원"))
//        mainItems.add(MainItem("MainItem 2",R.drawable._3,"공릉동","34000원"))
//        mainItems.add(MainItem("MainItem 2",R.drawable._4,"공릉동","34000원"))
//        mainItems.add(MainItem("MainItem 2",R.drawable._5,"공릉동","34000원"))
//        mainItems.add(MainItem("MainItem 2",R.drawable._6,"공릉동","34000원"))
//        mainItems.add(MainItem("MainItem 2",R.drawable._7,"공릉동","34000원"))
//
//        binding.recycler.adapter = CategorySearchAdapter(this,mainItems)
    }
    private fun loadData() {
        val retrofit = RetrofitHelper.getRetrofitInstance("http://tjdrjs0803.dothome.co.kr")
        val retrofitService = retrofit.create(RetrofitService::class.java)
        val call: Call<MutableList<CategorySearchItem>> = retrofitService.getDataFromServerForCategory(G.location,categoryName)
        call.enqueue(object : Callback<MutableList<CategorySearchItem>> {
            override fun onResponse(
                call: Call<MutableList<CategorySearchItem>>,
                response: Response<MutableList<CategorySearchItem>>
            ) {
                categoryItems = response.body()!!
                Log.i("zxicovhn","${categoryItems[0]}")
                binding.recycler.adapter = CategorySearchAdapter(this@SearchItemByCategoryActivity,categoryItems)
            }

            override fun onFailure(call: Call<MutableList<CategorySearchItem>>, t: Throwable) {
                Log.i("test01","${t.message}")
            }
        })
    }

    private fun clickCategorySearch() {
        if(binding.etSearch.visibility == View.GONE){
            binding.tvAppbarTitle.visibility = View.GONE
            binding.etSearch.visibility = View.VISIBLE

            binding.etSearch.setOnFocusChangeListener { v, hasFocus ->
                if(!hasFocus){
                    val imm: InputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
                    binding.tvAppbarTitle.visibility = View.VISIBLE
                    binding.etSearch.visibility = View.GONE
                    binding.etSearch.setText("")
                }
            }


            binding.etSearch.setOnKeyListener { v , keyCode, event ->
                if(event.action == KeyEvent.ACTION_DOWN
                    && keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    val imm : InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.etSearch.windowToken,0)
                    binding.recycler.requestFocus()
                    /*
                    *
                    *       검색 결과 처리 작업
                    *
                    * */
                    true
                }

                false
            }
        } else {
            binding.tvAppbarTitle.visibility = View.VISIBLE
            binding.etSearch.visibility = View.GONE
            binding.etSearch.setText("")
        }
    }

    /*
    *
    *       뒤로 가기 버튼
    *
    * */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

}
package com.cha.auctionapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.room.Room
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.adapters.MyFavoriteAdapter
import com.cha.auctionapp.databinding.ActivityMyFavoriteListBinding
import com.cha.auctionapp.model.AppDatabase
import com.cha.auctionapp.model.MainItem
import com.cha.auctionapp.model.MyFavListItem

class MyFavoriteListActivity : AppCompatActivity() {
    lateinit var binding : ActivityMyFavoriteListBinding
    lateinit var favorItems: MutableList<MyFavListItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyFavoriteListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    /*
    *
    *       초기화 작업
    *
    * */
    private fun init(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        favorItems = mutableListOf()
        loadMyFavItemListFromRoomDB()
    }

    /*
    *
    *       Room DB 에서 찜 목록 가져오기
    *
    * */
    private fun loadMyFavItemListFromRoomDB(){
        val db = Room.databaseBuilder(
            this,
            AppDatabase::class.java, "fav-database"
        ).build()

        val r = Runnable {
            var myFavList = db.myFavListItemDAO().getAll()
            favorItems = myFavList.toMutableList()
            var newFavorItems = mutableListOf<MyFavListItem>()

            for(i in 0 until favorItems.size){
                if("${favorItems[i].indexProduct.toString()}${G.userAccount.id}" == favorItems[i].idx) {
                    newFavorItems.add(favorItems[i])
                }
            }
            if(newFavorItems.isNotEmpty()) binding.tvNone.visibility = View.GONE
            else binding.tvNone.visibility = View.VISIBLE
            binding.recycler.adapter = MyFavoriteAdapter(this,newFavorItems)
        }
        Thread(r).start()
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
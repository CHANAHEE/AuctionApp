package com.cha.auctionapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.room.Room
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.adapters.MyAuctionFavoriteAdapter
import com.cha.auctionapp.adapters.MyCommunityFavoriteAdapter
import com.cha.auctionapp.adapters.MyFavoriteAdapter
import com.cha.auctionapp.databinding.ActivityMyFavoriteListBinding
import com.cha.auctionapp.model.AppDatabase
import com.cha.auctionapp.model.MainItem
import com.cha.auctionapp.model.MyAuctionFavListItem
import com.cha.auctionapp.model.MyCommunityFavListItem
import com.cha.auctionapp.model.MyFavListItem

class MyFavoriteListActivity : AppCompatActivity() {
    lateinit var binding : ActivityMyFavoriteListBinding
    lateinit var favorItems: MutableList<MyFavListItem>
    lateinit var communityFavorItems: MutableList<MyCommunityFavListItem>
    lateinit var auctionFavorItems: MutableList<MyAuctionFavListItem>

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
        loadItemListFromRoomDB()

    }

    /*
    *
    *       Room DB 에서 찜 목록 가져오기
    *
    *
    *             "myfav"         ->{}
            "mycommunityfav"->{}
            "myauctionfav"  ->{}
    * */
    private fun loadItemListFromRoomDB(){
        val db = Room.databaseBuilder(
            this,
            AppDatabase::class.java, "fav-database"
        ).build()
        when(intent.getStringExtra("navigation")){
            "myfav"         -> loadMyFavItemListFromRoomDB(db)
            "mycommunityfav"-> loadMyCommunityFavItemListFromRoomDB(db)
            "myauctionfav"  -> loadMyAuctionFavItemListFromRoomDB(db)
        }
    }




    /*
    *
    *       찜한 물건 가져오기
    *
    * */
    private fun loadMyFavItemListFromRoomDB(db: AppDatabase){
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
    *       관심 글 가져오기
    *
    * */
    private fun loadMyCommunityFavItemListFromRoomDB(db: AppDatabase){
        val r = Runnable {
            var myCommunityFavList = db.MyCommunityFavListItemDAO().getAll()
            communityFavorItems = myCommunityFavList.toMutableList()
            var newFavorItems = mutableListOf<MyCommunityFavListItem>()

            for(i in 0 until communityFavorItems.size){
                if("${communityFavorItems[i].indexProduct.toString()}${G.userAccount.id}" == communityFavorItems[i].idx) {
                    newFavorItems.add(communityFavorItems[i])
                }
            }
            if(newFavorItems.isNotEmpty()) binding.tvNone.visibility = View.GONE
            else binding.tvNone.visibility = View.VISIBLE
            binding.recycler.adapter = MyCommunityFavoriteAdapter(this,newFavorItems)
        }
        Thread(r).start()
    }


    /*
    *
    *       관심 경매 가져오기
    *
    * */

    private fun loadMyAuctionFavItemListFromRoomDB(db: AppDatabase){
        val r = Runnable {
            var myAuctionFavList = db.MyAuctionFavListItemDAO().getAll()
            auctionFavorItems = myAuctionFavList.toMutableList()
            var newFavorItems = mutableListOf<MyAuctionFavListItem>()

            for(i in 0 until auctionFavorItems.size){
                if("${auctionFavorItems[i].indexProduct.toString()}${G.userAccount.id}" == auctionFavorItems[i].idx) {
                    newFavorItems.add(auctionFavorItems[i])
                }
            }
            if(newFavorItems.isNotEmpty()) binding.tvNone.visibility = View.GONE
            else binding.tvNone.visibility = View.VISIBLE
            binding.recycler.adapter = MyAuctionFavoriteAdapter(this,newFavorItems)
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
package com.example.auctionapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.FragmentTransaction
import com.example.auctionapp.fragments.HomeFragment
import com.example.auctionapp.R
import com.example.auctionapp.databinding.ActivityMainBinding
import com.example.auctionapp.fragments.AuctionFragment
import com.example.auctionapp.fragments.ChatFragment
import com.example.auctionapp.fragments.CommunityFragment
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {

    val binding:ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        HomeFragment()
        CommunityFragment()
        AuctionFragment()
        ChatFragment()

        var tran:FragmentTransaction = supportFragmentManager.beginTransaction().add(R.id.container_fragment,HomeFragment())
        tran.commit()


        /*
        *       BottomNavigationView 선택
        * */
        binding.bnv.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener {
            tran = supportFragmentManager.beginTransaction()
            changeFragment(it,tran)
            return@OnItemSelectedListener true
        })

        setNavigationDrawer()
        binding.btnSelectTown.setOnClickListener { showMyPlaceList() }



    }

    private val POPUP_MENU_MY_FIRST_PLACE_ITEM_ID :Int? = 0
    private val POPUP_MENU_MY_SECOND_PLACE_ITEM_ID :Int? = 1
    private val POPUP_MENU_SET_PLACE_ITEM_ID :Int? = 2
    private fun showMyPlaceList(){
        val popupMenu:PopupMenu = PopupMenu(this,binding.btnSelectTown)
        popupMenu.menu.add(0, POPUP_MENU_MY_FIRST_PLACE_ITEM_ID!!,0,"공릉 1동")
        popupMenu.menu.add(0, POPUP_MENU_MY_SECOND_PLACE_ITEM_ID!!,0,"공릉 2동")
        popupMenu.menu.add(0, POPUP_MENU_SET_PLACE_ITEM_ID!!,0,"내 동네 설정")
        menuInflater.inflate(R.menu.popupmenu,popupMenu.menu)

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener {
            var id:Int = it.itemId
            if(id == POPUP_MENU_MY_FIRST_PLACE_ITEM_ID){
                // G 클래스에 내 동네 데이터 넣는 코드 작성
                binding.btnSelectTown.text = popupMenu
                    .menu
                    .getItem(POPUP_MENU_MY_FIRST_PLACE_ITEM_ID).toString()
            }else if(id == POPUP_MENU_MY_SECOND_PLACE_ITEM_ID){
                // G 클래스에 내 동네 데이터 넣는 코드 작성
                binding.btnSelectTown.text = popupMenu
                    .menu
                    .getItem(POPUP_MENU_MY_SECOND_PLACE_ITEM_ID).toString()
            }else if(id == POPUP_MENU_SET_PLACE_ITEM_ID){
                startActivity(Intent(this,SetUpMyPlaceActivity::class.java))
            }
            false
        }
    }
    /*
    *       Set NavigationDrawer
    * */
    private fun setNavigationDrawer() {
        setSupportActionBar(binding.toolbar)
        var drawerToggle:ActionBarDrawerToggle = ActionBarDrawerToggle(this,binding.drawerLayout,binding.toolbar,R.string.open,R.string.close)

        var actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        drawerToggle.syncState()
        actionBar?.title = null
        binding.drawerLayout.addDrawerListener(drawerToggle)
    }

    /*
    *       프래그먼트 전환 함수
    * */
    fun changeFragment(item:MenuItem,tran:FragmentTransaction){

        when(item.itemId){
            R.id.home_tab -> {
                binding.appbar.visibility = View.VISIBLE
                tran.replace(R.id.container_fragment, HomeFragment()).commit()
            }
            R.id.community_tab -> {
                binding.appbar.visibility = View.GONE
                tran.replace(R.id.container_fragment, CommunityFragment()).commit()
            }
            R.id.auction_tab -> {
                binding.appbar.visibility = View.GONE
                tran.replace(R.id.container_fragment, AuctionFragment()).commit()
            }
            R.id.chat_tab -> {
                binding.appbar.visibility = View.GONE
                tran.replace(R.id.container_fragment, ChatFragment()).commit()
            }
        }
    }
}
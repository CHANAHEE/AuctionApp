package com.example.auctionapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.Toast
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
    lateinit var popupMenu:PopupMenu


    private val POPUP_MENU_MY_FIRST_PLACE_ITEM_ID :Int? = 0
    private val POPUP_MENU_MY_SECOND_PLACE_ITEM_ID :Int? = 1
    private val POPUP_MENU_SET_PLACE_ITEM_ID :Int? = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        HomeFragment()
        CommunityFragment()
        AuctionFragment()
        ChatFragment()

        // 팝업메뉴 만들어 놓기. 그래서 처음 설정값을 정해두기
        popupMenu = PopupMenu(this,binding.btnSelectTown)
        popupMenu.menu.add(0, POPUP_MENU_MY_FIRST_PLACE_ITEM_ID!!,0,"공릉 1동")
        popupMenu.menu.add(0, POPUP_MENU_MY_SECOND_PLACE_ITEM_ID!!,0,"공릉 2동")
        popupMenu.menu.add(0, POPUP_MENU_SET_PLACE_ITEM_ID!!,0,"내 동네 설정")
        binding.btnSelectTown.text = popupMenu
            .menu
            .getItem(POPUP_MENU_MY_FIRST_PLACE_ITEM_ID).toString()


        /*
        *       BottomNavigationView 선택
        * */

        var tran:FragmentTransaction = supportFragmentManager
            .beginTransaction()
            .add(R.id.container_fragment,HomeFragment().apply {

                arguments = Bundle().apply {
                    putString("place",this@MainActivity.binding.btnSelectTown.text.toString())
                }
            })

        tran.commit()
        binding.bnv.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener {

            tran = supportFragmentManager.beginTransaction()
            changeFragment(it,tran)
            return@OnItemSelectedListener true
        })

        setNavigationDrawer()
        binding.btnSelectTown.setOnClickListener { clickMyPlace() }
        binding.ibSearch.setOnClickListener { clickEditSearch() }
        binding.ibCategory.setOnClickListener { clickCategoryBtn() }


    }

    /*
    *
    *       카테고리 버튼 클릭
    *
    * */
    private fun clickCategoryBtn() {
        startActivity(Intent(this, SelectCategoryActivity::class.java))

    }

    /*
    *
    *
    *       검색 EditText 설정
    *
    */
    private fun clickEditSearch() {
        if(binding.etSearch.visibility == View.INVISIBLE){
            binding.btnSelectTown.visibility = View.INVISIBLE
            binding.etSearch.visibility = View.VISIBLE

            binding.etSearch.setOnFocusChangeListener { v, hasFocus ->
                if(!hasFocus){
                    val imm: InputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
                    binding.btnSelectTown.visibility = View.VISIBLE
                    binding.etSearch.visibility = View.INVISIBLE
                    binding.etSearch.setText("")
                }
            }


            binding.etSearch.setOnKeyListener { v , keyCode, event ->
                if(event.action == KeyEvent.ACTION_DOWN
                    && keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    val imm : InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.etSearch.windowToken,0)
                    binding.containerFragment.requestFocus()
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
            binding.btnSelectTown.visibility = View.VISIBLE
            binding.etSearch.visibility = View.INVISIBLE
            binding.etSearch.setText("")
        }
    }





    private fun clickMyPlace(){



        menuInflater.inflate(R.menu.popupmenu,popupMenu.menu)

        popupMenu.show()

        // 각 popupMenu 의 동네 이름 string 값이 아니고, id 값을 기준으로 분기문이 실행된다.
        // 그러니, 기본설정값 만들 때, 주의할것.
        popupMenu.setOnMenuItemClickListener {
            var id:Int = it.itemId
            if(id == POPUP_MENU_MY_FIRST_PLACE_ITEM_ID){
                // G 클래스에 내 동네 데이터 넣는 코드 작성
                binding.btnSelectTown.text = popupMenu
                    .menu
                    .getItem(POPUP_MENU_MY_FIRST_PLACE_ITEM_ID).toString()

                var tran:FragmentTransaction = supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container_fragment,HomeFragment().apply {

                        arguments = Bundle().apply {
                            putString("place",this@MainActivity.binding.btnSelectTown.text.toString())
                        }
                    })
                tran.commit()
            }else if(id == POPUP_MENU_MY_SECOND_PLACE_ITEM_ID){
                // G 클래스에 내 동네 데이터 넣는 코드 작성
                binding.btnSelectTown.text = popupMenu
                    .menu
                    .getItem(POPUP_MENU_MY_SECOND_PLACE_ITEM_ID).toString()
                var tran:FragmentTransaction = supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container_fragment,HomeFragment().apply {

                        arguments = Bundle().apply {
                            putString("place",this@MainActivity.binding.btnSelectTown.text.toString())
                        }
                    })
                tran.commit()
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

        binding.nav.getHeaderView(0).findViewById<View>(R.id.btn_edit_profile).setOnClickListener {
            when(it.id){
                R.id.btn_edit_profile->{
                    startActivity(Intent(this,MyProfileEditActivity::class.java))
                }
            } 
            
        }
        binding.nav.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.menu_my_fav_list->{
                    startActivity(Intent(this,MyFavoriteListActivity::class.java))
                }
                R.id.menu_my_post_list->{
                    startActivity(Intent(this,MyPostListActivity::class.java))
                }
            }
            binding.drawerLayout.closeDrawer(binding.nav)
            false
        }
    }

    /*
    *       프래그먼트 전환 함수
    * */
    private fun changeFragment(item:MenuItem,tran:FragmentTransaction){

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
package com.example.actionapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.FragmentTransaction
import com.example.actionapp.fragments.HomeFragment
import com.example.actionapp.R
import com.example.actionapp.databinding.ActivityMainBinding
import com.example.actionapp.fragments.AuctionFragment
import com.example.actionapp.fragments.ChatFragment
import com.example.actionapp.fragments.CommunityFragment
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


        /*
        *       NavigationDrawer
        * */
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
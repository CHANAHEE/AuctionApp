package com.example.actionapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import com.example.actionapp.fragments.HomeFragment
import com.example.actionapp.R
import com.example.actionapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val binding:ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tran:FragmentTransaction = supportFragmentManager.beginTransaction()



        binding.bnv.setOnClickListener {changeFragment(it,tran)}
    }

    fun changeFragment(it:View,tran:FragmentTransaction){
        when(it.id){
            R.id.home_tab ->{
                tran.add(R.id.container_fragment, HomeFragment()).commit()

            }
            R.id.community_tab ->{
                tran.add(R.id.container_fragment, HomeFragment()).commit()
            }
            R.id.auction_tab ->{
                tran.add(R.id.container_fragment, HomeFragment()).commit()
            }
            R.id.chat_tab ->{
                tran.add(R.id.container_fragment, HomeFragment()).commit()
            }

        }
    }
}
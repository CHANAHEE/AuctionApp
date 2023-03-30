package com.example.auctionapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.View.GONE
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.fragment.app.FragmentTransaction
import com.example.auctionapp.R
import com.example.auctionapp.fragments.SignUpEmailInputFragment
import com.example.auctionapp.databinding.ActivityLoginBinding
import com.example.auctionapp.fragments.LoginMainFragment
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    private val binding:ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val tran:FragmentTransaction = supportFragmentManager
            .beginTransaction()
            .add(R.id.container_fragment,LoginMainFragment())
        tran.commit()
    }
    
    



}
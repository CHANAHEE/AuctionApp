package com.example.actionapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import androidx.fragment.app.FragmentTransaction
import com.example.actionapp.R
import com.example.actionapp.fragments.SignUpEmailInputFragment
import com.example.actionapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    val binding:ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            var intent: Intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnSignup.setOnClickListener {
            binding.loginrootview.visibility = GONE
            var fragmentTransaction:FragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.container_fragment, SignUpEmailInputFragment()).commit()

        }
    }
}
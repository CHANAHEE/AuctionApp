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
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    private val binding:ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        editTextListener()
        binding.btnSignup.setOnClickListener {clickSignUp()}
        binding.btnLogin.setOnClickListener { clickLogin() }
        binding.btnKakao.setOnClickListener { clickKakao() }
    }
    
    
    private fun clickKakao(){
        /*
        *       kakao 로그인 API 활용
        * */
    }


    private fun clickLogin(){
        /*
        *       가입된 회원 정보를 userId 와 userPass 에 넣기
        * */
        var userId = "aaa"
        var userPass = "1234"

        if(userId.equals(binding.etId.text.toString()) && userPass.equals(binding.etPass.text.toString())){
            var intent: Intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Snackbar.make(binding.root,"아이디 혹은 비밀번호가 틀렸습니다.",Snackbar.LENGTH_SHORT).show()
        }
    }
    private fun clickSignUp(){
        binding.loginRootview.visibility = GONE
        var fragmentTransaction:FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.container_fragment, SignUpEmailInputFragment()).commit()
    }
    private fun editTextListener(){
        binding.etPass.setOnKeyListener { v , keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_ENTER)
            {
                val imm : InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.etPass.windowToken,0)
                true
            }

            false
        }
    }


}
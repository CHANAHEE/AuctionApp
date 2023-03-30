package com.example.auctionapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.FragmentTransaction
import com.example.auctionapp.R
import com.example.auctionapp.activities.LoginActivity
import com.example.auctionapp.activities.MainActivity
import com.example.auctionapp.databinding.FragmentLoginMainBinding
import com.google.android.material.snackbar.Snackbar

class LoginMainFragment : Fragment() {

    lateinit var binding : FragmentLoginMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginMainBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            var intent: Intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
            LoginActivity().finish()
        } else {
            Snackbar.make(binding.root,"아이디 혹은 비밀번호가 틀렸습니다.", Snackbar.LENGTH_SHORT).show()
        }
    }
    private fun clickSignUp(){
        var tran: FragmentTransaction? = activity?.supportFragmentManager
            ?.beginTransaction()
            ?.addToBackStack(null)
            ?.replace(R.id.container_fragment, SignUpEmailInputFragment())
        tran?.commit()

    }
    private fun editTextListener(){
        binding.etPass.setOnKeyListener { v , keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_ENTER)
            {

                val imm : InputMethodManager = context?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.etPass.windowToken,0)
                true
            }

            false
        }
    }

}
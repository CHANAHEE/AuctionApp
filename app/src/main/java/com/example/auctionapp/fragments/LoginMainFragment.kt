package com.example.auctionapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.auctionapp.R
import com.example.auctionapp.activities.LoginActivity
import com.example.auctionapp.activities.MainActivity
import com.example.auctionapp.databinding.FragmentLoginMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

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
    }

    private fun clickKakao(){
        /*
        *       kakao 로그인 API 활용
        * */
    }


    private fun clickLogin(){
        /*
        *       firebase 에서 유저 정보 가져와서 비교 후 로그인
        * */
        var firebase: FirebaseFirestore = FirebaseFirestore.getInstance()
        var userRef: CollectionReference = firebase.collection("user")


        userRef.whereEqualTo("email",binding.etId.text.toString())
            .whereEqualTo("password",binding.etPass.text.toString())
            .addSnapshotListener { value, error ->
                if (value != null) {
                    for (snapshot in value) {
                        if (binding.etId.text.toString() == snapshot.data["email"].toString()
                            && binding.etPass.text.toString() == snapshot.data["password"].toString()
                        ) {
                            var intent: Intent = Intent(context, MainActivity::class.java)
                            startActivity(intent)
                            LoginActivity().finish()
                        }
                    }
                    Snackbar.make(
                        activity?.findViewById(android.R.id.content)!!,
                        "이메일 혹은 비밀번호가 잘못 입력 되었습니다.", Snackbar.LENGTH_LONG).show()
                }
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
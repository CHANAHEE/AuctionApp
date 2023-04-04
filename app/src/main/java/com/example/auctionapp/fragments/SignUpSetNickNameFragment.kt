package com.example.auctionapp.fragments

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.auctionapp.R
import com.example.auctionapp.databinding.FragmentSignUpSetNickNameBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore


class SignUpSetNickNameFragment : Fragment() {

    val binding by lazy { FragmentSignUpSetNickNameBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editTextListener()
        binding.btnBack.setOnClickListener { clickBackBtn() }
        binding.btnComplete.setOnClickListener { clickCompleteBtn() }

    }

    private fun clickBackBtn(){
        val fragment = activity?.supportFragmentManager
        fragment?.popBackStack()
    }

    private fun editTextListener(){
        binding.etNickname.setOnKeyListener { v , keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_ENTER)
            {
                val imm : InputMethodManager = context?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.etNickname.windowToken,0)
                true
            }

            false
        }
    }

    private fun clickCompleteBtn(){

        val fragmentManager : FragmentManager? = activity?.supportFragmentManager
        fragmentManager?.popBackStack(null,FragmentManager.POP_BACK_STACK_INCLUSIVE)

        saveUserInfo()
        Snackbar.make(binding.root,"가입 완료!",Snackbar.LENGTH_SHORT).show()

    }

    /*
    *       유저 정보 firestore 에 저장
    * */
    private fun saveUserInfo(){
        var email = arguments?.getString("email")!!
        var password = arguments?.getString("password")!!
        var name = arguments?.getString("name")!!
        var birth = arguments?.getString("birth")!!
        var location = arguments?.getString("location")!!
        var nickname = binding.etNickname.text.toString()

        var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        var userRef: CollectionReference = firestore.collection("user")

        var user: MutableMap<String,String> = mutableMapOf<String,String>()
        user.put("email",email)
        user.put("password",password)
        user.put("name",name)
        user.put("birth",birth)
        user.put("location",location)
        user.put("nickname",nickname)

        userRef.document().set(user)


    }
}


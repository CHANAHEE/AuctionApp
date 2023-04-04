package com.example.auctionapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.auctionapp.R
import com.example.auctionapp.activities.LoginActivity
import com.example.auctionapp.activities.MainActivity
import com.example.auctionapp.databinding.FragmentSignUpEmailInputBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class SignUpEmailInputFragment : Fragment() {

    val binding:FragmentSignUpEmailInputBinding by lazy { FragmentSignUpEmailInputBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextListener()
        binding.btnNext.setOnClickListener { clickNextBtn() }
        binding.btnBack.setOnClickListener { clickBackBtn() }
    }

    private fun clickBackBtn(){
        val fragment = activity?.supportFragmentManager
        fragment?.popBackStack()
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
    private fun clickNextBtn(){
        /*
  *
  *       이메일 중복체크 및 유효성 검사
  *
  * */

        var firebase: FirebaseFirestore = FirebaseFirestore.getInstance()
        var userRef: CollectionReference = firebase.collection("user")

        if(binding.etEmail.text.toString() == ""){
            Snackbar.make(
                activity?.findViewById(android.R.id.content)!!,
                "이메일을 입력해주세요.", Snackbar.LENGTH_SHORT).show()
            binding.etEmail.requestFocus()
        } else if(binding.etPass.text.toString() == ""){
            Snackbar.make(
                activity?.findViewById(android.R.id.content)!!,
                "비밀번호를 입력해주세요.", Snackbar.LENGTH_SHORT).show()
            binding.etPass.requestFocus()
        }

        userRef.whereEqualTo("email",binding.etEmail.text.toString())
            .addSnapshotListener { value, error ->
                if (value != null) {
                    for (snapshot in value) {
                        if (binding.etEmail.text.toString() == snapshot.data["email"].toString()) {
                            Snackbar.make(
                                activity?.findViewById(android.R.id.content)!!,
                                "이미 가입된 이메일 입니다.", Snackbar.LENGTH_SHORT).show()
                            binding.etEmail.requestFocus()
                            return@addSnapshotListener
                        }
                    }

                    var fragment = SignUpPersonalInfoFragment()
                    var bundle = Bundle()
                    bundle.putString("email",binding.etEmail.text.toString())
                    bundle.putString("password",binding.etPass.text.toString())
                    fragment.arguments = bundle
                    val tran:FragmentTransaction? =
                        activity?.
                        supportFragmentManager?.
                        beginTransaction()?.
                        replace(R.id.container_fragment,fragment)?.addToBackStack(null)

                    tran?.commit()

                }
            }
    }
}
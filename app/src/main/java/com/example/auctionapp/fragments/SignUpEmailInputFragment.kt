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
        binding.btnCertifyEmail.tag = 0
        binding.btnCertifyEmail.setOnClickListener { clickCertifyBtn() }
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
        *       입력 안된 영역 걸러내기, 비밀번호 확인 체크
        * */
        if(dataCheck()) return

        /*
        *       이메일 중복체크 및 유효성 검사
        * */

        if(binding.btnCertifyEmail.tag == 0) {
            Snackbar.make(
                activity?.findViewById(android.R.id.content)!!,
                "이메일 중복체크를 해주세요.", Snackbar.LENGTH_SHORT).show()
            return
        }

        /*
        *       두번째 프래그먼트 실행
        * */
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

    val EXIST_EMAIL = 0
    val NOT_EXIST_EMAIL = 1
    private fun clickCertifyBtn() {
        var firebase: FirebaseFirestore = FirebaseFirestore.getInstance()
        var userRef: CollectionReference = firebase.collection("user")

        userRef.whereEqualTo("email",binding.etEmail.text.toString())
            .addSnapshotListener { value, error ->
                if (value != null) {
                    for (snapshot in value) {
                        if (binding.etEmail.text.toString() == snapshot.data["email"].toString()) {
                            Snackbar.make(
                                activity?.findViewById(android.R.id.content)!!,
                                "이미 가입된 이메일 입니다.", Snackbar.LENGTH_SHORT).show()
                            binding.etEmail.requestFocus()
                            binding.btnCertifyEmail.tag = EXIST_EMAIL
                            return@addSnapshotListener
                        }
                    }

                    Snackbar.make(
                        activity?.findViewById(android.R.id.content)!!,
                        "사용 가능한 이메일 입니다.", Snackbar.LENGTH_SHORT).show()
                    binding.btnCertifyEmail.tag = NOT_EXIST_EMAIL

                }
            }

    }

    private fun dataCheck() : Boolean{

        if(binding.etEmail.text.toString() == ""){
            Snackbar.make(
                activity?.findViewById(android.R.id.content)!!,
                "이메일을 입력해주세요.", Snackbar.LENGTH_SHORT).show()
            binding.etEmail.requestFocus()
            return true
        } else if(binding.etPass.text.toString() == ""){
            Snackbar.make(
                activity?.findViewById(android.R.id.content)!!,
                "비밀번호를 입력해주세요.", Snackbar.LENGTH_SHORT).show()
            binding.etPass.requestFocus()
            return true
        } else if(binding.etPassCertify.text.toString() != binding.etPass.text.toString()){
            Snackbar.make(
                activity?.findViewById(android.R.id.content)!!,
                "비밀번호가 동일하지 않습니다.", Snackbar.LENGTH_SHORT).show()
            binding.etPassCertify.requestFocus()
            return true
        }

        return false
    }
}
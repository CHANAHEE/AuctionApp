package com.cha.auctionapp.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.cha.auctionapp.R
import com.cha.auctionapp.databinding.FragmentSignUpSetNickNameBinding
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
        binding.btnCertifyNickname.setOnClickListener { clickCertifyBtn() }
        binding.etNickname.addTextChangedListener(watcher)
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

        if(binding.etNickname.text.length < 3){
            Snackbar.make(binding.root,"닉네임은 3글자 이상으로 설정해주세요",Snackbar.LENGTH_SHORT).show()
            return
        }

        if(isExistNickname){
            Snackbar.make(binding.root,"닉네임 중복체크를 해주세요",Snackbar.LENGTH_SHORT).show()
            return
        }else{
            val fragmentManager : FragmentManager? = activity?.supportFragmentManager
            fragmentManager?.popBackStack(null,FragmentManager.POP_BACK_STACK_INCLUSIVE)

            saveUserInfo()
            Snackbar.make(binding.root,"가입 완료!",Snackbar.LENGTH_SHORT).show()
        }


    }

    var isExistNickname = false
    private fun clickCertifyBtn(){
        var firebase: FirebaseFirestore = FirebaseFirestore.getInstance()
        var userRef: CollectionReference = firebase.collection("user")

        userRef.whereEqualTo("nickname",binding.etNickname.text.toString()).get().addOnSuccessListener {
            if(it.documents.size > 0) {
                Snackbar.make(binding.root,"이미 있는 닉네임 입니다.",Snackbar.LENGTH_SHORT).show()
                isExistNickname = true
            } else {
                Snackbar.make(binding.root,"사용 가능한 닉네임 입니다.",Snackbar.LENGTH_SHORT).show()
                isExistNickname = false
            }
        }
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

    val watcher: TextWatcher = object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if(s?.length!! > 3) {
                binding.btnCertifyNickname.isEnabled = true
                binding.btnCertifyNickname.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.brand,requireContext().theme))
            }
        }

        override fun afterTextChanged(s: Editable?) {

        }

    }
}


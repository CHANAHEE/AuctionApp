package com.cha.auctionapp.fragments

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.databinding.FragmentSignUpSetNickNameBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern


class SignUpSetNickNameFragment : Fragment() {

    val binding by lazy { FragmentSignUpSetNickNameBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnComplete.setOnClickListener { clickCompleteBtn() }
        binding.btnCertifyNickname.setOnClickListener { clickCertifyBtn() }
        binding.etNickname.addTextChangedListener(watcher)

        editTextListener()
        binding.btnBack.setOnClickListener { clickBackBtn() }
    }


    /*
    *
    *       닉네임 설정 완료 버튼 : BackStack 지우고 EmailLogin 으로 이동
    *
    * */
    private fun clickCompleteBtn(){

        if(binding.etNickname.text.length < 3){
            Snackbar.make(binding.root,"닉네임은 3글자 이상으로 설정해주세요",Snackbar.LENGTH_SHORT).show()
            return
        }else if(isExistNickname){
            Snackbar.make(binding.root,"닉네임 중복체크를 해주세요",Snackbar.LENGTH_SHORT).show()
            return
        }else{
            val fragmentManager : FragmentManager? = activity?.supportFragmentManager
            fragmentManager?.popBackStack(null,FragmentManager.POP_BACK_STACK_INCLUSIVE)
            G.nickName = binding.etNickname.text.toString()
            saveUserInfo()
            Snackbar.make(binding.root,"가입 완료!",Snackbar.LENGTH_SHORT).show()
        }
    }



    /*
    *
    *       유저 정보 firestore 에 저장
    *
    * */
    private fun saveUserInfo(){
        var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        var userRef: CollectionReference = firestore.collection("user")
        G.profileImg = getURLForResource(R.drawable.default_profile)

        val id = userRef.document().id
        var user = mutableMapOf<String,Any>()
        user.put("email",G.userAccount.email)
        user.put("password",G.password)
        user.put("location",G.location)
        user.put("nickname",G.nickName)
        user.put("profileImage",G.profileImg)
        user.put("id",id)
        userRef.document(id).set(user)

    }
    private fun getURLForResource(resId: Int): Uri {
        return Uri.parse("android.resource://" + (R::class.java.getPackage()?.getName()) + "/" + resId)
    }




    /*
    *
    *       닉네임 중복체크
    *
    * */
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
    *
    *       닉네임 EditText 리스너
    *
    * */
    val watcher: TextWatcher = object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if(binding.etNickname.text.toString().length < 3 ){
                binding.btnCertifyNickname.isEnabled = false
                binding.btnCertifyNickname.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.edit_fail,requireContext().theme))
                binding.tvAlertNicknameLength.visibility = View.VISIBLE
            }
            else if(!Pattern.matches("^[a-zA-Z0-9가-힣]{3,}$",binding.etNickname.text.toString())) {
                binding.btnCertifyNickname.isEnabled = false
                binding.btnCertifyNickname.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.edit_fail,requireContext().theme))
                binding.tvAlertNicknameLength.visibility = View.GONE
                binding.tvAlertNickname.visibility = View.VISIBLE
            }else{
                binding.btnCertifyNickname.isEnabled = true
                binding.btnCertifyNickname.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.edit_success,requireContext().theme))
                binding.tvAlertNicknameLength.visibility = View.GONE
                binding.tvAlertNickname.visibility = View.GONE
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }



    /*
    *
    *       닉네임 설정 완료 시, 키보드 숨기기
    *
    * */
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



    /*
    *
    *       뒤로가기 버튼
    *
    * */
    private fun clickBackBtn(){
        val fragment = activity?.supportFragmentManager
        fragment?.popBackStack()
    }
}


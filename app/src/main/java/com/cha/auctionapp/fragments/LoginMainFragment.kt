package com.cha.auctionapp.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.activities.EmailLoginActivity
import com.cha.auctionapp.activities.SNSLoginActivity
import com.cha.auctionapp.activities.MainActivity
import com.cha.auctionapp.activities.MyProfileEditActivity
import com.cha.auctionapp.databinding.FragmentLoginMainBinding
import com.cha.auctionapp.model.NidUserInfoResponse
import com.cha.auctionapp.model.UserAccount
import com.cha.auctionapp.network.RetrofitHelper
import com.cha.auctionapp.network.RetrofitService
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginMainFragment : Fragment() {

    lateinit var binding : FragmentLoginMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginMainBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignup.setOnClickListener { clickSignUp() }
        binding.btnLogin.setOnClickListener { clickLogin() }
        binding.etPass.setOnKeyListener(listener)

        val keyHash:String = Utility.getKeyHash(requireContext())
        Log.i("keyhash",keyHash)
    }


    /*
    *
    *       회원가입 후, 로그인 기능
    *
    * */
    private fun clickLogin(){
        var firebase: FirebaseFirestore = FirebaseFirestore.getInstance()
        var userRef: CollectionReference = firebase.collection("user")

        userRef.whereEqualTo("email",binding.etId.text.toString())
            .whereEqualTo("password",binding.etPass.text.toString())
            .get()
            .addOnSuccessListener {
                for(document in it.documents){
                    G.nickName = document["nickname"].toString()
                    G.location = document["location"].toString()
                    G.userAccount.id = document["id"].toString()
                    G.userAccount.email = document["email"].toString()
                }

                var intent: Intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
                (context as EmailLoginActivity).finish()
            }.addOnFailureListener {
                Snackbar.make(
                    binding.root,
                    "이메일 혹은 비밀번호가 잘못 입력 되었습니다.", Snackbar.LENGTH_LONG).show()
            }
//            .addSnapshotListener { value, error ->
//                if (value != null) {
//                    for (snapshot in value) {
//                        G.nickName = snapshot.data["nickname"].toString()
//                        G.location = snapshot.data["location"].toString()
//                        G.userAccount.id = snapshot.data["id"].toString()
//                        G.userAccount.email = snapshot.data["email"].toString()
//                        var intent: Intent = Intent(context, MainActivity::class.java)
//                        startActivity(intent)
//                        (context as EmailLoginActivity).finish()
//                    }
//                    Snackbar.make(
//                        binding.root,
//                        "이메일 혹은 비밀번호가 잘못 입력 되었습니다.", Snackbar.LENGTH_LONG).show()
//                }
//            }
    }


    /*
    *
    *       회원가입 버튼
    *
    * */
    private fun clickSignUp(){
        var tran: FragmentTransaction? = activity?.supportFragmentManager
            ?.beginTransaction()
            ?.addToBackStack(null)
            ?.replace(R.id.container_fragment, SignUpEmailInputFragment())
        tran?.commit()
    }

    val listener = View.OnKeyListener { v, keyCode, event ->
        if (event.action == KeyEvent.ACTION_DOWN
            && keyCode == KeyEvent.KEYCODE_ENTER
        ) {
            val imm: InputMethodManager =
                context?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.etPass.windowToken, 0)
            true
        }
        false
    }
}
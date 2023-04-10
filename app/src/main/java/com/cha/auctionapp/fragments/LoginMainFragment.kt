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
import android.view.View.OnKeyListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.activities.LoginActivity
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

        binding.btnSignup.setOnClickListener { clickSignUp() }
        binding.btnLogin.setOnClickListener { clickLogin() }
        binding.etPass.setOnKeyListener(listener)

        binding.ivLoginGoogle.setOnClickListener { googleLogin() }
        binding.ivLoginKakao.setOnClickListener { kakaoLogin() }
        binding.ivLoginNaver.setOnClickListener { naverLogin() }

        val keyHash:String = Utility.getKeyHash(requireContext())
        Log.i("keyhash",keyHash)
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
                            G.nickName = snapshot.data.get("nickname").toString()
                            G.location = snapshot.data.get("location").toString()

                            var intent: Intent = Intent(context, MainActivity::class.java)
                            startActivity(intent)
                            activity?.finish()
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

    val listener = OnKeyListener { v, keyCode, event ->
        if(event.action == KeyEvent.ACTION_DOWN
            && keyCode == KeyEvent.KEYCODE_ENTER)
        {
            val imm : InputMethodManager = context?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.etPass.windowToken,0)
            true
        }
        false
    }


    /*
    *
    *       구글 로그인 기능
    *
    * */

    private fun googleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        var client: GoogleSignInClient = GoogleSignIn.getClient(requireContext(),gso)

        val signInIntent: Intent = client.signInIntent
        launcher.launch(signInIntent)

    }

    val launcher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()
        , ActivityResultCallback {

            var intent: Intent? = it.data
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(intent)
            val account: GoogleSignInAccount = task.result

            val pref: SharedPreferences = requireContext().getSharedPreferences("Data",Context.MODE_PRIVATE)
            var id: String = pref.getString("google","").toString()

            if(id.isBlank()){
                id = account.id ?: ""
                var email: String = account.email ?: ""
                G.userAccount = UserAccount(id,email)

                val editor: SharedPreferences.Editor = pref.edit()
                editor.putString("google",id)
                editor.apply()
                launcherActivity.launch(Intent(context,MyProfileEditActivity::class.java).putExtra("Login","Login").addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY))
            }else{
                var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
                var userRef: CollectionReference = firestore.collection("user")

                userRef.document(id).get().addOnSuccessListener {
                    G.userAccount.email = it.get("email").toString()
                    G.userAccount.id = it.get("id").toString()
                    G.nickName = it.get("nickname").toString()
                    G.profile = Uri.parse(it.get("profile").toString())
                    G.location = it.get("location").toString()

                    startActivity(Intent(context, MainActivity::class.java))
                    activity?.finish()

                    return@addOnSuccessListener
                }
            }
        })


    /*
    *
    *       카카오 로그인 기능
    *
    * */
    private fun kakaoLogin() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Snackbar.make(binding.root,"카카오 로그인 실패",Snackbar.LENGTH_SHORT)
            } else if (token != null) {
                UserApiClient.instance.me { user, error ->
                    if(user != null){
                        val pref: SharedPreferences = requireContext().getSharedPreferences("Data",Context.MODE_PRIVATE)
                        var id: String = pref.getString("kakao","").toString()

                        if(id.isBlank()){
                            id = user.id.toString()
                            var email: String = user.kakaoAccount?.email ?: ""
                            G.userAccount = UserAccount(id,email)
                            val editor: SharedPreferences.Editor = pref.edit()
                            editor.putString("kakao",id)
                            editor.apply()

                            launcherActivity.launch(Intent(context,MyProfileEditActivity::class.java).putExtra("Login","Login").addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY))
                        }else{
                            var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
                            var userRef: CollectionReference = firestore.collection("user")

                            userRef.document(id).get().addOnSuccessListener {
                                G.userAccount.email = it.get("email").toString()
                                G.userAccount.id = it.get("id").toString()
                                G.nickName = it.get("nickname").toString()
                                G.profile = Uri.parse(it.get("profile").toString())
                                G.location = it.get("location").toString()

                                startActivity(Intent(context, MainActivity::class.java))
                                activity?.finish()

                                return@addOnSuccessListener
                            }

//                        userRef.get().addOnSuccessListener { p0 ->
//                            if (p0 != null) {
//                                for (snapshot in p0) {
//                                    var user = snapshot.data
//                                    if(id == user.get("id")){
//                                        G.nickName = user.get("nickname").toString()
//                                        G.profile = Uri.parse(user.get("profile").toString())
//                                        G.userAccount?.email = user.get("email").toString()
//                                        var list = user.get("location").toString().split(" ")
//                                        G.location = list[list.lastIndex - 1]
//
//                                        Log.i("kakaoLogin","${G.nickName} : ${G.profile} : ${G.location}")
//                                        startActivity(Intent(context,MainActivity::class.java))
//                                        activity?.finish()
//                                        return@addOnSuccessListener
//                                    }else{
//
//                                    }
//                                }
//                                Log.i("kakaoLogin","${id} 동일한게 없음")
//                                launcherActivity.launch(Intent(context,MyProfileEditActivity::class.java).putExtra("Login","Login").addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY))
//                            }
//                        }
                        }
                    }
                }
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
            UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                if (error != null) {
                    Log.i("kakaoLogin","1")
                    UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
                } else if (token != null) {
                    Log.i("kakaoLogin","2")
                    UserApiClient.instance.loginWithKakaoTalk(requireContext(),callback = callback)
                }
            }
        } else {
            Log.i("kakaoLogin","3")
            UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
        }
    }


    /*
    *
    *        네이버 로그인 기능
    *
    * */
    private fun naverLogin() {
        NaverIdLoginSDK.initialize(requireContext(), "6Sq_GSOmgCFqeqaJVNtm", "tTZx8fr_jR", "근방")

        NaverIdLoginSDK.authenticate(requireContext(),object : OAuthLoginCallback{
            override fun onError(errorCode: Int, message: String) {
                Snackbar.make(binding.root,"네이버 로그인 에러 : ${message}",Snackbar.LENGTH_SHORT).show()
            }

            override fun onFailure(httpStatus: Int, message: String) {
                Snackbar.make(binding.root,"네이버 로그인 실패 : ${message}",Snackbar.LENGTH_SHORT).show()
            }

            override fun onSuccess() {

                val pref: SharedPreferences = requireContext().getSharedPreferences("Data",Context.MODE_PRIVATE)
                var id: String = pref.getString("naver","").toString()

                if(!id.isBlank()) {
                    var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
                    var userRef: CollectionReference = firestore.collection("user")

                    userRef.document(id).get().addOnSuccessListener {
                        G.userAccount.email = it.get("email").toString()
                        G.userAccount.id = it.get("id").toString()
                        G.nickName = it.get("nickname").toString()
                        G.profile = Uri.parse(it.get("profile").toString())
                        G.location = it.get("location").toString()

                        startActivity(Intent(context, MainActivity::class.java))
                        activity?.finish()

                        return@addOnSuccessListener
                    }
                }
                else {
                    val accessToken: String? = NaverIdLoginSDK.getAccessToken()

                    val retrofit = RetrofitHelper.getRetrofitInstance("https://openapi.naver.com")
                    retrofit
                        .create(RetrofitService::class.java)
                        .getNaverUserInfo("Bearer ${accessToken}")
                        .enqueue(object : Callback<NidUserInfoResponse> {
                            override fun onResponse(
                                call: Call<NidUserInfoResponse>,
                                response: Response<NidUserInfoResponse>
                            ) {
                                val userInfoResponse = response.body()
                                val id: String = userInfoResponse?.response?.id ?: ""
                                val email: String = userInfoResponse?.response?.email ?: ""

                                G.userAccount = UserAccount(id, email)

                                val editor: SharedPreferences.Editor = pref.edit()
                                editor.putString("naver", id)
                                editor.apply()


                                launcherActivity.launch(
                                    Intent(
                                        context,
                                        MyProfileEditActivity::class.java
                                    ).putExtra("Login", "Login").addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                )
                            }

                            override fun onFailure(call: Call<NidUserInfoResponse>, t: Throwable) {
                                Snackbar.make(
                                    binding.root,
                                    "네이버 회원정보 로드 실패 : ${t.message}",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }

                        })
                }
            }
        })
    }

    val launcherActivity: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {
            if(it.resultCode == AppCompatActivity.RESULT_OK) (context as LoginActivity).finish()
        })
}
package com.cha.auctionapp.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentTransaction
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.databinding.ActivityLoginSnsBinding
import com.cha.auctionapp.fragments.LoginMainFragment
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

class SNSLoginActivity : AppCompatActivity() {

    private val binding:ActivityLoginSnsBinding by lazy { ActivityLoginSnsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
    }


    /*
    *
    *       초기화 작업
    *
    * */
    private fun init() {
        val keyHash:String = Utility.getKeyHash(this)
        Log.i("keyhash",keyHash)
        Log.i("kakaoLogin","릴리즈 모드 시작")
        // 내 위치 정보 제공에 대한 동적 퍼미션 요청
        if( checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
            // 퍼미션 요청 대행사 이용 - 계약 체결
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        binding.ivLoginGoogle.setOnClickListener { googleLogin() }
        binding.ivLoginKakao.setOnClickListener { kakaoLogin() }
        binding.ivLoginNaver.setOnClickListener { naverLogin() }
        binding.btnWithoutLogin.setOnClickListener { clickWithoutLogin() }
        binding.btnLogin.setOnClickListener { clickLogin() }
    }


    /*
    *
    *       내 위치 정보 퍼미션 받기
    *
    * */
    var permissionLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission(), object : ActivityResultCallback<Boolean> {
            override fun onActivityResult(result: Boolean?) {
                if(!result!!) {
                    Snackbar.make(
                        findViewById(android.R.id.content)!!,
                        "위치 정보 제공에 동의하지 않았습니다. 검색 기능이 제한됩니다.", Snackbar.LENGTH_SHORT).show()
                }
            }
        })



    /*
    *
    *       로그인 없이 둘러보기 기능
    *
    * */
    private fun clickWithoutLogin() {
        G.userAccount.email = "no email"
        G.userAccount.id = "-1"
        G.location = "공릉1동"
        G.nickName = "no nickname"
        G.profileImg = getURLForResource(R.drawable.default_profile)
        startActivity(Intent(this,MainActivity::class.java))
    }
    private fun getURLForResource(resId: Int): Uri {
        return Uri.parse("android.resource://" + (R::class.java.getPackage()?.getName()) + "/" + resId)
    }


    /*
    *
    *       이메일로 로그인
    *
    * */
    private fun clickLogin() = startActivity(Intent(this,EmailLoginActivity::class.java))



    /*
    *
    *       구글 로그인 기능
    *
    * */
    private fun googleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        var client: GoogleSignInClient = GoogleSignIn.getClient(this,gso)

        val signInIntent: Intent = client.signInIntent
        launcher.launch(signInIntent)
    }

    val launcher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()
        , ActivityResultCallback {

            var intent: Intent? = it.data
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(intent)
            val account: GoogleSignInAccount = task.result

            val pref: SharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE)
            var id: String = pref.getString("google","").toString()

            if(id.isBlank()){
                id = account.id ?: ""
                var email: String = account.email ?: ""
                G.userAccount = UserAccount(id,email)

                val editor: SharedPreferences.Editor = pref.edit()
                editor.putString("google",id)
                editor.apply()
                launcherActivity.launch(
                    Intent(this,MyProfileEditActivity::class.java).putExtra("Login","Login").addFlags(
                        Intent.FLAG_ACTIVITY_NO_HISTORY))
            }else{
                var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
                var userRef: CollectionReference = firestore.collection("user")

                userRef.document(id).get().addOnSuccessListener {
                    G.userAccount.email = it.get("email").toString()
                    G.userAccount.id = it.get("id").toString()
                    G.nickName = it.get("nickname").toString()
                    G.location = it.get("location").toString()

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()

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
        Log.i("kakaoLogin","로그인 시작")
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Snackbar.make(binding.root,"카카오 로그인 실패",Snackbar.LENGTH_SHORT)
            } else if (token != null) {
                Log.i("kakaoLogin","로그인 에러 없음")
                UserApiClient.instance.me { user, error ->
                    if(user != null){
                        val pref: SharedPreferences = getSharedPreferences("Data",
                            Context.MODE_PRIVATE)
                        var id: String = pref.getString("kakao","").toString()

                        if(id.isBlank()){
                            id = user.id.toString()
                            var email: String = user.kakaoAccount?.email ?: ""
                            G.userAccount = UserAccount(id,email)
                            val editor: SharedPreferences.Editor = pref.edit()
                            editor.putString("kakao",id)
                            editor.apply()

                            launcherActivity.launch(
                                Intent(this,MyProfileEditActivity::class.java).putExtra("Login","Login").addFlags(
                                    Intent.FLAG_ACTIVITY_NO_HISTORY))
                        }else{
                            var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
                            var userRef: CollectionReference = firestore.collection("user")

                            userRef.document(id).get().addOnSuccessListener {
                                G.userAccount.email = it.get("email").toString()
                                G.userAccount.id = it.get("id").toString()
                                G.nickName = it.get("nickname").toString()
                                G.location = it.get("location").toString()

                                startActivity(Intent(this, MainActivity::class.java))
                                finish()

                                return@addOnSuccessListener
                            }
                        }
                    }
                }
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Log.i("kakaoLogin","1")
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                } else if (token != null) {
                    Log.i("kakaoLogin","2")
                    UserApiClient.instance.loginWithKakaoTalk(this,callback = callback)
                }
            }
        } else {
            Log.i("kakaoLogin","3")
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }


    /*
    *
    *        네이버 로그인 기능
    *
    * */
    private fun naverLogin() {
        NaverIdLoginSDK.initialize(this, "6Sq_GSOmgCFqeqaJVNtm", "tTZx8fr_jR", "근방")

        NaverIdLoginSDK.authenticate(this,object : OAuthLoginCallback {
            override fun onError(errorCode: Int, message: String) {
                Snackbar.make(binding.root,"네이버 로그인 에러 : ${message}",Snackbar.LENGTH_SHORT).show()
            }

            override fun onFailure(httpStatus: Int, message: String) {
                Snackbar.make(binding.root,"네이버 로그인 실패 : ${message}",Snackbar.LENGTH_SHORT).show()
            }

            override fun onSuccess() {

                val pref: SharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE)
                var id: String = pref.getString("naver","").toString()

                if(!id.isBlank()) {
                    var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
                    var userRef: CollectionReference = firestore.collection("user")

                    userRef.document(id).get().addOnSuccessListener {
                        G.userAccount.email = it.get("email").toString()
                        G.userAccount.id = it.get("id").toString()
                        G.nickName = it.get("nickname").toString()
                        G.location = it.get("location").toString()

                        startActivity(Intent(this@SNSLoginActivity, MainActivity::class.java))
                        finish()

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
                                        this@SNSLoginActivity,
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
            if(it.resultCode == AppCompatActivity.RESULT_OK) finish()
        })
}
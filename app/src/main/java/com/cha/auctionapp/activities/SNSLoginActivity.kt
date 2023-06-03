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
        initial()
    }


    /*
    *
    *       초기화 작업
    *
    * */
    private fun initial() {
        val keyHash:String = Utility.getKeyHash(this)
        Log.i("keyhash",keyHash)
        Log.i("kakaoLogin","릴리즈 모드 시작")


        /*
        *        내 위치 정보 제공에 대한 동적 퍼미션 요청
        * */
        if( checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
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
        ActivityResultContracts.RequestPermission()
    ) { result ->
        if (!result!!) {
            Snackbar.make(
                findViewById(android.R.id.content)!!,
                "위치 정보 제공에 동의하지 않았습니다. 검색 기능이 제한됩니다.", Snackbar.LENGTH_SHORT
            ).show()
        }
    }


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
        G.profileImg = getUriForResource(R.drawable.default_profile)
        startActivity(Intent(this,MainActivity::class.java))
    }
    /*
    *       Resource ID -> Uri
    * */
    private fun getUriForResource(resId: Int): Uri {
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
        val client: GoogleSignInClient = GoogleSignIn.getClient(this,gso)
        val signInIntent: Intent = client.signInIntent
        launcher.launch(signInIntent)
    }

    val launcher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()
        , ActivityResultCallback {

            val intent: Intent? = it.data
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(intent)
            if(!task.isSuccessful) return@ActivityResultCallback
            val account: GoogleSignInAccount = task.result

            val pref: SharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE)
            val id: String = pref.getString("google","").toString()
            if(id.isBlank()){
                saveUserData(account.id ?: "",account.email ?: "","google")
                launchingActivity()
            }
            else getUserInfoFromFirebase(id)
        })




    /*
    *
    *       카카오 로그인 기능
    *
    * */
    private fun kakaoLogin() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) Snackbar.make(binding.root,"카카오 로그인 실패",Snackbar.LENGTH_SHORT).show()
            else if (token != null) {
                UserApiClient.instance.me { user, error ->
                    if(user != null){
                        val pref: SharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE)
                        val id: String = pref.getString("kakao","").toString()
                        if(id.isBlank()){
                            saveUserData(user.id.toString(),user.kakaoAccount?.email ?: "","kakao")
                            launchingActivity()
                        }
                        else getUserInfoFromFirebase(id)
                    }
                }
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                else if (token != null) UserApiClient.instance.loginWithKakaoTalk(this,callback = callback)
            }
        }
        else UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
    }


    /*
    *
    *        네이버 로그인 기능
    *
    * */
    private fun naverLogin() {
        NaverIdLoginSDK.initialize(this, "6Sq_GSOmgCFqeqaJVNtm", "tTZx8fr_jR", "밍글")
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
                if(id.isNotBlank()) getUserInfoFromFirebase(id)
                else {
                    val accessToken: String? = NaverIdLoginSDK.getAccessToken()
                    val retrofit = RetrofitHelper.getRetrofitInstance("https://openapi.naver.com")
                    retrofit.create(RetrofitService::class.java)
                        .getNaverUserInfo("Bearer $accessToken")
                        .enqueue(object : Callback<NidUserInfoResponse> {
                            override fun onResponse(
                                call: Call<NidUserInfoResponse>,
                                response: Response<NidUserInfoResponse>
                            ) {
                                val userInfo = response.body()?.response
                                saveUserData(userInfo?.id ?: ""
                                    ,userInfo?.email ?: ""
                                    ,"naver")
                                launchingActivity()
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



    /*
    *
    *       Firebase 에 저장된 유저 정보 가져와서 G 클래스 멤버변수에 담아두기
    *
    * */
    private fun getUserInfoFromFirebase(id: String){
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

    /*
    *
    *       유저 정보 SharedPreference 에 저장
    *
    * */
    private fun saveUserData(id: String, email: String, snsType: String){
        val pref: SharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE)
        G.userAccount = UserAccount(id,email)
        val editor: SharedPreferences.Editor = pref.edit()
        editor.putString(snsType,id)
        editor.apply()
    }


    /*
    *
    *       MyProfileEditActivity 로 이동
    *
    * */
    private fun launchingActivity(){
        launcherActivity.launch(
            Intent(this@SNSLoginActivity,MyProfileEditActivity::class.java)
                .putExtra("Login","Login")
                .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY))
    }

}
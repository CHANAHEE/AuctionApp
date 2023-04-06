package com.example.auctionapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnKeyListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.auctionapp.G
import com.example.auctionapp.R
import com.example.auctionapp.activities.LoginActivity
import com.example.auctionapp.activities.MainActivity
import com.example.auctionapp.databinding.FragmentLoginMainBinding
import com.example.auctionapp.model.UserAccount
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore


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

            var id: String = account.id ?: ""
            var email: String = account.email ?: ""
            G.userAccount = UserAccount(id, email)

            startActivity(Intent(requireContext(),MainActivity::class.java))
        })

    private fun kakaoLogin() {

    }

    private fun naverLogin() {

    }
}
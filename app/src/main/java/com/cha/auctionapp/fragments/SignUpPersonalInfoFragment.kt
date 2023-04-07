package com.cha.auctionapp.fragments

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
import com.cha.auctionapp.R
import com.cha.auctionapp.databinding.FragmentSignUpPersonalInfoBinding
import com.google.android.material.snackbar.Snackbar

class SignUpPersonalInfoFragment : Fragment() {

    val binding by lazy { FragmentSignUpPersonalInfoBinding.inflate(layoutInflater) }
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
        binding.btnBack.setOnClickListener { clickBackBtn() }
        binding.btnNext.setOnClickListener { clickNextBtn() }
    }

    private fun clickBackBtn(){
        val fragment = activity?.supportFragmentManager
        fragment?.popBackStack()
    }


    private fun editTextListener(){
        binding.etBirth.setOnKeyListener { v , keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_ENTER)
            {
                val imm : InputMethodManager = context?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.etBirth.windowToken,0)
                true
            }

            false
        }
    }
    private fun clickNextBtn(){

        if(binding.etName.text.toString()== "") {
            Snackbar.make(
                activity?.findViewById(android.R.id.content)!!,
                "이름을 입력해주세요.", Snackbar.LENGTH_SHORT).show()

            binding.etName.requestFocus()
            return
        } else if(binding.etBirth.text.toString() == "") {
            Snackbar.make(
                activity?.findViewById(android.R.id.content)!!,
                "생년월일을 입력해주세요.", Snackbar.LENGTH_SHORT).show()

            binding.etBirth.requestFocus()
            return
        }

        var fragment = SignUpSetUpPlaceFragment()
        var bundle = Bundle()
        bundle.putString("name",binding.etName.text.toString())
        bundle.putString("birth",binding.etBirth.text.toString())
        bundle.putString("email",arguments?.getString("email"))
        bundle.putString("password",arguments?.getString("password"))
        fragment.arguments = bundle
        val tran:FragmentTransaction? =
            activity?.
            supportFragmentManager?.
            beginTransaction()?.
            replace(R.id.container_fragment,fragment)?.addToBackStack(null)

        tran?.commit()
    }

}
package com.example.auctionapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.auctionapp.R
import com.example.auctionapp.activities.LoginActivity
import com.example.auctionapp.databinding.FragmentSignUpEmailInputBinding
import com.google.android.material.snackbar.Snackbar

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

    private fun clickNextBtn(){
        val tran:FragmentTransaction? =
            activity?.
            supportFragmentManager?.
            beginTransaction()?.
            replace(R.id.container_fragment,SignUpPersonalInfoFragment())?.addToBackStack(null)
        tran?.commit()

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
}
package com.example.auctionapp.fragments

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
import com.example.auctionapp.databinding.FragmentSignUpPersonalInfoBinding

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

    private fun clickNextBtn(){
        val tran: FragmentTransaction? =
            activity?.
            supportFragmentManager?.
            beginTransaction()?.
            replace(R.id.container_fragment,SignUpSetUpPlaceFragment())?.addToBackStack(null)
        tran?.commit()
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

}
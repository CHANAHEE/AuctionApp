package com.example.actionapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import com.example.actionapp.R
import com.example.actionapp.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    var binding: FragmentHomeBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_option,menu)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        // Inflate the layout for this fragment
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.toolbar?.inflateMenu(R.menu.home_option)
        binding?.toolbar?.setNavigationIcon(R.drawable.chat)

        binding?.toolbar?.setNavigationOnClickListener {
            //Toast.makeText(context  , "Hello", Toast.LENGTH_SHORT).show()


            /*
            *
            * 팝업 메뉴 처리하기
            *
            * */
        }
    }

}
package com.cha.auctionapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.cha.auctionapp.G
import com.cha.auctionapp.R
import com.cha.auctionapp.activities.CommunityEditActivity
import com.cha.auctionapp.activities.MainActivity
import com.cha.auctionapp.activities.SetUpMyPlaceActivity
import com.cha.auctionapp.activities.SetUpMyPlaceListActivity
import com.cha.auctionapp.adapters.CommunityAdapter
import com.cha.auctionapp.adapters.MyPostListAdapter
import com.cha.auctionapp.adapters.ProductAdapter
import com.cha.auctionapp.databinding.FragmentCommunityBinding
import com.cha.auctionapp.model.CommunityPostItem
import com.cha.auctionapp.model.MainItem
import com.cha.auctionapp.network.RetrofitHelper
import com.cha.auctionapp.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommunityFragment : Fragment() {

    lateinit var binding: FragmentCommunityBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommunityBinding.inflate(LayoutInflater.from(context),container,false)
        return binding.root
    }

    lateinit var popupMenu:PopupMenu
    lateinit var communityItems: MutableList<CommunityPostItem>

    private val POPUP_MENU_MY_FIRST_PLACE_ITEM_ID :Int? = 0
    private val POPUP_MENU_SET_PLACE_ITEM_ID :Int? = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        communityItems = mutableListOf()
        binding.recycler.adapter = CommunityAdapter(requireContext(),communityItems)

        binding.fab.setOnClickListener{ startActivity(Intent(context,CommunityEditActivity::class.java)) }
        binding.ibSearch.setOnClickListener { clickSearch(it) }
        binding.btnSelectTown.setOnClickListener { clickMyPlace() }

        setUpPopupMenu()
    }

    override fun onResume() {
        super.onResume()
        loadDataFromServer()
    }

    /*
    *
    *       서버에서 커뮤니티 글 데이터 가져오기
    *
    * */
    private fun loadDataFromServer(){
        val retrofit = RetrofitHelper.getRetrofitInstance("http://tjdrjs0803.dothome.co.kr")
        val retrofitService = retrofit.create(RetrofitService::class.java)
        val call: Call<MutableList<CommunityPostItem>> = retrofitService.getDataFromServerForCommunityFragment(G.location)
        call.enqueue(object : Callback<MutableList<CommunityPostItem>> {
            override fun onResponse(
                call: Call<MutableList<CommunityPostItem>>,
                response: Response<MutableList<CommunityPostItem>>
            ) {
                communityItems = response.body()!!
                binding.recycler.adapter = CommunityAdapter(requireContext(),communityItems)
            }

            override fun onFailure(call: Call<MutableList<CommunityPostItem>>, t: Throwable) {
                Log.i("test01","${t.message}")
            }
        })
    }
    /*
    *
    *       팝업 메뉴 설정
    *
    * */
    private fun setUpPopupMenu(){
        popupMenu = PopupMenu(context,binding.btnSelectTown)
        popupMenu.menu.add(0, POPUP_MENU_MY_FIRST_PLACE_ITEM_ID!!,0, G.location)
        popupMenu.menu.add(0, POPUP_MENU_SET_PLACE_ITEM_ID!!,0,"내 동네 설정")
        binding.btnSelectTown.text = popupMenu
            .menu
            .getItem(POPUP_MENU_MY_FIRST_PLACE_ITEM_ID).toString()
    }

    /*
    *
    *       동네 설정 기능
    *
    * */
    private fun clickMyPlace(){

        activity?.menuInflater?.inflate(R.menu.popupmenu,popupMenu.menu)
        popupMenu.show()

        // 각 popupMenu 의 동네 이름 string 값이 아니고, id 값을 기준으로 분기문이 실행된다.
        // 그러니, 기본설정값 만들 때, 주의할것.
        popupMenu.setOnMenuItemClickListener {
            var id:Int = it.itemId
            when(id) {
                // G 클래스에 내 동네 데이터 넣는 코드 작성

                POPUP_MENU_MY_FIRST_PLACE_ITEM_ID -> {
                    binding.btnSelectTown.text = popupMenu
                        .menu
                        .getItem(POPUP_MENU_MY_FIRST_PLACE_ITEM_ID).toString()

                    var tran: FragmentTransaction? = activity?.supportFragmentManager
                        ?.beginTransaction()
                        ?.replace(R.id.container_fragment, CommunityFragment())
                    tran?.commit()
                }

                POPUP_MENU_SET_PLACE_ITEM_ID -> {
                    var intent = Intent(context, SetUpMyPlaceListActivity::class.java).putExtra("Community","Community")
                    placeLauncher.launch(intent)
                }
            }
            false
        }
    }

    /*
    *
    *       검색 창 클릭 이벤트
    *
    * */
    private fun clickSearch(it : View){
        if(it.isSelected){
            it.isSelected = false
            binding.etSearch.visibility = View.VISIBLE
            binding.tvTitle.visibility = View.GONE
            binding.btnSelectTown.visibility = View.GONE
        } else {
            it.isSelected = true
            binding.etSearch.visibility = View.GONE
            binding.tvTitle.visibility = View.VISIBLE
            binding.btnSelectTown.visibility = View.VISIBLE
        }
    }

    /*
    *
    *       SetUpMyPlaceListActivity Launcher : 새로운 동네 설정 시, MainActivity 종료
    *
    * */
    private val placeLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        when(it.resultCode){
            AppCompatActivity.RESULT_OK -> (context as MainActivity).finish()
        }
    }


}

package com.example.auctionapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.example.auctionapp.R
import com.example.auctionapp.activities.SetUpMyPlaceActivity
import com.example.auctionapp.adapters.MyPostListAdapter
import com.example.auctionapp.databinding.FragmentCommunityBinding
import com.example.auctionapp.model.CommunityPostItem

class CommunityFragment : Fragment() {

    lateinit var binding: FragmentCommunityBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCommunityBinding.inflate(LayoutInflater.from(context),container,false)
        return binding.root
    }

    lateinit var popupMenu:PopupMenu
    lateinit var communityItems: MutableList<CommunityPostItem>

    private val POPUP_MENU_MY_FIRST_PLACE_ITEM_ID :Int? = 0
    private val POPUP_MENU_MY_SECOND_PLACE_ITEM_ID :Int? = 1
    private val POPUP_MENU_SET_PLACE_ITEM_ID :Int? = 2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        popupMenu = PopupMenu(context,binding.btnSelectTown)
        popupMenu.menu.add(0, POPUP_MENU_MY_FIRST_PLACE_ITEM_ID!!,0,"공릉 1동")
        popupMenu.menu.add(0, POPUP_MENU_MY_SECOND_PLACE_ITEM_ID!!,0,"공릉 2동")
        popupMenu.menu.add(0, POPUP_MENU_SET_PLACE_ITEM_ID!!,0,"내 동네 설정")
        binding.btnSelectTown.text = popupMenu
            .menu
            .getItem(POPUP_MENU_MY_FIRST_PLACE_ITEM_ID).toString()



        communityItems = mutableListOf()

        communityItems.add(CommunityPostItem(R.drawable._0,"안녕하세요~~~","이번에 묵동 구길 쪽으로 이사를 가는데 집사님들 혹시 아시나요?","공릉 1동",10,5))
        communityItems.add(CommunityPostItem(R.drawable._1,"123456~~~","숫자놀이?","공릉 1동",5,8))
        communityItems.add(CommunityPostItem(R.drawable._2,"에베ㅔㅔㅔ~~~","이상한가요?","공릉 1동",1,2))
        communityItems.add(CommunityPostItem(R.drawable._3,"저녁에 같이 산책해요~~~같이 산책하면 좋을것같아요! 장소는 내일 알려드려요!!","최근에 날씨도 좋은데 산책 어때요?","공릉 1동",0,0))
        communityItems.add(CommunityPostItem(R.drawable._4,"노원 공릉 헬스장 추천~~~","헬스에 빠지게되었습니다","공릉 1동",10,12))
        communityItems.add(CommunityPostItem(R.drawable._5,"애플워치 잃어버렸어요 ㅠㅠ","어제(3/31) 에 애플워치를 잃어버렸어요","공릉 1동",22,23))
        communityItems.add(CommunityPostItem(R.drawable._6,"말티즈를 찾고있습니다.","최근에 강아지를 잃어버렸어요 위치는 저도 잘 모르겠습니다. 꼭 좀 연락 부탁드려요.","공릉 1동",0,1))
        communityItems.add(CommunityPostItem(R.drawable._7,"저와 식재료를 반띵하실 분을 찾습니다.","혼자 사는 자취생입니다","공릉 1동",55,102))
        communityItems.add(CommunityPostItem(R.drawable._8,"토요 산책","산책해요!! ","공릉 1동",1,2))
        communityItems.add(CommunityPostItem(R.drawable._9,"진삼 독서모임","독서모임 입니다.","공릉 1동",1,8))

        binding.recycler.adapter = MyPostListAdapter(requireContext(),communityItems)

        binding.btnSelectTown.setOnClickListener {
            activity?.menuInflater?.inflate(R.menu.popupmenu,popupMenu.menu)
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener {
                if(it.itemId == POPUP_MENU_MY_FIRST_PLACE_ITEM_ID){
                    binding.btnSelectTown.text = popupMenu
                        .menu
                        .getItem(POPUP_MENU_MY_FIRST_PLACE_ITEM_ID).toString()

                    communityItems.clear()
                    communityItems.add(CommunityPostItem(R.drawable._0,"안녕하세요~~~","이번에 묵동 구길 쪽으로 이사를 가는데 집사님들 혹시 아시나요?","공릉 1동",10,5))
                    communityItems.add(CommunityPostItem(R.drawable._1,"123456~~~","숫자놀이?","공릉 1동",5,8))
                    communityItems.add(CommunityPostItem(R.drawable._2,"에베ㅔㅔㅔ~~~","이상한가요?","공릉 1동",1,2))
                    communityItems.add(CommunityPostItem(R.drawable._3,"저녁에 같이 산책해요~~~같이 산책하면 좋을것같아요! 장소는 내일 알려드려요!!","최근에 날씨도 좋은데 산책 어때요?","공릉 1동",0,0))
                    communityItems.add(CommunityPostItem(R.drawable._4,"노원 공릉 헬스장 추천~~~","헬스에 빠지게되었습니다","공릉 1동",10,12))
                    communityItems.add(CommunityPostItem(R.drawable._5,"애플워치 잃어버렸어요 ㅠㅠ","어제(3/31) 에 애플워치를 잃어버렸어요","공릉 1동",22,23))
                    communityItems.add(CommunityPostItem(R.drawable._6,"말티즈를 찾고있습니다.","최근에 강아지를 잃어버렸어요 위치는 저도 잘 모르겠습니다. 꼭 좀 연락 부탁드려요.","공릉 1동",0,1))
                    communityItems.add(CommunityPostItem(R.drawable._7,"저와 식재료를 반띵하실 분을 찾습니다.","혼자 사는 자취생입니다","공릉 1동",55,102))
                    communityItems.add(CommunityPostItem(R.drawable._8,"토요 산책","산책해요!! ","공릉 1동",1,2))
                    communityItems.add(CommunityPostItem(R.drawable._9,"진삼 독서모임","독서모임 입니다.","공릉 1동",1,8))

                    binding.recycler.adapter?.notifyDataSetChanged()
                }else if(it.itemId == POPUP_MENU_MY_SECOND_PLACE_ITEM_ID){
                    binding.btnSelectTown.text = popupMenu
                        .menu
                        .getItem(POPUP_MENU_MY_SECOND_PLACE_ITEM_ID).toString()

                    communityItems.clear()
                    communityItems.add(CommunityPostItem(R.drawable._9,"안녕하세요~~~","이번에 묵동 구길 쪽으로 이사를 가는데 집사님들 혹시 아시나요?","공릉 2동",10,5))
                    communityItems.add(CommunityPostItem(R.drawable._8,"123456~~~","숫자놀이?","공릉 2동",5,8))
                    communityItems.add(CommunityPostItem(R.drawable._7,"에베ㅔㅔㅔ~~~","이상한가요?","공릉 2동",1,2))
                    communityItems.add(CommunityPostItem(R.drawable._4,"애플워치 잃어버렸어요 ㅠㅠ","어제(3/31) 에 애플워치를 잃어버렸어요","공릉 2동",22,23))
                    communityItems.add(CommunityPostItem(R.drawable._3,"말티즈를 찾고있습니다.","최근에 강아지를 잃어버렸어요 위치는 저도 잘 모르겠습니다. 꼭 좀 연락 부탁드려요.","공릉 2동",0,1))
                    communityItems.add(CommunityPostItem(R.drawable._2,"저와 식재료를 반띵하실 분을 찾습니다.","혼자 사는 자취생입니다","공릉 2동",55,102))
                    communityItems.add(CommunityPostItem(R.drawable._1,"토요 산책","산책해요!! ","공릉 2동",1,2))
                    communityItems.add(CommunityPostItem(R.drawable._0,"진삼 독서모임","독서모임 입니다.","공릉 2동",1,8))
                    communityItems.add(CommunityPostItem(R.drawable._6,"저녁에 같이 산책해요~~~같이 산책하면 좋을것같아요! 장소는 내일 알려드려요!!","최근에 날씨도 좋은데 산책 어때요?","공릉 2동",0,0))
                    communityItems.add(CommunityPostItem(R.drawable._5,"노원 공릉 헬스장 추천~~~","헬스에 빠지게되었습니다","공릉 2동",10,12))
                    binding.recycler.adapter?.notifyDataSetChanged()
                }else if(it.itemId == POPUP_MENU_SET_PLACE_ITEM_ID){
                    startActivity(Intent(context, SetUpMyPlaceActivity::class.java))
                }
                false
            }

        }




    }


}
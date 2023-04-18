package com.cha.auctionapp.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.cha.auctionapp.G
import com.cha.auctionapp.fragments.HomeFragment
import com.cha.auctionapp.R
import com.cha.auctionapp.databinding.ActivityMainBinding
import com.cha.auctionapp.fragments.AuctionFragment
import com.cha.auctionapp.fragments.ChatFragment
import com.cha.auctionapp.fragments.CommunityFragment
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {

    val binding:ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    lateinit var popupMenu:PopupMenu

    private val POPUP_MENU_MY_FIRST_PLACE_ITEM_ID :Int = 0
    private val POPUP_MENU_SET_PLACE_ITEM_ID :Int = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Log.i("daniboeb","확인용")
        init()
    }


    private fun init() {
        getProfileURLFromFirestore(G.userAccount.id)

        HomeFragment()
        CommunityFragment()
        AuctionFragment()
        ChatFragment()

        // 팝업메뉴 만들어 놓기. 그래서 처음 설정값을 정해두기
        setUpPopUpMenu()

        // 프래그먼트 초기값 설정
        setUpFragment()

        binding.bnv.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener {
            var tran = supportFragmentManager.beginTransaction()
            changeFragment(it,tran)
            return@OnItemSelectedListener true
        })


        binding.btnSelectTown.setOnClickListener { clickMyPlace() }
        binding.ibSearch.setOnClickListener { clickEditSearch() }
        binding.ibCategory.setOnClickListener { clickCategoryBtn() }


    }
    /*
    *
    *       전역으로 쓰일 프로필 다운로드 URL 받아오기
    *
    * */
    private fun getProfileURLFromFirestore(id: String){
        val firebaseStorage = FirebaseStorage.getInstance()
        val rootRef = firebaseStorage.reference

        val imgRef = rootRef.child("profile/IMG_$id.jpg")
        imgRef.downloadUrl.addOnSuccessListener { p0 ->
            G.profileImg = p0
            Log.i("15eeee","$p0")
            setNavigationDrawer()
        }.addOnFailureListener {
        }
    }

    /*
    *
    *       초기 Fragment 설정
    *
    * */
    private fun setUpFragment(){
        var tran = supportFragmentManager
        if(intent.getStringExtra("Community") == "Community"){
            tran.beginTransaction().replace(R.id.container_fragment,CommunityFragment()).commit()
            binding.appbar.visibility = View.GONE
            binding.bnv.selectedItemId = R.id.community_tab
            return
        }
        tran.beginTransaction()
            .add(R.id.container_fragment,HomeFragment().apply {
                arguments = Bundle().apply {
                    putString("place",this@MainActivity.binding.btnSelectTown.text.toString())
                }
            }).commit()
    }


    /*
    *
    *       팝업메뉴 설정
    *
    * */
    private fun setUpPopUpMenu(){
        popupMenu = PopupMenu(this,binding.btnSelectTown)
        popupMenu.menu.add(0, POPUP_MENU_MY_FIRST_PLACE_ITEM_ID!!,0,G.location)
        popupMenu.menu.add(0, POPUP_MENU_SET_PLACE_ITEM_ID!!,0,"내 동네 설정")
        binding.btnSelectTown.text = popupMenu
            .menu
            .getItem(POPUP_MENU_MY_FIRST_PLACE_ITEM_ID).toString()
    }


    /*
    *
    *       카테고리 버튼 클릭
    *
    * */
    private fun clickCategoryBtn() {
        startActivity(Intent(this, SelectCategoryActivity::class.java))

    }


    /*
    *
    *
    *       검색 EditText 설정
    *
    */
    private fun clickEditSearch() {
        if(binding.etSearch.visibility == View.INVISIBLE){
            binding.btnSelectTown.visibility = View.INVISIBLE
            binding.etSearch.visibility = View.VISIBLE

            binding.etSearch.setOnFocusChangeListener { v, hasFocus ->
                if(!hasFocus){
                    val imm: InputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)

                    binding.btnSelectTown.visibility = View.VISIBLE
                    binding.etSearch.visibility = View.INVISIBLE
                    binding.etSearch.setText("")
                }
            }


            binding.etSearch.setOnKeyListener { v , keyCode, event ->
                if(event.action == KeyEvent.ACTION_DOWN
                    && keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    val imm : InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.etSearch.windowToken,0)
                    binding.containerFragment.requestFocus()
                    /*
                    *
                    *       검색 결과 처리 작업
                    *
                    * */
                    true
                }

                false
            }
        } else {
            binding.btnSelectTown.visibility = View.VISIBLE
            binding.etSearch.visibility = View.INVISIBLE
            binding.etSearch.setText("")
        }
    }





    /*
    *
    *       동네 설정
    *
    * */
    private fun clickMyPlace(){
        menuInflater.inflate(R.menu.popupmenu,popupMenu.menu)
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

                    var tran: FragmentTransaction = supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container_fragment, HomeFragment().apply {

                            arguments = Bundle().apply {
                                putString(
                                    "place",
                                    this@MainActivity.binding.btnSelectTown.text.toString()
                                )
                            }
                        })
                    tran.commit()
                }

                POPUP_MENU_SET_PLACE_ITEM_ID -> {
                    var intent = Intent(this, SetUpMyPlaceListActivity::class.java)
                    placeLauncher.launch(intent)
                }

            }
            false
        }
    }


    /*
    *
    *       SetUpMyPlaceListActivity Launcher : 새로운 동네 설정 시, MainActivity 종료
    *
    * */
    private val placeLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        when(it.resultCode){
            RESULT_OK->finish()
        }
    }


    /*
    *
    *       Set NavigationDrawer
    *
    * */
    private fun setNavigationDrawer() {
        setSupportActionBar(binding.toolbar)
        var drawerToggle:ActionBarDrawerToggle = ActionBarDrawerToggle(this,binding.drawerLayout,binding.toolbar,R.string.open,R.string.close)

        var actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        drawerToggle.syncState()
        actionBar?.title = null
        binding.drawerLayout.addDrawerListener(drawerToggle)

        binding.nav.getHeaderView(0).findViewById<TextView>(R.id.tv_nav_email).text = G.userAccount?.email ?: "no email"
        binding.nav.getHeaderView(0).findViewById<TextView>(R.id.tv_nav_nickname).text = G.nickName
        val profile = binding.nav.getHeaderView(0).findViewById<CircleImageView>(R.id.iv_nav_profile)

        Glide.with(this).load(G.profileImg).error(R.drawable.default_profile).into(profile)

        binding.nav.getHeaderView(0).findViewById<View>(R.id.btn_edit_profile).setOnClickListener {
            when(it.id){
                R.id.btn_edit_profile->{
                    var intent = Intent(this,MyProfileEditActivity::class.java)
                    profileLauncher.launch(intent)
                }
            }
        }

        binding.nav.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.menu_my_fav_list->{
                    startActivity(Intent(this,MyFavoriteListActivity::class.java))
                }
                R.id.menu_my_post_list->{
                    startActivity(Intent(this,MyPostListActivity::class.java))
                }
            }
            binding.drawerLayout.closeDrawer(binding.nav)
            false
        }
    }


    /*
    *
    *       프로필 변경 런처
    *
    * */
    private val profileLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()
    ) {
        when(it.resultCode){
            RESULT_OK->{
                var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
                var userRef: CollectionReference = firestore.collection("user")

                userRef.document(G.userAccount.id).update("nickname",G.nickName,"profileImage",G.profileImg)

                binding.nav.getHeaderView(0).findViewById<TextView>(R.id.tv_nav_nickname).text = G.nickName
                Glide.with(this).load(G.profileImg).into(binding.nav.getHeaderView(0).findViewById<CircleImageView>(R.id.iv_nav_profile))
            }
        }
    }


    /*
    *       프래그먼트 전환 함수
    * */
    private fun changeFragment(item:MenuItem,tran:FragmentTransaction){

        when(item.itemId){
            R.id.home_tab -> {
                binding.appbar.visibility = View.VISIBLE
                tran.replace(R.id.container_fragment, HomeFragment()).commit()
            }
            R.id.community_tab -> {
                binding.appbar.visibility = View.GONE
                tran.replace(R.id.container_fragment, CommunityFragment()).commit()
            }
            R.id.auction_tab -> {
                binding.appbar.visibility = View.GONE
                tran.replace(R.id.container_fragment, AuctionFragment()).commit()
            }
            R.id.chat_tab -> {
                binding.appbar.visibility = View.GONE
                tran.replace(R.id.container_fragment, ChatFragment()).commit()
            }
        }
    }
}
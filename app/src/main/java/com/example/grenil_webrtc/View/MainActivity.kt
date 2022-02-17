package com.example.grenil_webrtc.View

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.grenil_webrtc.Adapter.ViewPagerAdapter
import com.example.grenil_webrtc.R
import com.example.grenil_webrtc.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    var fragmentList = listOf(VoiceFragment(), GroupFragment())

    //퍼미션 응답 처리 코드
    private val multiplePermissionsCode = 100

    //필요한 퍼미션 리스트
    //원하는 퍼미션을 이곳에 추가
    //<uses-permission android:name="android.permission.CALL_PHONE" />
    //<uses-permission android:name="android.permission.ANSWER_PHONE_CALLS" />
    //<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    //<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    val requiredPermissions = arrayOf(
        Manifest.permission.CALL_PHONE,
        Manifest.permission.ANSWER_PHONE_CALLS,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.SYSTEM_ALERT_WINDOW)

    //바인딩
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //권한 설정
        checkPermissions()


        //탭레이아웃
//        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                // 탭이 선택 되었을 때
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//                // 탭이 선택되지 않은 상태로 변경 되었을 때
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//                // 이미 선택된 탭이 다시 선택 되었을 때
//            }
//        })
//
//        // 뷰페이저에 어댑터 연결
//        binding.viewPager.adapter = ViewPagerAdapter(this)
//
//
//        /* 탭과 뷰페이저를 연결, 여기서 새로운 탭을 다시 만드므로 레이아웃에서 꾸미지말고
//        여기서 꾸며야함
//         */
//        TabLayoutMediator(binding.tabLayout, binding.viewPager) {tab, position ->
//            when(position) {
//                0 -> tab.text = "탭1"
//                1 -> tab.text = "탭2"
//
//            }
//        }.attach()

        //1. fragmentStateAdapter 초기화
        val pagerAdapter = ViewPagerAdapter(this)
            .apply {
                addFragment(VoiceFragment())
                addFragment(GroupFragment())
            }
        //2. viewpager2의 adapter 설정
        val viewPager : ViewPager2 = binding.viewPager.apply {
            adapter = pagerAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    Log.d("ViewPagerFragment","Page ${position+1}")
                }

            })
        }
        //3. TabLayout 과 ViewPager 연결
        TabLayoutMediator(binding.tabLayout, viewPager) {tab,position ->
            when(position) {
                0 -> tab.text = "1:1 VoiceChat"
                1 -> tab.text = "Group VoiceChat"

            }
        }.attach()

    }

    private fun checkPermissions() {
        var rejectedPermissionList = ArrayList<String>()

        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
        for(permission in requiredPermissions){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                //만약 권한이 없다면 rejectedPermissionList에 추가
                rejectedPermissionList.add(permission)
            }
        }
        //거절된 퍼미션이 있다면...
        if(rejectedPermissionList.isNotEmpty()){
            //권한 요청!
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(this, rejectedPermissionList.toArray(array), multiplePermissionsCode)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            multiplePermissionsCode -> {
                if(grantResults.isNotEmpty()) {
                    for((i, permission) in permissions.withIndex()) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            //권한 획득 실패
                            Log.i("TAG", "The user has denied to $permission")
                            Log.i("TAG", "I can't work for you anymore then. ByeBye!")

                            //finish()!!!!!!!!!!!?????


                        }
                    }
                }
            }
        }
    }
}
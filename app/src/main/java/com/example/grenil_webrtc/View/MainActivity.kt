package com.example.grenil_webrtc.View

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.grenil_webrtc.Adapter.ViewPagerAdapter
import com.example.grenil_webrtc.GroupChat.GroupFragment
import com.example.grenil_webrtc.R
import com.example.grenil_webrtc.VoiceChat.VoiceFragment
import com.example.grenil_webrtc.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.sothree.slidinguppanel.SlidingUpPanelLayout

class MainActivity : AppCompatActivity() {
    var fragmentList = listOf(VoiceFragment(), GroupFragment())

    //퍼미션 응답 처리 코드
    private val multiplePermissionsCode = 100

    //거부된 퍼미션 리스트
    var rejectedPermissionList = ArrayList<String>()
    //필요한 퍼미션 리스트
    val requiredPermissions = arrayOf(
        Manifest.permission.CALL_PHONE,
        Manifest.permission.ANSWER_PHONE_CALLS,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.SYSTEM_ALERT_WINDOW)

    companion object {
        private const val CAMERA_AUDIO_PERMISSION_REQUEST_CODE = 1
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        private const val AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO
        //private const val LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION
    }

    //바인딩
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //권한 설정
        //checkPermissions()


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

        //sliding pannel
        val slidePanel = binding.mainFrame                      // SlidingUpPanel
        //slidePanel.addPanelSlideListener(PanelEventListener())

        //다른 영역 터치 불가
        slidePanel.isTouchEnabled = false

        //처음부터 나왔고 toggle 버튼 클릭하면 사라지기
        binding.btnToggle.setOnClickListener {
            slidePanel.panelState =SlidingUpPanelLayout.PanelState.HIDDEN
            checkPermissions()
            //val state = slidePanel.panelState
            // 닫힌 상태일 경우 열기
//            if (state == SlidingUpPanelLayout.PanelState.COLLAPSED) {
//                slidePanel.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
//            }
//            // 열린 상태일 경우 닫기
//            else if (state == SlidingUpPanelLayout.PanelState.EXPANDED) {
//                slidePanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
//            }
        }
        //알람
        binding.permissionAlarm.setOnClickListener {
            rejectedPermissionList.add(CAMERA_PERMISSION)
        }

        //카메라
        binding.permissionCamera.setOnClickListener {
            rejectedPermissionList.add(CAMERA_PERMISSION)
        }

        //마이크
        binding.permissionMike.setOnClickListener {
            rejectedPermissionList.add(AUDIO_PERMISSION)

        }

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
//    inner class PanelEventListener : SlidingUpPanelLayout.PanelSlideListener {
//        override fun onPanelSlide(panel: View, slideOffset: Float) {
//            Log.i("offset:",slideOffset.toString())
//        }
//
//        override fun onPanelStateChanged(
//            panel: View?,
//            previousState: SlidingUpPanelLayout.PanelState?,
//            newState: SlidingUpPanelLayout.PanelState?
//        ) {
//            if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
//                binding.btnToggle.setText("열기")
//            } else if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
//                binding.btnToggle.setText("닫기")
//            }
//        }
//
//
//    }
}

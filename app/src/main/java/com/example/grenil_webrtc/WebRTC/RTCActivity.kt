package com.example.grenil_webrtc.WebRTC

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.developerspace.webrtcsample.RTCAudioManager
import com.developerspace.webrtcsample.RTCClient
import com.developerspace.webrtcsample.SignalingClient
import com.example.grenil_webrtc.R
import org.webrtc.SessionDescription

class RTCActivity : AppCompatActivity() {

    private lateinit var rtcClient: RTCClient
    private lateinit var signallingClient: SignalingClient

    private val audioManager by lazy { RTCAudioManager.create(this) }

    val TAG = "MainActivity"

    private var meetingID : String = "test-call"

    private var isJoin = false

    private var myID = ""

    private var isMute = false

    private var isVideoPaused = false

    private var inSpeakerMode = true

    private val sdpObserver = object : AppSdpObserver() {
        override fun onCreateSuccess(p0: SessionDescription?) {
            super.onCreateSuccess(p0)
            //signallingClient.send(p0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
    }

}
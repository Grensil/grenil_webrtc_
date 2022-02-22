package com.example.grenil_webrtc.VoiceChat

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.recyclerview.widget.LinearLayoutManager
import com.developerspace.webrtcsample.RTCClient
import com.developerspace.webrtcsample.SignalingClient
import com.developerspace.webrtcsample.SignalingClientListener
import com.example.grenil_webrtc.Adapter.PersonAdapter
import com.example.grenil_webrtc.R
import com.example.grenil_webrtc.View.MainActivity
import com.example.grenil_webrtc.WebRTC.Constants
import com.example.grenil_webrtc.WebRTC.PeerConnectionObserver
import com.example.grenil_webrtc.WebRTC.RTCActivity
import com.example.grenil_webrtc.databinding.FragmentVoiceBinding
import kotlinx.android.synthetic.main.activity_call.*
import org.webrtc.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [VoiceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VoiceFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private var binding : FragmentVoiceBinding? = null
    lateinit var myadapter : PersonAdapter

    //이 어플을 현재 사용하고 있는 사용자 list 얻어오기 -> firebase or aws
    var peers: List<String> = listOf("first peer","second peer","third peer")

    val TAG = "VoiceFragment"

    private lateinit var rtcClient: RTCClient
    private lateinit var signallingClient: SignalingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_voice,container,false)
        val recyclerView = binding!!.recyclerview
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        myadapter = PersonAdapter(requireContext(),peers)
        myadapter.setPeerClickListener(PersonClickListener())
        recyclerView.adapter = myadapter

        //처음부터 firebase 및 candidate offerCandidate로 등록시켜야함..
        val intent = Intent(requireContext(), RTCActivity::class.java)
        val meeting_id = "my_id" //peername 으로 방을 만들자
        val isJoin = false // false면 방장 , true 면 참가자



        intent.putExtra("meetingID",meeting_id)
        intent.putExtra("isJoin",isJoin)

        //나의 연결에 응답이 있을시에!
        //startActivity(intent)

        AddRoomAndWait() //방만들고(candidate 및 파이어베이스) 기다리기
        return binding!!.root
    }

    //접근 권한부여 -> IceCandidate 추가
    private fun AddRoomAndWait() {
        rtcClient = RTCClient(
            requireContext() as Application,
            object : PeerConnectionObserver() {
                override fun onIceCandidate(p0: IceCandidate?) {
                    super.onIceCandidate(p0)
                    signallingClient.sendIceCandidate(p0, false)
                    rtcClient.addIceCandidate(p0)
                }

                override fun onAddStream(p0: MediaStream?) {
                    super.onAddStream(p0)
                    Log.e(TAG, "onAddStream: $p0")
                    p0?.videoTracks?.get(0)?.addSink(remote_view)
                }

                override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
                    Log.e(TAG, "onIceConnectionChange: $p0")
                }

                override fun onIceConnectionReceivingChange(p0: Boolean) {
                    Log.e(TAG, "onIceConnectionReceivingChange: $p0")
                }

                override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                    Log.e(TAG, "onConnectionChange: $newState")
                }

                override fun onDataChannel(p0: DataChannel?) {
                    Log.e(TAG, "onDataChannel: $p0")
                }

                override fun onStandardizedIceConnectionChange(newState: PeerConnection.IceConnectionState?) {
                    Log.e(TAG, "onStandardizedIceConnectionChange: $newState")
                }

                override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {
                    Log.e(TAG, "onAddTrack: $p0 \n $p1")
                }

                override fun onTrack(transceiver: RtpTransceiver?) {
                    Log.e(TAG, "onTrack: $transceiver" )
                }
            }
        )

        //내 화면 및 상대 화면 공간 초기화 및 비디오 출력 담당
        rtcClient.initSurfaceView(remote_view)
        rtcClient.initSurfaceView(local_view)
        rtcClient.startLocalVideoCapture(local_view)


        //시그널링 리스너 (방장일때도 포함되어야함)
        signallingClient =  SignalingClient("meetingID",createSignallingClientListener())
        if (!isJoin) // 방장일 때
            rtcClient.call(sdpObserver,meetingID)
    }


    //signallingclient 리스너
    private fun createSignallingClientListener() = object : SignalingClientListener {
        override fun onConnectionEstablished() {
            end_call_button.isClickable = true
        }

        //client 가 수신을 제안한 경우 호출
        override fun onOfferReceived(description: SessionDescription) {
            rtcClient.onRemoteSessionReceived(description)
            Constants.isIntiatedNow = false
            rtcClient.answer(sdpObserver,meetingID)
            remote_view_loading.isGone = true
        }

        //client 가 수신 답변을 한 경우 호출
        override fun onAnswerReceived(description: SessionDescription) {
            rtcClient.onRemoteSessionReceived(description)
            Constants.isIntiatedNow = false
            remote_view_loading.isGone = true
        }

        //end-call한 경우 호출
        override fun onIceCandidateReceived(iceCandidate: IceCandidate) {
            rtcClient.addIceCandidate(iceCandidate)
        }

        override fun onCallEnded() {
            if (!Constants.isCallEnded) {
                Constants.isCallEnded = true
                rtcClient.endCall(meetingID)
                finish()
                startActivity(Intent(this@RTCActivity, MainActivity::class.java))
            }
        }
    }



    inner class PersonClickListener : PersonAdapter.onPeerClickListener {
        override fun onPeerClick(peerName: String) {
            //peer 아이템 클릭 이벤트
            //Log.i("peer","Hi, I'm $peerName")
            //현재 아이템(peer 정보/ meeting ID) 클릭한것을 RTCActivity로 넘겨주자
            val intent = Intent(requireContext(), RTCActivity::class.java)
            val meeting_id = "my_id" //만든 meeting id 로 접근
            val isJoin = true // false면 방장 , true 면 참가자



            intent.putExtra("meetingID",meeting_id)
            intent.putExtra("isJoin",isJoin)
            startActivity(intent)
        }

    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment VoicdeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VoiceFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
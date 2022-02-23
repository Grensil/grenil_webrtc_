package com.example.grenil_webrtc.GroupChat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.developerspace.webrtcsample.RTCClient
import com.developerspace.webrtcsample.SignalingClient
import com.example.grenil_webrtc.Adapter.PersonAdapter
import com.example.grenil_webrtc.R
import com.example.grenil_webrtc.databinding.FragmentGroupBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GroupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GroupFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var binding : FragmentGroupBinding? = null
    lateinit var myadapter : PersonAdapter

    var peers: List<String> = listOf("first group","second group","third group")

    private var meetingID : String = "test-groupcall"
    //나에 대한 대화 요청이 왔는지 리스너 만들기 위한 변수..
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
        //return inflater.inflate(R.layout.fragment_group, container, false)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_group,container,false)
        val recyclerView = binding!!.recyclerview
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        myadapter = PersonAdapter(requireContext(),peers)
        myadapter.setPeerClickListener(PersonClickListener())
        recyclerView.adapter = myadapter
        return binding!!.root
    }

    inner class PersonClickListener : PersonAdapter.onPeerClickListener {
        override fun onPeerClick(peerName: String) {
            //peer 아이템 클릭 이벤트
            Log.i("peer","Hi, I'm $peerName")
            //현재 아이템(peer 정보/ meeting ID) 클릭한것을 GroupActivity로 넘겨주자
            val intent = Intent(requireContext(), GroupActivity::class.java)
            val meeting_id = peerName //peername 으로 방을 만들자
            val isJoin = true // false면 방장(가상머신) , true 면 참가자(기기)
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
         * @return A new instance of fragment GroupFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GroupFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
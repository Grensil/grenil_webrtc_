package com.example.grenil_webrtc.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grenil_webrtc.databinding.ItemPersonBinding

class PersonAdapter(context: Context, private var peers: List<String>) : RecyclerView.Adapter<PersonAdapter.ViewHolder>() {

    private var peerlist : List<String> = peers
    private lateinit var peerClickListener : onPeerClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPersonBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewHolder = holder
        viewHolder.itemView.setOnClickListener {
            peerClickListener.onPeerClick(peerlist.get(position))
        }
        val peer_name = peerlist.get(position)
        holder.bind(peer_name)
    }


    override fun getItemCount(): Int {
        return peerlist!!.size
    }
    inner class ViewHolder(private var binding : ItemPersonBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(peers : String) {
            binding.textviewProfile.text = peers
        }
    }
    interface onPeerClickListener {
        fun onPeerClick(peerName : String)
    }
    fun setPeerClickListener(peerClickListener: onPeerClickListener) {
        this.peerClickListener = peerClickListener
    }
}
package com.example.chatku.adapter

import android.view.LayoutInflater
import com.example.chatku.R
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatku.modal.RecentChats
import de.hdodenhof.circleimageview.CircleImageView

class RecentChatAdapter : RecyclerView.Adapter<RecentChatHolder>() {

    private var listofchats = listOf<RecentChats>()
    private var listener : onRecentChatCLicked? = null
    private var recentModal = RecentChats()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentChatHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recetchatlist, parent, false)
        return RecentChatHolder(view)
    }

    override fun getItemCount(): Int {
       return  listofchats.size
    }

    override fun onBindViewHolder(holder: RecentChatHolder, position: Int) {

        val recentchatlist = listofchats[position]

        recentModal = recentchatlist

        holder.userName.setText(recentchatlist.name)

        val themessage = recentchatlist.message!!.split("").take(4).joinToString("")
        val makelastmessage = "${recentchatlist.person}: ${themessage}"

        holder.lastMessage.setText(makelastmessage)

        Glide.with(holder.itemView.context).load(recentchatlist.friendsimage).into(holder.imageView)

        holder.timeView.setText(recentchatlist.time!!.substring(0,5))

        holder.itemView.setOnClickListener {
            listener?.getOnRecentChatClicked(position, recentchatlist)
        }


    }

    fun setOnRecentChatlistener(listener: onRecentChatCLicked) {
        this.listener = listener

    }

    fun setOnRecentList(list: List<RecentChats>) {
        this.listofchats = list
    }
}

class  RecentChatHolder(itemview:View) : RecyclerView.ViewHolder(itemview){
    val imageView: CircleImageView = itemView.findViewById(R.id.recentChatImageView)
    val userName: TextView = itemView.findViewById(R.id.recentChatTextName)
    val lastMessage: TextView = itemView.findViewById(R.id.recentChatTextLastMessage)
    val timeView: TextView = itemView.findViewById(R.id.recentChatTextTime)
}

interface onRecentChatCLicked {
    fun getOnRecentChatClicked(position: Int, recentchatlist: RecentChats)
}
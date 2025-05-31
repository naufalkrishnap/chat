package com.example.chatku.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatku.R
import com.example.chatku.Utils
import androidx.lifecycle.Observer
import com.example.chatku.adapter.MessageAdapter
import com.example.chatku.databinding.FragmentChatfromHomeBinding
import com.example.chatku.modal.Messages
import com.example.chatku.modal.Users
import com.example.chatku.mvvm.ChatAppViewModel
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView


class ChatFromHomeFragment : Fragment() {

    private lateinit var args: ChatFromHomeFragmentArgs
    private lateinit var chatfromhomebinding: FragmentChatfromHomeBinding
    private lateinit var chatAppViewModel: ChatAppViewModel
    private lateinit var chattoolbar: Toolbar
    private lateinit var circleImageView: CircleImageView
    private lateinit var tvUsername : TextView
    private lateinit var tvStatus : TextView
    private lateinit var backbtn : ImageView
    private lateinit var messageAdapter: MessageAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        chatfromhomebinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chatfrom_home, container, false)
        return chatfromhomebinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args = ChatFromHomeFragmentArgs.fromBundle(requireArguments())
        chatAppViewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)

        chattoolbar = view.findViewById(R.id.toolBarChat)
        circleImageView = view.findViewById(R.id.chatImageViewUser)
        tvUsername = view.findViewById(R.id.chatUserName)
        tvStatus = view.findViewById(R.id.chatUserStatus)
        backbtn = chattoolbar.findViewById(R.id.chatBackBtn)

        backbtn.setOnClickListener {
            view.findNavController().navigate(R.id.action_chatFromHomeFragment_to_homeFragment)
        }


        Glide.with(requireContext()).load(args.recentchats.friendsimage!!).into(circleImageView)

        val firestore  = FirebaseFirestore.getInstance()
        firestore.collection("Users").document(args.recentchats.friendid!!).addSnapshotListener { value, error ->
            if (error != null) {
                return@addSnapshotListener
            }


            if (value != null && value.exists()) {
                val userModal = value.toObject(Users::class.java)

                tvStatus.setText(userModal!!.status.toString())
            }
        }





        tvUsername.setText(args.recentchats.name)


        chatfromhomebinding.viewModel = chatAppViewModel
        chatfromhomebinding.lifecycleOwner = viewLifecycleOwner

        chatfromhomebinding.sendBtn.setOnClickListener {

            chatAppViewModel.sendMessage(Utils.getUiLoggedIn(), args.recentchats.friendid!!, args.recentchats.name!!, args.recentchats.friendsimage!!)
        }


        chatAppViewModel.getMessages(args.recentchats.friendid!!).observe(viewLifecycleOwner, Observer {

            initRecyclerView(it)

        })

    }

    private fun initRecyclerView(it: List<Messages>) {

        messageAdapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(context)
        chatfromhomebinding.messagesRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        messageAdapter.setMessageList(it)
        messageAdapter.notifyDataSetChanged()
        chatfromhomebinding.messagesRecyclerView.adapter = messageAdapter

    }

}
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.bumptech.glide.Glide
import com.example.chatku.R
import com.example.chatku.Utils
import com.example.chatku.adapter.MessageAdapter
import com.example.chatku.databinding.FragmentChatBinding
import com.example.chatku.modal.Messages
import com.example.chatku.mvvm.ChatAppViewModel
import de.hdodenhof.circleimageview.CircleImageView


class ChatFragment : Fragment() {

    private lateinit var args: ChatFragmentArgs
    private lateinit var chatbinding: FragmentChatBinding
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
        chatbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        return chatbinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args = ChatFragmentArgs.fromBundle(requireArguments())

        chatAppViewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)

        chattoolbar = view.findViewById(R.id.toolBarChat)
        circleImageView = view.findViewById(R.id.chatImageViewUser)
        tvUsername = view.findViewById(R.id.chatUserName)
        tvStatus = view.findViewById(R.id.chatUserStatus)
        backbtn = chattoolbar.findViewById(R.id.chatBackBtn)

        backbtn.setOnClickListener {
            view.findNavController().navigate(R.id.action_chatFromHomeFragment_to_homeFragment)
        }



        Glide.with(requireContext()).load(args.users.imageUrl).into(circleImageView)
        tvStatus.setText(args.users.status)
        tvUsername.setText(args.users.username)


        chatbinding.viewModel = chatAppViewModel
        chatbinding.lifecycleOwner = viewLifecycleOwner

        chatbinding.sendBtn.setOnClickListener {

            chatAppViewModel.sendMessage(Utils.getUiLoggedIn(), args.users.userid!!, args.users.username!!, args.users.imageUrl!!)
        }


        chatAppViewModel.getMessages(args.users.userid!!).observe(viewLifecycleOwner, Observer {

            initRecyclerView(it)

        })

    }

    private fun initRecyclerView(it: List<Messages>) {

        messageAdapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(context)
        chatbinding.messagesRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        messageAdapter.setMessageList(it)
        messageAdapter.notifyDataSetChanged()
        chatbinding.messagesRecyclerView.adapter = messageAdapter

    }

}
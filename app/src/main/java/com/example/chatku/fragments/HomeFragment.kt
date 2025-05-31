 package com.example.chatku.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.media.tv.TvContract.Programs
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatku.R
import com.example.chatku.SignInActivity
import com.example.chatku.adapter.RecentChatAdapter
import com.example.chatku.adapter.UserAdapter
import com.example.chatku.adapter.onRecentChatCLicked
import com.example.chatku.databinding.FragmentHomeBinding
import com.example.chatku.modal.RecentChats
import com.example.chatku.modal.Users
import com.example.chatku.mvvm.ChatAppViewModel
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView



 @Suppress("DEPRECATION")
 class HomeFragment : Fragment(), UserAdapter.onUserClickListener, onRecentChatCLicked{


    lateinit var rvUsers : RecyclerView
    lateinit var useradapter: UserAdapter
    lateinit var userViewModel: ChatAppViewModel
    lateinit var homebinding: FragmentHomeBinding
    lateinit var fbauth : FirebaseAuth
    lateinit var toolbar : Toolbar
    lateinit var circleImageVIew : CircleImageView
    lateinit var recentchatadapter: RecentChatAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        homebinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        return  homebinding.root
    }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         super.onViewCreated(view, savedInstanceState)

         userViewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)

         fbauth = FirebaseAuth.getInstance()

         toolbar = view.findViewById(R.id.toolbarMain)
         circleImageVIew = toolbar.findViewById(R.id.tlImage)

         homebinding.lifecycleOwner = viewLifecycleOwner


         useradapter = UserAdapter()
         rvUsers = view.findViewById(R.id.rvUsers)

         val layoutManagerUsers = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
         rvUsers.layoutManager = layoutManagerUsers

         userViewModel.getUsers().observe(viewLifecycleOwner, Observer {

             useradapter.setUserList(it)
             useradapter.setOnUserClickListener(this)
             rvUsers.adapter = useradapter
         })

         homebinding.logOut.setOnClickListener {
             fbauth.signOut()

             startActivity(Intent(requireContext(), SignInActivity::class.java))
         }

         userViewModel.imageUrl.observe(viewLifecycleOwner, Observer {

             Glide.with(requireContext()).load(it).into(circleImageVIew)
         })

         recentchatadapter = RecentChatAdapter()

         userViewModel.getRecentChats().observe(viewLifecycleOwner, Observer {

             homebinding.rvRecentChats.layoutManager = LinearLayoutManager(activity)

             recentchatadapter.setOnRecentList(it)
             homebinding.rvRecentChats.adapter = recentchatadapter
         })

         recentchatadapter.setOnRecentChatlistener(this)

         circleImageVIew.setOnClickListener {

             view?.findNavController()?.navigate(R.id.action_homeFragment_to_settingFragment)
         }
     }

     override fun onUserSelected(position: Int, users: Users) {

         val action = HomeFragmentDirections.actionHomeFragmentToChatFragment(users)
         view?.findNavController()?.navigate(action)

         Log.e("HOMEFRAGMENT", "ClickedOn${users.username}")
     }

     override fun getOnRecentChatClicked(position: Int, recentchatlist: RecentChats) {

         val action = HomeFragmentDirections.actionHomeFragmentToChatFromHomeFragment(recentchatlist)
         view?.findNavController()?.navigate(action)


     }


 }
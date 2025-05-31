package com.example.chatku.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chatku.Utils
import com.example.chatku.modal.Users
import com.google.firebase.firestore.FirebaseFirestore

class UsersRepo {

    private val firestore = FirebaseFirestore.getInstance()

    fun getUsers() : LiveData<List<Users>> {
        
        val users = MutableLiveData<List<Users>>()
        
        firestore.collection("Users").addSnapshotListener{snapshot, exception->

            if (exception!=null){

                return@addSnapshotListener
            }

            val userList = mutableListOf<Users>()
            snapshot?.documents?.forEach { document ->

                val user = document.toObject(Users::class.java)

                //all the users who are not same as the user who has logged in add them to the list
                if (user!!.userid != Utils.getUiLoggedIn()) {

                    user.let {

                        userList.add(it)
                    }

                }


                users.value = userList

            }
        }

        return users
    }

}
package com.example.chatku.mvvm

//import android.media.session.MediaSession.Token
import android.util.Log
import com.example.chatku.notifications.entity.Token
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.chatku.MyApplication
import com.example.chatku.SharedPrefs
import com.example.chatku.Utils
import com.example.chatku.modal.Messages
import com.example.chatku.modal.RecentChats
import com.example.chatku.modal.Users
import com.google.firebase.firestore.FirebaseFirestore
import com.example.chatku.notifications.FirebaseService.Companion.token
import com.example.chatku.notifications.entity.NotificationData
import com.example.chatku.notifications.entity.PushNotification
import com.example.chatku.notifications.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatAppViewModel: ViewModel() {
    val name = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()
    val message = MutableLiveData<String>()
    private val firestore = FirebaseFirestore.getInstance()

    val usersRepo = UsersRepo()
    val messageRepo = MessageRepo()
    val recentChatRepo = ChatListRepo()
    var token: String? = null

    init {
        getCurrentUser()
        getRecentChats()
    }


    fun getUsers() : LiveData<List<Users>>{

        return usersRepo.getUsers()
    }

    fun getCurrentUser() = viewModelScope.launch(Dispatchers.IO) {

        val context = MyApplication.instance.applicationContext


        firestore.collection("Users").document(Utils.getUiLoggedIn()).addSnapshotListener { value, error ->

            if (value!!.exists() && value !=null){

                val users = value.toObject(Users::class.java)
                name.value= users?.username!!
                imageUrl.value = users?.imageUrl!!

                val mySharedPrefs = SharedPrefs(context)
                mySharedPrefs.setValue("username", users.username )
            }
        }
    }

    //Send Message

    fun sendMessage(sender: String, receiver: String, friendname: String, friendimage: String) = viewModelScope.launch(Dispatchers.IO) {

        val context = MyApplication.instance.applicationContext

        val hashMap = hashMapOf<String, Any>(
            "sender" to sender,
            "receiver" to receiver,
            "message" to message.value!!,
            "time" to Utils.getTime()
        )

        val uniqueId = listOf(sender, receiver).sorted()
        uniqueId.joinToString(separator = "")


        val friendnamesplit = friendname.split("\\s".toRegex())[0]

        val mysharedPrefs = SharedPrefs(context)
        mysharedPrefs.setValue("friendid", receiver)
        mysharedPrefs.setValue("chatroomid", uniqueId.toString())
        mysharedPrefs.setValue("friendname", friendnamesplit)
        mysharedPrefs.setValue("friendimage", friendimage)

        firestore.collection("Messages").document(uniqueId.toString()).collection("chats")
            .document(Utils.getTime()).set(hashMap).addOnCompleteListener { task ->

                val hashMapForRecent = hashMapOf<String, Any>(
                    "friendid" to receiver,
                    "time" to Utils.getTime(),
                    "sender" to Utils.getUiLoggedIn(),
                    "message" to message.value!!,
                    "friendsimage" to friendimage,
                    "name" to friendname,
                    "person" to "you"
                )


                firestore.collection("Conversation${Utils.getUiLoggedIn()}").document(receiver)
                    .set(hashMapForRecent)

                firestore.collection("Conversation${receiver}").document(Utils.getUiLoggedIn())
                    .update(
                        "message",
                        message.value!!,
                        "time",
                        Utils.getTime(),
                        "person",
                        name.value!!
                    )


                firestore.collection("Token").document(receiver)
                    .addSnapshotListener { value, error ->

                        if (value != null && value.exists()) {

                            val tokenObject = value.toObject(Token::class.java)

                            token = tokenObject?.token!!

                            val loggedInUsername =
                                mysharedPrefs.getValue("username")!!.split("\\s".toRegex())[0]



                            if (message.value!!.isNotEmpty() && receiver.isNotEmpty()) {

                                PushNotification(
                                    NotificationData(
                                        loggedInUsername,
                                        message.value!!
                                    ), token!!
                                ).also {
                                    sendNotification(it)
                                }
                            } else {
                                Log.e("ViewModel", token.toString())
                            }

                        }


                        if (task.isSuccessful) {

                            message.value = ""
                        }
                    }
            }




    }

      private fun sendNotification(notification: PushNotification) = viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
            } catch (e: Exception) {

                Log.e("ViewModelError", e.toString())
                // showToast(e.message.toString())
            }
        }

    fun getMessages(friendid: String) : LiveData<List<Messages>>{
        return messageRepo.getMessages(friendid)
    }

    fun getRecentChats(): LiveData<List<RecentChats>> {
        return  recentChatRepo.getAllChatList()
    }

    fun updateProfile() = viewModelScope.launch(Dispatchers.IO) {

        val context = MyApplication.instance.applicationContext

        val hashMapUser =
            hashMapOf<String, Any>("username" to name.value!!, "imageUrl" to imageUrl.value!!)

        firestore.collection("Users").document(Utils.getUiLoggedIn()).update(hashMapUser)
            .addOnCompleteListener { task->

                if (task.isSuccessful) {

                    Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()


                }

            }

        val mysharedPrefs = SharedPrefs(context)
        val friendid = mysharedPrefs.getValue("friendid")

        val hashMapUpDATE = hashMapOf<String, Any>("friendsimage" to imageUrl.value!!,
            "name" to name.value!!, "person" to name.value!!)

        if (friendid != null) {

            firestore.collection("Conversation${friendid}")
                .document(Utils.getUiLoggedIn()).update(hashMapUpDATE)

            firestore.collection("Conversation${Utils.getUiLoggedIn()}")
                .document(friendid).update("person","you")
        }
    }
    }
package com.example.chatku.notifications

import android.content.BroadcastReceiver
import android.app.RemoteInput
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.chatku.R
import com.example.chatku.SharedPrefs
import com.example.chatku.Utils
import com.google.firebase.firestore.FirebaseFirestore

private const val CHANNEL_ID = "my_channel"

class NotificationReply : BroadcastReceiver() {

    val firestor = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationManager: NotificationManager =
            context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val remoteInput = RemoteInput.getResultsFromIntent(intent)

        if (remoteInput != null) {

            val repliedText = remoteInput.getString("KEY_REPLY_TEXT")

            val mysharedPrefs = SharedPrefs(context)
            val friendid = mysharedPrefs.getValue("friendid")
            val chatroomid = mysharedPrefs.getValue("chatroomid")
            val friendname = mysharedPrefs.getValue("friendname")
            val friendimage = mysharedPrefs.getValue("friendimage")

            val hashMap = hashMapOf<String, Any>(
                "sender" to Utils.getUiLoggedIn(),
                "time" to Utils.getTime(),
                "receiver" to friendid!!,
                "message" to repliedText!!
            )

            firestor.collection("Messages").document(chatroomid!!)
                .collection("chats").document(Utils.getTime()).set(hashMap).addOnCompleteListener {


                    if (it.isSuccessful) {
                    }


                    val setHashap = hashMapOf<String, Any>(
                        "friendid" to friendid,
                        "time" to Utils.getTime(),
                        "sender" to Utils.getUiLoggedIn(),
                        "message" to repliedText,
                        "friendsimage" to friendimage!!,
                        "name" to friendname!!,
                        "person" to "you",
                    )

                    firestor.collection("Conversation${Utils.getUiLoggedIn()}").document(friendid)
                        .set(setHashap)


                    val updateHashMap =
                        hashMapOf<String, Any>(
                            "message" to repliedText,
                            "time" to Utils.getTime(),
                            "person" to friendname!!
                        )



                    firestor.collection("Conversation${friendid}").document(Utils.getUiLoggedIn())
                        .update(updateHashMap)

                    val sharedCustomPref = SharedPrefs(context)
                    val replyid: Int = sharedCustomPref.getIntValue("values", 0)


                    val repliedNotification =
                        NotificationCompat
                            .Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.chat)
                            .setContentText("Reply Sent").build()





                    notificationManager.notify(replyid, repliedNotification)

                }


        }
    }
}
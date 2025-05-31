@file:Suppress("DEPRECATION")

package com.example.chatku

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var navController : NavController
    lateinit var auth : FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    var token : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val navHostFrag = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFrag.navController

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        generateToken()

    }

    private fun generateToken() {


        val firebaseInstance = FirebaseInstallations.getInstance()

        firebaseInstance.id.addOnSuccessListener {
            installationid ->
            FirebaseMessaging.getInstance().token.addOnSuccessListener {
                gettoken->
                token = gettoken

                val hashMap = hashMapOf<String, Any>("token" to token)

                firestore.collection("Tokens").document(Utils.getUiLoggedIn()).set(hashMap).addOnCompleteListener {

                }
            }
        }



    }

    override fun onBackPressed() {


        if (supportFragmentManager.backStackEntryCount >0) {
            super.onBackPressed()


        } else {

            if (navController.currentDestination?.id == R.id.homeFragment) {

                moveTaskToBack(true)
            } else {

                super.onBackPressed()

            }
        }

    }

    override fun onPause() {
        super.onPause()

        if (auth.currentUser!=null){


            firestore.collection("Users").document(Utils.getUiLoggedIn()).update("status", "Offline")


        }
    }

    override fun onResume() {
        super.onResume()


        if (auth.currentUser!=null){


            firestore.collection("Users").document(Utils.getUiLoggedIn()).update("status", "Online")


        }
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser!=null){


            firestore.collection("Users").document(Utils.getUiLoggedIn()).update("status", "Online")


        }
    }
}
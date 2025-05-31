@file:Suppress("DEPRECATION")

package com.example.chatku

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentDialog
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.chatku.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class SignInActivity : AppCompatActivity() {

    private lateinit var email : String
    private lateinit var password : String
    private lateinit var auth : FirebaseAuth
    private lateinit var progressDialogSignIn : ProgressDialog
    private lateinit var signInBinding: ActivitySignInBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        signInBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()


        if (auth.currentUser!=null) {
            startActivity(Intent(this, MainActivity::class.java))
        }

        progressDialogSignIn = ProgressDialog(this)

        signInBinding.signInTextToSignUp.setOnClickListener{

            startActivity(Intent(this, SignUpActivity::class.java))
        }

        signInBinding.loginButton.setOnClickListener {

            email = signInBinding.loginetemail.text.toString()
            password = signInBinding.loginetpassword.text.toString()

            if (signInBinding.loginetemail.text.isEmpty()) {
                Toast.makeText(this, "Email cant be empty", Toast.LENGTH_SHORT).show()
            }
            if (signInBinding.loginetpassword.text.isEmpty()) {
                Toast.makeText(this, "Password cant be empty", Toast.LENGTH_SHORT).show()
            }
            if (signInBinding.loginetemail.text.isEmpty()) {
                Toast.makeText(this, "Empty cant be empty", Toast.LENGTH_SHORT).show()
            }
            if (signInBinding.loginetpassword.text.isNotEmpty() && signInBinding.loginetemail.text.isNotEmpty()) {

                signIn(password, email)
            }
        }
    }

    private fun signIn(password: String, email: String) {

        progressDialogSignIn.show()
        progressDialogSignIn.setMessage("Signing In")

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {

            if (it.isSuccessful){
                progressDialogSignIn.dismiss()
                startActivity(Intent(this, MainActivity::class.java))

            } else {

                progressDialogSignIn.dismiss()
                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show()

            }
        }.addOnFailureListener {exception->

            when (exception) {
                is FirebaseAuthInvalidCredentialsException->{
                    Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                } else-> {
                    Toast.makeText(this, "Auth Failed", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        progressDialogSignIn.dismiss()
        finishAffinity()
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialogSignIn.dismiss()
    }
}
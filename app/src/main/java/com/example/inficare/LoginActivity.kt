package com.example.inficare

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*
import com.google.firebase.auth.FirebaseUser
import android.support.annotation.NonNull
import android.view.View
import com.example.inficare.R.id.loginButton
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.AuthCredential
import com.facebook.AccessToken
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import android.content.Intent




class LoginActivity:AppCompatActivity() {

    private var callbackManager: CallbackManager? = null

    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseAuthListener: FirebaseAuth.AuthStateListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        callbackManager = CallbackManager.Factory.create()

        loginButton.setReadPermissions(Arrays.asList("email"))

        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                handleFacebookAccessToken(result!!.accessToken)
            }

            override fun onCancel() {
                Toast.makeText(applicationContext, R.string.cancel_login, Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException?) {
                Toast.makeText(applicationContext, R.string.error_login, Toast.LENGTH_SHORT).show()
            }
        })

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                goMainScreen()
            }
        }
    }

    private fun handleFacebookAccessToken(accessToken: AccessToken) {
        progressBar.visibility = View.VISIBLE
        loginButton.visibility = View.GONE

        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        firebaseAuth?.signInWithCredential(credential)?.addOnCompleteListener(this) { task ->
            if (!task.isSuccessful) {
                Toast.makeText(applicationContext, R.string.firebase_error_login, Toast.LENGTH_LONG).show()
            }
            progressBar.visibility = View.GONE
            loginButton.visibility = View.VISIBLE
        }
    }

    private fun goMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth?.addAuthStateListener(firebaseAuthListener!!)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth?.removeAuthStateListener(firebaseAuthListener!!)
    }
}
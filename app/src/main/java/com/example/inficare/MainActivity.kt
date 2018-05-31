package com.example.inficare

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import com.facebook.login.LoginManager
import android.content.Intent
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val name = user.displayName
            val email = user.email
            val photoUrl = user.photoUrl
            val uid = user.uid

            nameTextView.text =name
            emailTextView.text = email
            uidTextView.text = uid

            Glide.with(MainActivity@this)
                    .load(photoUrl)
                    .apply(RequestOptions()
                            .circleCrop()
                            .fitCenter()
                    )
                    .into(picImageView)
        } else {
            goLoginScreen()
        }
    }

    private fun goLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun logout(view: View) {
        FirebaseAuth.getInstance().signOut()
        LoginManager.getInstance().logOut()
        goLoginScreen()
    }
}

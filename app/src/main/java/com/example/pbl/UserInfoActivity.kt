package com.example.pbl

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UserInfoActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_info)

        auth = Firebase.auth

        findViewById<Button>(R.id.edit).setOnClickListener {
            val intent = Intent(this, UserEditActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.shop_list).setOnClickListener {
            val intent = Intent(this, ShopListActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.sns).setOnClickListener {
            val intent = Intent(this, SnsActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.logout).setOnClickListener {
            auth.signOut()
            val intent = Intent(this,LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
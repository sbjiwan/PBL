package com.example.pbl

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserEditActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var usernamefield : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_edit)

        usernamefield = findViewById<TextView>(R.id.username)
        auth = Firebase.auth
        val userdb = Firebase.firestore.collection("user_info")
        userdb.document(auth?.uid.toString()).get().addOnSuccessListener {
            usernamefield.setText("${it["username"]}")
        }
        findViewById<Button>(R.id.change).setOnClickListener {
            val newInfo = hashMapOf(
                "userprofile" to null,
                "username" to usernamefield.text.toString()
            )
            Firebase.firestore.collection("user_info").document(auth.uid.toString()).set(newInfo)
            val intent = Intent(this, UserInfoActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.withdraw).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
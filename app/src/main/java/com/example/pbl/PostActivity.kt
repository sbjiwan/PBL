package com.example.pbl

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PostActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var authorInfo: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_add)

        auth = Firebase.auth

        findViewById<Button>(R.id.cancel).setOnClickListener {
            val intent = Intent(this, SnsActivity::class.java)
            startActivity(intent)
        }

        val userdb = Firebase.firestore.collection("user_info")
        userdb.document(auth.uid.toString()).get().addOnSuccessListener {
            authorInfo = "${it["username"]} : ${auth.currentUser?.email.toString()}"
        }
        findViewById<Button>(R.id.post_add).setOnClickListener {
            val postInfo = hashMapOf(
                "author" to auth.currentUser?.email.toString(),
                "postname" to findViewById<EditText>(R.id.namefield).text.toString(),
                "postmain" to findViewById<EditText>(R.id.mainfield).text.toString(),
                "postcategory" to findViewById<EditText>(R.id.category).text.toString()
            )
            Firebase.firestore.collection("post_list").add(postInfo)
            val intent = Intent(this, SnsActivity::class.java)
            startActivity(intent)
        }
    }
}
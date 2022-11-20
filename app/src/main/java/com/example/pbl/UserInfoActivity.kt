package com.example.pbl

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserInfoActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_info)

        auth = Firebase.auth
        val userdb = Firebase.firestore.collection("user_info")
        userdb.document(auth.currentUser?.email.toString()).get().addOnSuccessListener {
            findViewById<TextView>(R.id.userid).setText(auth.currentUser?.email.toString())
        }
        Firebase.firestore.collection("post_list").whereEqualTo("author", auth.currentUser?.email.toString()).get().addOnSuccessListener {
            for (data in it) {
                val post = layoutInflater.inflate(R.layout.post_view, null, false);
                post.findViewById<TextView>(R.id.username).setText(Firebase.auth.currentUser?.email.toString());
                post.findViewById<TextView>(R.id.post_title).setText(data["postname"].toString())
                post.findViewById<TextView>(R.id.post_main).setText(data["postmain"].toString())
                post.findViewById<TextView>(R.id.post_category).setText("카테고리: ${data["postcategory"].toString()}")
                findViewById<LinearLayout>(R.id.my_post).addView(post);
            }
        }

        findViewById<Button>(R.id.edit_profile).setOnClickListener {
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
package com.example.pbl

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class SnsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sns)

        Firebase.firestore.collection("post_list").get().addOnSuccessListener {
            for (data in it) {
                val post = layoutInflater.inflate(R.layout.post_view, null, false);
                post.findViewById<TextView>(R.id.username).setText(Firebase.auth.currentUser?.email.toString());
                post.findViewById<TextView>(R.id.post_title).setText(data["postname"].toString())
                post.findViewById<TextView>(R.id.post_main).setText(data["postmain"].toString())
                post.findViewById<TextView>(R.id.post_category).setText("카테고리: ${data["postcategory"].toString()}")
                findViewById<LinearLayout>(R.id.post_list).addView(post);
            }
        }
        findViewById<Button>(R.id.user_info).setOnClickListener {
            val intent = Intent(this, UserInfoActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.postedit).setOnClickListener {
            val intent = Intent(this, PostActivity::class.java)
            startActivity(intent)
        }
    }
}
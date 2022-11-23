package com.example.pbl

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import org.w3c.dom.Text

class SnsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sns)

        Firebase.firestore.collection("post_list").get().addOnSuccessListener {
            for (data in it) {
                val post = layoutInflater.inflate(R.layout.post_view, null, false);

                val ref = FirebaseStorage.getInstance().getReference(data["author"].toString() + "_profile")

                ref.downloadUrl
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if(task.isSuccessful){
                            Glide.with(this)
                                .load(task.result)
                                .into(post.findViewById(R.id.user_profile))
                        }
                    })
                post.findViewById<TextView>(R.id.username).setText(data["author"].toString());
                post.findViewById<TextView>(R.id.post_title).setText(data["post_name"].toString())
                post.findViewById<TextView>(R.id.post_main).setText(data["post_main"].toString())
              //  post.findViewById<TextView>(R.id.post_category).setText("카테고리: ${data["postcategory"].toString()}")
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
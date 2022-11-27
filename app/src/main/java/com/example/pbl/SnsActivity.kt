package com.example.pbl

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pbl.util.FirebaseUtil
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.Query

class SnsActivity : AppCompatActivity() {
    private val util = FirebaseUtil()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sns)

        // 게시글 모두 불러오기

        util.posts.orderBy("time", Query.Direction.DESCENDING).get().addOnSuccessListener {
            for (data in it) {
                val post = layoutInflater.inflate(R.layout.post_item, null, false);
                util.instance.getReference(data["author"].toString() + "_profile")
                    .downloadUrl
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if(task.isSuccessful){
                            Glide.with(this)
                                .load(task.result)
                                .into(post.findViewById(R.id.user_profile))
                        }
                    })
                post.setOnClickListener {
                    val intent = Intent(this, UserPostActivity::class.java)
                    intent.putExtra("uid", data.id)
                    startActivity(intent)
                }
                post.findViewById<TextView>(R.id.username).text = data["author"].toString();
                post.findViewById<TextView>(R.id.post_title).text = data["post_name"].toString()
                post.findViewById<TextView>(R.id.post_main).text = data["post_main"].toString()
                post.findViewById<TextView>(R.id.post_date).text = data["time"].toString()
              //  post.findViewById<TextView>(R.id.post_category).setText("카테고리: ${data["postcategory"].toString()}")
                findViewById<LinearLayout>(R.id.post_list).addView(post);
            }
        }

        // 내 정보 버튼
        
        findViewById<Button>(R.id.user_info).setOnClickListener {
            val intent = Intent(this, UserInfoActivity::class.java)
            startActivity(intent)
        }
        
        // 게시글 추가 버튼

        findViewById<Button>(R.id.post_add).setOnClickListener {
            val intent = Intent(this, PostActivity::class.java)
            startActivity(intent)
        }
    }
}
package com.example.pbl

import android.content.Intent
import android.os.Bundle
import android.view.View
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

        // 카테고리 스피너
        val spinner = findViewById<Spinner>(R.id.post_category)
        spinner.adapter = ArrayAdapter.createFromResource(this, R.array.post_category,android.R.layout.simple_spinner_item)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 게시글 모두 불러오기
                val selectedItem = parent!!.selectedItem
                findViewById<LinearLayout>(R.id.post_list).removeAllViews()
                util.posts.orderBy("time", Query.Direction.DESCENDING).get().addOnSuccessListener {
                    for (data in it) {
                        if (selectedItem == "[없음]") {
                            val post = layoutInflater.inflate(R.layout.post_item, null, false);
                            util.instance.getReference(data["author"].toString() + "_profile")
                                .downloadUrl
                                .addOnCompleteListener(OnCompleteListener { task ->
                                    if(task.isSuccessful){
                                        Glide.with(view!!.context)
                                            .load(task.result)
                                            .into(post.findViewById(R.id.user_profile))
                                    }
                                })
                            post.setOnClickListener {
                                val intent = Intent(view!!.context, UserPostActivity::class.java)
                                intent.putExtra("uid", data.id)
                                startActivity(intent)
                            }
                            post.findViewById<TextView>(R.id.username).text = data["author"].toString();
                            post.findViewById<TextView>(R.id.post_title).text = data["post_name"].toString()
                            post.findViewById<TextView>(R.id.post_main).text = data["post_main"].toString()
                            post.findViewById<TextView>(R.id.post_date).text = data["time"].toString()
                            post.findViewById<TextView>(R.id.post_category).text = "${data["post_category"].toString()}"
                            findViewById<LinearLayout>(R.id.post_list).addView(post);
                        } else if (data["post_category"].toString() == selectedItem) {
                            val post = layoutInflater.inflate(R.layout.post_item, null, false);
                            util.instance.getReference(data["author"].toString() + "_profile")
                                .downloadUrl
                                .addOnCompleteListener(OnCompleteListener { task ->
                                    if(task.isSuccessful){
                                        Glide.with(view!!.context)
                                            .load(task.result)
                                            .into(post.findViewById(R.id.user_profile))
                                    }
                                })
                            post.setOnClickListener {
                                val intent = Intent(view!!.context, UserPostActivity::class.java)
                                intent.putExtra("uid", data.id)
                                startActivity(intent)
                            }
                            post.findViewById<TextView>(R.id.username).text = data["author"].toString();
                            post.findViewById<TextView>(R.id.post_title).text = data["post_name"].toString()
                            post.findViewById<TextView>(R.id.post_main).text = data["post_main"].toString()
                            post.findViewById<TextView>(R.id.post_date).text = data["time"].toString()
                            post.findViewById<TextView>(R.id.post_category).text = "${data["post_category"].toString()}"
                            findViewById<LinearLayout>(R.id.post_list).addView(post);
                        }
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

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
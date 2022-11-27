package com.example.pbl

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pbl.Utils.FirebaseUtil
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlin.properties.Delegates

class CommentActivity : AppCompatActivity() {
    private val util = FirebaseUtil()
    private lateinit var documentuid : String
    private var commentnum by Delegates.notNull<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comment_add)

        // lateinit 변수 초기화

        documentuid = intent.getStringExtra("uid")!!
        commentnum = intent.getIntExtra("num", -1)

        // 동기화 문서 요청

        util.posts.document(documentuid).get().addOnSuccessListener {

            // 유저 프로필 동기화

            util.instance.getReference(it["author"].toString() + "_profile")
                .downloadUrl
                .addOnCompleteListener(OnCompleteListener { task ->
                    if(task.isSuccessful){
                        Glide.with(this)
                            .load(task.result)
                            .into(findViewById<ImageView>(R.id.user_profile))
                    }
                })
            
            // 각종 데이터 동기화
            
            findViewById<TextView>(R.id.username).text = it["author"].toString()
            findViewById<TextView>(R.id.post_date).text = it["time"].toString()
            if (commentnum != -1) {
                val comments = it["comment"] as ArrayList<MutableMap<String, String>>
                findViewById<EditText>(R.id.comment_main).setText(comments[commentnum]["comment"])
            }
        }

        // 댓글 게시

        findViewById<Button>(R.id.comment).setOnClickListener {
            if (findViewById<EditText>(R.id.comment_main).text.toString().isNotEmpty()) {
                util.posts.document(documentuid).get().addOnSuccessListener {
                    val comment = it["comment"] as ArrayList<MutableMap<String, String>>
                    if (commentnum == -1) {
                        comment.add(mutableMapOf(
                            "author" to util.currentUser,
                            "comment" to findViewById<EditText>(R.id.comment_main).text.toString()
                        ))
                    } else {
                        comment[commentnum] = mutableMapOf(
                            "author" to util.currentUser,
                            "comment" to findViewById<EditText>(R.id.comment_main).text.toString()
                        )
                    }
                    util.posts.document(documentuid).update(hashMapOf<String, Any>(
                        "author" to it["author"] as String,
                        "post_name" to it["post_name"] as String,
                        "post_main" to it["post_main"] as String,
                        "time" to it["time"] as String,
                        "comment" to comment
                    ))
                }
                val intent = Intent(this,UserPostActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("uid", documentuid)
                startActivity(intent)
            } else Toast.makeText(this, "댓글 내용을 작성해주세요.", Toast.LENGTH_SHORT).show()
        }
    }
}
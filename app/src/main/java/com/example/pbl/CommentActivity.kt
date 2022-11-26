package com.example.pbl

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlin.properties.Delegates

class CommentActivity : AppCompatActivity() {
    private lateinit var documentuid : String
    private var commentnum by Delegates.notNull<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comment_add)
        documentuid = intent.getStringExtra("uid")!!
        commentnum = intent.getIntExtra("num", -1)
        Firebase.firestore.collection("post_list").document(documentuid).get().addOnSuccessListener {
            FirebaseStorage.getInstance().getReference(it["author"].toString() + "_profile").downloadUrl
                .addOnCompleteListener(OnCompleteListener { task ->
                    if(task.isSuccessful){
                        Glide.with(this)
                            .load(task.result)
                            .into(findViewById<ImageView>(R.id.user_profile))
                    }
                })
            findViewById<TextView>(R.id.username).text = it["author"].toString()
            findViewById<TextView>(R.id.post_date).text = it["time"].toString()
            val comments = it["comment"] as ArrayList<MutableMap<String, String>>
            if (commentnum != -1) findViewById<EditText>(R.id.comment_main).setText(comments[commentnum]["comment"])
        }
        findViewById<Button>(R.id.comment).setOnClickListener {
            if (findViewById<EditText>(R.id.comment_main).text.toString().isNotEmpty()) {
                val postInfo = mutableMapOf<String, Any>()
                Firebase.firestore.collection("post_list").document(documentuid).get().addOnSuccessListener {
                    postInfo["author"] = it["author"] as String
                    postInfo["post_name"] = it["post_name"] as String
                    postInfo["post_main"] = it["post_main"] as String
                    postInfo["time"] = it["time"] as String
                    val newComment = it["comment"] as ArrayList<MutableMap<String, String>>
                    if (commentnum == -1) {
                        newComment.add(
                            mutableMapOf<String, String>(
                                "author" to Firebase.auth.currentUser?.email.toString(),
                                "comment" to findViewById<EditText>(R.id.comment_main).text.toString()
                            )
                        )
                    } else {
                        newComment[commentnum] = mutableMapOf<String, String>(
                            "author" to Firebase.auth.currentUser?.email.toString(),
                            "comment" to findViewById<EditText>(R.id.comment_main).text.toString()
                        )
                    }
                    postInfo["comment"] = newComment
                    Firebase.firestore.collection("post_list").document(intent.getStringExtra("uid")!!).update(postInfo)
                }
                val intent = Intent(this,UserPostActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("uid", documentuid)
                startActivity(intent)
            } else Toast.makeText(this, "댓글 내용을 작성해주세요.", Toast.LENGTH_SHORT).show()
        }
    }
}
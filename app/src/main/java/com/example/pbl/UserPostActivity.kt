package com.example.pbl

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class UserPostActivity : AppCompatActivity() {
    lateinit var documentuid : String
    lateinit var author : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_view)
        documentuid = intent.getStringExtra("uid")!!

        Firebase.firestore.collection("post_list").document(documentuid).get().addOnSuccessListener {
            author = it["author"].toString()
            if (author != Firebase.auth.currentUser?.email.toString()) {
                findViewById<Button>(R.id.edit).visibility = View.GONE
                findViewById<Button>(R.id.del).visibility = View.GONE
            } else {
                findViewById<Button>(R.id.edit).setOnClickListener {
                    val intent = Intent(this, PostActivity::class.java)
                    intent.putExtra("uid", documentuid)
                    startActivity(intent)
                }
                findViewById<Button>(R.id.del).setOnClickListener {
                    AlertDialog.Builder(this)
                        .setTitle("경고")
                        .setMessage("정말로 게시글을 삭제하시겠습니까?")
                        .setPositiveButton("네") {dialogInterface: DialogInterface, i: Int ->
                            Firebase.firestore.collection("post_list").document(documentuid).delete()
                            val intent = Intent(this, UserInfoActivity::class.java)
                            startActivity(intent)
                        }
                        .setNegativeButton("아니오") { dialogInterface: DialogInterface, i: Int -> }
                        .show()
                }
            }
            FirebaseStorage.getInstance().getReference(author + "_profile").downloadUrl
                .addOnCompleteListener(OnCompleteListener { task ->
                    if(task.isSuccessful){
                        Glide.with(this)
                            .load(task.result)
                            .into(findViewById<ImageView>(R.id.user_profile))
                    }
                })
            findViewById<LinearLayout>(R.id.user_info).setOnClickListener {
                val intent = Intent(this, UserInfoActivity::class.java)
                intent.putExtra("user", author)
                startActivity(intent)
            }
            findViewById<TextView>(R.id.username).text = author
            findViewById<TextView>(R.id.post_title).text = it["post_name"].toString()
            findViewById<TextView>(R.id.post_main).text = it["post_main"].toString()
            val comments = it["comment"] as ArrayList<MutableMap<String, String>>
            comments.reverse()
            for ((commentindex, comment) in comments.withIndex()) {
                val item = layoutInflater.inflate(R.layout.comment_item, null, false);
                val ref = FirebaseStorage.getInstance().getReference(comment["author"].toString() + "_profile")
                ref.downloadUrl
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if(task.isSuccessful){
                            Glide.with(this)
                                .load(task.result)
                                .into(item.findViewById(R.id.user_profile))
                        }
                    })
                item.findViewById<LinearLayout>(R.id.user_info).setOnClickListener {
                    val intent = Intent(this, UserInfoActivity::class.java)
                    intent.putExtra("user", comment["author"])
                    startActivity(intent)
                }
                item.findViewById<TextView>(R.id.username).text = comment["author"]
                if (comment["author"] == Firebase.auth.currentUser?.email.toString()) {
                    item.findViewById<Button>(R.id.edit).setOnClickListener {
                        val intent = Intent(this, CommentActivity::class.java)
                        intent.putExtra("uid", documentuid)
                        intent.putExtra("num", comments.lastIndex - commentindex)
                        startActivity(intent)
                    }
                    item.findViewById<Button>(R.id.del).setOnClickListener {
                        AlertDialog.Builder(this)
                            .setTitle("경고")
                            .setMessage("정말로 댓글을 삭제하시겠습니까?")
                            .setPositiveButton("네") { dialogInterface: DialogInterface, i: Int ->
                                val postInfo = mutableMapOf<String, Any>()
                                Firebase.firestore.collection("post_list").document(documentuid).get().addOnSuccessListener {
                                    postInfo["author"] = it["author"] as String
                                    postInfo["post_name"] = it["post_name"] as String
                                    postInfo["post_main"] = it["post_main"] as String
                                    postInfo["time"] = it["time"] as String
                                    val newComment = it["comment"] as ArrayList<MutableMap<String, String>>
                                    newComment.remove(mutableMapOf<String, String>(
                                        "author" to comment["author"].toString(),
                                        "comment" to comment["comment"].toString()
                                    ))
                                    postInfo["comment"] = newComment
                                    Firebase.firestore.collection("post_list").document(intent.getStringExtra("uid")!!).update(postInfo)
                                    finish()
                                    startActivity(this.intent)
                                }
                            }
                            .setNegativeButton("아니오") { dialogInterface: DialogInterface, i: Int -> }
                            .show()
                    }
                } else if (author == Firebase.auth.currentUser?.email.toString()) {
                    item.findViewById<Button>(R.id.edit).visibility = View.GONE
                    item.findViewById<Button>(R.id.del).setOnClickListener {
                        AlertDialog.Builder(this)
                            .setTitle("경고")
                            .setMessage("정말로 댓글을 삭제하시겠습니까?")
                            .setPositiveButton("네") { dialogInterface: DialogInterface, i: Int ->
                                val postInfo = mutableMapOf<String, Any>()
                                Firebase.firestore.collection("post_list").document(documentuid).get().addOnSuccessListener {
                                    postInfo["author"] = it["author"] as String
                                    postInfo["post_name"] = it["post_name"] as String
                                    postInfo["post_main"] = it["post_main"] as String
                                    postInfo["time"] = it["time"] as String
                                    val newComment = it["comment"] as ArrayList<MutableMap<String, String>>
                                    newComment.remove(mutableMapOf<String, String>(
                                        "author" to comment["author"].toString(),
                                        "comment" to comment["comment"].toString()
                                    ))
                                    postInfo["comment"] = newComment
                                    Firebase.firestore.collection("post_list").document(intent.getStringExtra("uid")!!).update(postInfo)
                                    finish()
                                    startActivity(this.intent)
                                }
                            }
                            .setNegativeButton("아니오") { dialogInterface: DialogInterface, i: Int -> }
                            .show()
                    }
                } else {
                    item.findViewById<Button>(R.id.edit).visibility = View.GONE
                    item.findViewById<Button>(R.id.del).visibility = View.GONE
                }
                item.findViewById<TextView>(R.id.comment_main).text = comment["comment"]
                findViewById<LinearLayout>(R.id.comment_list).addView(item)
            }
        }
        findViewById<Button>(R.id.back).setOnClickListener {
            val intent = Intent(this,SnsActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.comment).setOnClickListener {
            val intent = Intent(this,CommentActivity::class.java)
            intent.putExtra("uid", documentuid)
            startActivity(intent)
        }
    }
}
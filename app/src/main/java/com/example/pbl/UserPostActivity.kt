package com.example.pbl

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pbl.util.FirebaseUtil
import com.google.android.gms.tasks.OnCompleteListener

class UserPostActivity : AppCompatActivity() {
    private val util = FirebaseUtil()
    private lateinit var documentuid : String
    private lateinit var author : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_view)
        
        // lateinit 변수 초기화
        
        documentuid = intent.getStringExtra("uid")!!

        util.posts.document(documentuid).get().addOnSuccessListener {
            author = it["author"].toString()

            // 만약 게시글 작성자가 내가 아니라면

            if (author != util.currentUser) {
                findViewById<Button>(R.id.edit).visibility = View.GONE
                findViewById<Button>(R.id.del).visibility = View.GONE

            // 게시글 작성자가 나라면

            } else {

                // 게시글 수정 버튼

                findViewById<Button>(R.id.edit).setOnClickListener {
                    val intent = Intent(this, PostActivity::class.java)
                    intent.putExtra("uid", documentuid)
                    startActivity(intent)
                }

                // 게시글 삭제 버튼

                findViewById<Button>(R.id.del).setOnClickListener {
                    AlertDialog.Builder(this)
                        .setTitle("경고")
                        .setMessage("정말로 게시글을 삭제하시겠습니까?")
                        .setPositiveButton("네") {dialogInterface: DialogInterface, i: Int ->
                            util.posts.document(documentuid).delete()
                            val intent = Intent(this, UserInfoActivity::class.java)
                            startActivity(intent)
                        }
                        .setNegativeButton("아니오") { dialogInterface: DialogInterface, i: Int -> }
                        .show()
                }
            }

            // 게시글 작성자 프로필 동기화
            
            util.instance.getReference(author + "_profile").downloadUrl
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Glide.with(this)
                            .load(task.result)
                            .into(findViewById<ImageView>(R.id.user_profile))
                    }
                })

            // 게시글 작성자 프로필 이동

            findViewById<LinearLayout>(R.id.user_info).setOnClickListener {
                val intent = Intent(this, UserInfoActivity::class.java)
                intent.putExtra("user", author)
                startActivity(intent)
            }

            // 게시글 정보 동기화

            findViewById<TextView>(R.id.username).text = author
            findViewById<TextView>(R.id.post_title).text = it["post_name"].toString()
            findViewById<TextView>(R.id.post_main).text = it["post_main"].toString()
            findViewById<TextView>(R.id.post_date).text = it["post_date"].toString()

            // 댓글 배치

            val comments = it["comment"] as ArrayList<MutableMap<String, String>>
            comments.reverse()
            for ((commentindex, comment) in comments.withIndex()) {
                val item = layoutInflater.inflate(R.layout.comment_item, null, false);
                util.instance.getReference(comment["author"].toString() + "_profile")
                    .downloadUrl
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if(task.isSuccessful){
                            Glide.with(this)
                                .load(task.result)
                                .into(item.findViewById(R.id.user_profile))
                        }
                    })

                // 댓글 작성자 프로필 이동

                item.findViewById<LinearLayout>(R.id.user_info).setOnClickListener {
                    val intent = Intent(this, UserInfoActivity::class.java)
                    intent.putExtra("user", comment["author"])
                    startActivity(intent)
                }

                // 댓글 정보 동기화

                item.findViewById<TextView>(R.id.username).text = comment["author"]
                item.findViewById<TextView>(R.id.comment_main).text = comment["comment"]

                // 댓글 작성자가 나라면

                if (comment["author"] == util.currentUser) {

                    // 게시글 수정 버튼

                    item.findViewById<Button>(R.id.edit).setOnClickListener {
                        val intent = Intent(this, CommentActivity::class.java)
                        intent.putExtra("uid", documentuid)
                        intent.putExtra("num", comments.lastIndex - commentindex)
                        startActivity(intent)
                    }

                    // 게시글 삭제 버튼
                    
                    item.findViewById<Button>(R.id.del).setOnClickListener {
                        AlertDialog.Builder(this)
                            .setTitle("경고")
                            .setMessage("정말로 댓글을 삭제하시겠습니까?")
                            .setPositiveButton("네") { dialogInterface: DialogInterface, i: Int ->
                                util.posts.document(documentuid).get().addOnSuccessListener {domdata->
                                    val newComment = domdata["comment"] as ArrayList<MutableMap<String, String>>
                                    newComment.remove(mutableMapOf<String, String>(
                                        "author" to comment["author"].toString(),
                                        "comment" to comment["comment"].toString()
                                    ))
                                    util.posts.document(documentuid).update(hashMapOf<String, Any>(
                                        "author" to domdata["author"] as String,
                                        "post_name" to domdata["post_name"] as String,
                                        "post_main" to domdata["post_main"] as String,
                                        "time" to domdata["time"] as String,
                                        "comment" to newComment
                                    ))
                                    finish()
                                    startActivity(this.intent)
                                }
                            }
                            .setNegativeButton("아니오") { dialogInterface: DialogInterface, i: Int -> }
                            .show()
                    }
                    
                // 게시글 작성자가 나라면
                    
                } else if (author == util.currentUser) {

                    // 댓글 삭제 버튼

                    item.findViewById<Button>(R.id.del).setOnClickListener {
                        AlertDialog.Builder(this)
                            .setTitle("경고")
                            .setMessage("정말로 댓글을 삭제하시겠습니까?")
                            .setPositiveButton("네") { dialogInterface: DialogInterface, i: Int ->
                                util.posts.document(documentuid).get().addOnSuccessListener {domdata->
                                    val newComment = domdata["comment"] as ArrayList<MutableMap<String, String>>
                                    newComment.remove(mutableMapOf<String, String>(
                                        "author" to comment["author"].toString(),
                                        "comment" to comment["comment"].toString()
                                    ))
                                    util.posts.document(documentuid).update(hashMapOf<String, Any>(
                                        "author" to domdata["author"] as String,
                                        "post_name" to domdata["post_name"] as String,
                                        "post_main" to domdata["post_main"] as String,
                                        "time" to domdata["time"] as String,
                                        "comment" to newComment
                                    ))
                                    finish()
                                    startActivity(this.intent)
                                }
                            }
                            .setNegativeButton("아니오") { dialogInterface: DialogInterface, i: Int -> }
                            .show()

                        // 권한을 보유하지 않은 버튼 숨김 처리

                        item.findViewById<Button>(R.id.edit).visibility = View.GONE
                    }

                // 제 3자일 때

                } else {

                    // 권한을 보유하지 않은 버튼 숨김 처리

                    item.findViewById<Button>(R.id.edit).visibility = View.GONE
                    item.findViewById<Button>(R.id.del).visibility = View.GONE
                }
                findViewById<LinearLayout>(R.id.comment_list).addView(item)
            }
        }

        // 뒤로 가기 버튼
        
        findViewById<Button>(R.id.back).setOnClickListener {
            val intent = Intent(this,SnsActivity::class.java)
            startActivity(intent)
        }
        
        // 게시글 리로드 버튼

        findViewById<Button>(R.id.reload).setOnClickListener {
            startActivity(intent)
        }
        
        // 댓글 달기 버튼

        findViewById<Button>(R.id.comment).setOnClickListener {
            val intent = Intent(this,CommentActivity::class.java)
            intent.putExtra("uid", documentuid)
            startActivity(intent)
        }
    }
}
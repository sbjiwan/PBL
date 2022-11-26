package com.example.pbl

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
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
            FirebaseStorage.getInstance().getReference(author + "_profile").downloadUrl
                .addOnCompleteListener(OnCompleteListener { task ->
                    if(task.isSuccessful){
                        Glide.with(this)
                            .load(task.result)
                            .into(findViewById<ImageView>(R.id.user_profile))
                    }
                })
            findViewById<TextView>(R.id.username).text = author
            findViewById<TextView>(R.id.post_title).text = it["post_name"].toString()
            findViewById<TextView>(R.id.post_main).text = it["post_main"].toString()
            val comments = it["comment"] as ArrayList<MutableMap<String, String>>
            comments.reverse()
            for (comment in comments) {
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
                item.findViewById<TextView>(R.id.username).text = comment["author"]
                item.findViewById<TextView>(R.id.comment_main).text = comment["comment"]
                findViewById<LinearLayout>(R.id.comment_list).addView(item)
            }
        }
        findViewById<Button>(R.id.edit).setOnClickListener {
            if (checkUser()) {
                val intent = Intent(this, PostActivity::class.java)
                intent.putExtra("uid", documentuid)
                startActivity(intent)
            } else Toast.makeText(this, "해당 게시글의 작성자만 게시글을 수정할 수 있습니다.", Toast.LENGTH_LONG).show()
        }
        findViewById<Button>(R.id.del).setOnClickListener {
            if (checkUser()) {
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
            } else Toast.makeText(this, "해당 게시글의 작성자만 게시글을 삭제할 수 있습니다.", Toast.LENGTH_LONG).show()
        }
        findViewById<Button>(R.id.comment).setOnClickListener {
            val intent = Intent(this,CommentActivity::class.java)
            intent.putExtra("uid", documentuid)
            startActivity(intent)
        }
    }
    private fun checkUser() : Boolean {
        return (Firebase.auth.currentUser?.email.toString() == author)
    }
}
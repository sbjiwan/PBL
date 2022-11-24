package com.example.pbl

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class PostActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var authorInfo: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_add)

        auth = Firebase.auth

        findViewById<Button>(R.id.cancel).setOnClickListener {
            val intent = Intent(this, SnsActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.post_add).setOnClickListener {
            val postInfo = hashMapOf(
                "author" to auth.currentUser?.email.toString(),
                "post_name" to findViewById<EditText>(R.id.namefield).text.toString(),
                "post_main" to findViewById<EditText>(R.id.mainfield).text.toString(),
                //"post_category" to findViewById<EditText>(R.id.category).text.toString()
                "time" to getTime() //데이터베이스에 시간 넣기
            )
            Firebase.firestore.collection("post_list").add(postInfo)
            val intent = Intent(this, SnsActivity::class.java)
            startActivity(intent)
        }


    }
    //시간 함수
    fun getTime() : String{
        val currentDayAndTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss",Locale.KOREA).format(currentDayAndTime)

        return dateFormat
    }
}

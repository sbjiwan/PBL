package com.example.pbl

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class PostActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var statePort by Delegates.notNull<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_add)

        auth = Firebase.auth

        findViewById<Button>(R.id.cancel).setOnClickListener {
            val intent = Intent(this, SnsActivity::class.java)
            startActivity(intent)
        }

        if (intent.getStringExtra("uid") == null) statePort = 0;
        else {
            Firebase.firestore.collection("post_list").document(intent.getStringExtra("uid")!!).get().addOnSuccessListener {
                findViewById<EditText>(R.id.namefield).setText(it["post_name"].toString())
                findViewById<EditText>(R.id.mainfield).setText(it["post_main"].toString())
            }
            statePort = 1
        }

        findViewById<Button>(R.id.post_add).setOnClickListener {
            val postInfo = mutableMapOf<String, Any>(
                "author" to auth.currentUser?.email.toString(),
                "post_name" to findViewById<EditText>(R.id.namefield).text.toString(),
                "post_main" to findViewById<EditText>(R.id.mainfield).text.toString(),
                //"post_category" to findViewById<EditText>(R.id.category).text.toString()
                "time" to getTime(),
                "comment" to ArrayList<MutableMap<String, String>>()
            )
            if (statePort == 0) Firebase.firestore.collection("post_list").add(postInfo)
            else Firebase.firestore.collection("post_list").document(intent.getStringExtra("uid")!!).update(postInfo)
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

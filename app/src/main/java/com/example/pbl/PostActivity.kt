package com.example.pbl

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.pbl.Utils.FirebaseUtil
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class PostActivity : AppCompatActivity() {
    val util = FirebaseUtil()
    private var statePort by Delegates.notNull<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_add)

        // lateinit 변수 초기화

        statePort =
            if (intent.getStringExtra("uid") == null) 0;
            else {
                util.posts.document(intent.getStringExtra("uid")!!).get().addOnSuccessListener {
                    findViewById<EditText>(R.id.namefield).setText(it["post_name"].toString())
                    findViewById<EditText>(R.id.mainfield).setText(it["post_main"].toString())
                }
                1
        }

        // 취소 버튼

        findViewById<Button>(R.id.cancel).setOnClickListener {
            val intent = Intent(this, SnsActivity::class.java)
            startActivity(intent)
        }

        // 게시 버튼

        findViewById<Button>(R.id.post_add).setOnClickListener {
            val hashMap = hashMapOf<String, Any>(
                "author" to util.currentUser,
                "post_name" to findViewById<EditText>(R.id.namefield).text.toString(),
                "post_main" to findViewById<EditText>(R.id.mainfield).text.toString(),
                //"post_category" to findViewById<EditText>(R.id.category).text.toString()
                "time" to getTime(),
                "comment" to ArrayList<MutableMap<String, String>>()
            )
            if (statePort == 0) util.posts.add(hashMap)
            else util.posts.document(intent.getStringExtra("uid")!!).update(hashMap)
            val intent = Intent(this, SnsActivity::class.java)
            startActivity(intent)
        }
    }

    // 시간 가져오는 함수

    private fun getTime(): String {
        val currentDayAndTime = Calendar.getInstance().time
        return SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA).format(currentDayAndTime)
    }
}
package com.example.pbl

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.pbl.util.FirebaseUtil
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class PostActivity : AppCompatActivity() {
    private val util = FirebaseUtil()
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
                    val tester = resources.getStringArray(R.array.post_category).toCollection(ArrayList<String>())
                    findViewById<Spinner>(R.id.post_category).setSelection(tester.indexOf(it["post_category"].toString()))
                }
                1
        }

        // 취소 버튼

        findViewById<Button>(R.id.cancel).setOnClickListener {
            val intent = Intent(this, SnsActivity::class.java)
            startActivity(intent)
        }

        // 카테고리 스피너
        
        findViewById<Spinner>(R.id.post_category).adapter =
            ArrayAdapter.createFromResource(this, R.array.post_category,android.R.layout.simple_spinner_item)

        // 게시 버튼

        findViewById<Button>(R.id.post_add).setOnClickListener {
            val postname = findViewById<EditText>(R.id.namefield).text.toString()
            val postmain = findViewById<EditText>(R.id.mainfield).text.toString()
            if (postname.isNotEmpty() && postmain.isNotEmpty()) {
                val hashMap = hashMapOf<String, Any>(
                    "author" to util.currentUser,
                    "post_name" to postname,
                    "post_main" to postmain,
                    "post_category" to findViewById<Spinner>(R.id.post_category).selectedItem.toString(),
                    "time" to getTime(),
                    "comment" to ArrayList<MutableMap<String, String>>()
                )
                if (statePort == 0) util.posts.add(hashMap)
                else util.posts.document(intent.getStringExtra("uid")!!).update(hashMap)
                val intent = Intent(this, SnsActivity::class.java)
                startActivity(intent)
            } else Toast.makeText(this, "게시글 이름이나 본문이 비어있을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 시간 가져오는 함수

    private fun getTime(): String {
        val currentDayAndTime = Calendar.getInstance().time
        return SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA).format(currentDayAndTime)
    }
}
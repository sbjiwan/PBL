package com.example.pbl

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pbl.Utils.FirebaseUtil
import com.google.android.gms.tasks.OnCompleteListener

class MyPinActivity : AppCompatActivity() {
    val util = FirebaseUtil()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_pin)
        
        // 내 핀 정보 동기화
        
        util.pins.document(util.currentUser).get().addOnSuccessListener {
            val pinUsers = it["pin_list"] as ArrayList<String>
            for (pinUser in pinUsers) {
                val pinObject = layoutInflater.inflate(R.layout.pin_item, null, false);
                util.instance.getReference("${pinUser}_profile")
                    .downloadUrl
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if(task.isSuccessful){
                            Glide.with(this)
                                .load(task.result)
                                .into(pinObject.findViewById(R.id.user_profile))
                        }
                    })
                pinObject.findViewById<TextView>(R.id.username).text = pinUser
                pinObject.setOnClickListener {
                    val intent = Intent(this, UserInfoActivity::class.java)
                    intent.putExtra("user", pinUser)
                    startActivity(intent)
                }
                findViewById<LinearLayout>(R.id.pin_list).addView(pinObject)
            }
        }

        // 뒤로 가기 버튼

        findViewById<Button>(R.id.back).setOnClickListener {
            val intent = Intent(this, UserInfoActivity::class.java)
            startActivity(intent)
        }
    }
}
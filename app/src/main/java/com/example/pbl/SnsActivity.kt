package com.example.pbl

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SnsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sns)

        findViewById<Button>(R.id.user_info).setOnClickListener {
            val intent = Intent(this, UserInfoActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.shop_list).setOnClickListener {
            val intent = Intent(this, ShopListActivity::class.java)
            startActivity(intent)
        }
    }
}
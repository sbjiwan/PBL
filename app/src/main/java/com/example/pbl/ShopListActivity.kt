package com.example.pbl

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ShopListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shop_list)

        findViewById<Button>(R.id.user_info).setOnClickListener {
            val intent = Intent(this, UserInfoActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.sns).setOnClickListener {
            val intent = Intent(this, SnsActivity::class.java)
            startActivity(intent)
        }
    }
}

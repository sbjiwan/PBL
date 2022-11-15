package com.example.pbl

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class UserInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_info)

        findViewById<Button>(R.id.edit).setOnClickListener {
            val intent = Intent(this, UserEditActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.shop_list).setOnClickListener {
            val intent = Intent(this, ShopListActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.sns).setOnClickListener {
            val intent = Intent(this, SnsActivity::class.java)
            startActivity(intent)
        }
    }
}
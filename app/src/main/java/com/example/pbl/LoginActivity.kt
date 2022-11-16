package com.example.pbl

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    lateinit var idfield : TextView
    lateinit var passfield : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_in)

        findViewById<Button>(R.id.back).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        findViewById<Button>(R.id.login).setOnClickListener {
            idfield = findViewById<TextView>(R.id.id_field)
            passfield = findViewById<TextView>(R.id.password_field)
            Firebase.auth.signInWithEmailAndPassword( idfield.text.toString(), passfield.text.toString())
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, UserInfoActivity::class.java)
                        startActivity(intent)
                    } else {
                        println("not allow")
                    }
                }
        }
    }
}
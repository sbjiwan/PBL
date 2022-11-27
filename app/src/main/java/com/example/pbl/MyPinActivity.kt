package com.example.pbl

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class MyPinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_pin)

        Firebase.firestore.collection("user_pins").document(Firebase.auth.currentUser?.email.toString()).get().addOnSuccessListener {
            val pinUsers = it["pin_list"] as ArrayList<String>
            for (pinUser in pinUsers) {
                val pinObject = layoutInflater.inflate(R.layout.pin_item, null, false);
                val ref = FirebaseStorage.getInstance().getReference("${pinUser}_profile")
                ref.downloadUrl
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
        findViewById<Button>(R.id.back).setOnClickListener {
            val intent = Intent(this, UserInfoActivity::class.java)
            startActivity(intent)
        }
    }
}
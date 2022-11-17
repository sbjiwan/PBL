package com.example.pbl

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pbl.databinding.LogInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var binding : LogInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LogInBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.login.setOnClickListener {
            val email = binding.Email.text.toString()
            val password = binding.passwordArea.text.toString()

            binding.Email.text.clear()
            binding.passwordArea.text.clear()
            MyApplication.auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){ task->
                    if(task.isSuccessful){
                        val intent = Intent(this, UserInfoActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        Toast.makeText(this,"로그인 성공",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this,"로그인 실패",Toast.LENGTH_SHORT).show()
                    }

                }


        }
    }
}
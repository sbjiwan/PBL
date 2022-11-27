package com.example.pbl

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pbl.databinding.LogInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var binding : LogInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 로그인 버튼

        binding.login.setOnClickListener {
            val id = binding.idInput.text.toString()
            val password = binding.passwordInput.text.toString()

            binding.idInput.text.clear()
            binding.passwordInput.text.clear()
            Firebase.auth.signInWithEmailAndPassword("${id}@sns.com",password)
                .addOnCompleteListener(this){ task->
                    if(task.isSuccessful){
                        val intent = Intent(this, UserInfoActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        Toast.makeText(this,"로그인 성공",Toast.LENGTH_SHORT).show()
                    } else Toast.makeText(this,"로그인 실패",Toast.LENGTH_SHORT).show()
                }
        }
    }
}
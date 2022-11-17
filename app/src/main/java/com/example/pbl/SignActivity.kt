package com.example.pbl

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pbl.databinding.SignInBinding

class SignActivity : AppCompatActivity() {

    lateinit var binding:SignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignInBinding.inflate(layoutInflater)
        setContentView(binding.root)


       /* binding.signIn.setOnClickListener {
            val intent = Intent(this, UserInfoActivity::class.java)
            startActivity(intent)
        }*/

       binding.signIn.setOnClickListener{
           val email = binding.emailEdit.text.toString()
           val password = binding.passwordEdit.text.toString()
           val password2 = binding.passwordConfirmed.text.toString()


                   binding.emailEdit.text.clear()
                   binding.passwordEdit.text.clear()
                     binding.passwordConfirmed.text.clear()
                   var verifiedGo = true
                   if(email.isEmpty()){
                       Toast.makeText(this,"이메일을 입력해주세요",Toast.LENGTH_SHORT).show()
                       verifiedGo = false
                   }
                   if(password.isEmpty()){
                       Toast.makeText(this,"password를 입력해주세요",Toast.LENGTH_SHORT).show()
                       verifiedGo = false
                   }
                   if(password2.isEmpty()){
                       Toast.makeText(this,"password확인을 입력해주세요",Toast.LENGTH_SHORT).show()
                       verifiedGo = false
                   }
                   if(!password.equals(password2)){
                       Toast.makeText(this,"비밀번호를 똑같이 입력해 주세요",Toast.LENGTH_SHORT).show()
                       verifiedGo = false
                   }

                   if(password.length < 6){
                       Toast.makeText(this,"6자리 이상 입력해주세요",Toast.LENGTH_SHORT).show()
                       verifiedGo = false
                   }


                   if(verifiedGo){
                       MyApplication.auth.createUserWithEmailAndPassword(email,password)
                           .addOnCompleteListener(this){ task->
                               if(task.isSuccessful){
                                   Toast.makeText(this,"가입되었습니다.",Toast.LENGTH_SHORT).show()

                                   val intent = Intent(this, LoginActivity::class.java)
                                   intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                   startActivity(intent)
                               }else{
                                   Toast.makeText(this,"가입 실패하였습니다. 이메일 형식으로 써주세요.",Toast.LENGTH_SHORT).show()
                               }
                   }
               }
       }
    }
}

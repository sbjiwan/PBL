package com.example.pbl

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pbl.databinding.SignInBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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
           val id = binding.idEdit.text.toString()
           val password = binding.passwordEdit.text.toString()
           val password2 = binding.passwordConfirmed.text.toString()


                   binding.idEdit.text.clear()
                   binding.passwordEdit.text.clear()
                     binding.passwordConfirmed.text.clear()
                   var verifiedGo = true
                   if(id.isEmpty() || 20 < id.length){
                       Toast.makeText(this,"올바른 id를 입력해주세요",Toast.LENGTH_SHORT).show()
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
                       val pinmap = hashMapOf(
                           "my_pin" to ArrayList<String>(),
                           "who_pin" to ArrayList<String>()
                       )
                       MyApplication.auth.createUserWithEmailAndPassword("${id}@sns.com",password)
                           .addOnCompleteListener(this){ task->
                               if(task.isSuccessful){
                                   Firebase.firestore.collection("user_pins").document("${id}@sns.com").set(pinmap)
                                   Toast.makeText(this,"가입되었습니다.",Toast.LENGTH_SHORT).show()
                                   val intent = Intent(this, LoginActivity::class.java)
                                   intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                   startActivity(intent)
                               }else{
                                   Toast.makeText(this,"가입 실패하였습니다. 올바른 아이디 형식으로 써주세요.",Toast.LENGTH_SHORT).show()
                               }
                   }
               }
       }
    }
}

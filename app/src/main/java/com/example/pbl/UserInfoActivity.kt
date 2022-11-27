package com.example.pbl

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pbl.Utils.FirebaseUtil
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.ktx.storage
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream
import kotlin.properties.Delegates


class UserInfoActivity : AppCompatActivity() {
    private val util = FirebaseUtil()
    private lateinit var userEmail : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_info)

        // lateinit 변수 초기화

        userEmail =
            if (intent.getStringExtra("user") == null) util.currentUser
            else intent.getStringExtra("user")!!

        // 로그아웃 버튼
        
        findViewById<Button>(R.id.logout).setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this,MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        
        // 회원 탈퇴 버튼

        findViewById<Button>(R.id.withdraw).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("경고")
                .setMessage("정말로 탈퇴하시겠습니까? (한번 결정한 내용은 되돌릴 수 없습니다.)")
                
                // 회원 탈퇴 확인 후 데이터 삭제 작업
                    
                .setPositiveButton("네") { dialogInterface: DialogInterface, i: Int ->
                    util.posts.get().addOnSuccessListener {
                        Toast.makeText(this, "'정상적으로 탈퇴되었습니다'라는 문구가 안나오면, 재 로그인 후 탈퇴를 다시 진행해주세요.", Toast.LENGTH_SHORT).show()
                        for (data in it) {
                            if (data["author"].toString() == util.currentUser) data.reference.delete()
                            else {
                                val comments = data["comment"] as ArrayList<MutableMap<String, String>>
                                for (comment in comments) if (comment["author"].toString() == util.currentUser) comments.remove(comment)
                                data.reference.update(hashMapOf<String, Any>(
                                    "author" to data.data["author"] as String,
                                    "post_name" to data.data["post_name"] as String,
                                    "post_main" to data.data["post_main"] as String,
                                    "time" to data.data["time"] as String,
                                    "comment" to comments
                                ))
                            }
                        }
                    }
                    
                    // 유저 정보 삭제
                    
                    util.pins.get().addOnSuccessListener {
                        for (data in it) {
                            val pinedData = data["pined_list"] as ArrayList<String>
                            for (pinedQuery in pinedData) if (pinedQuery == util.currentUser) pinedData.remove(pinedQuery)
                            data.reference.set(hashMapOf(
                                "pin_list" to data["pin_list"] as ArrayList<String>,
                                "pined_list" to pinedData
                            ))
                        }
                    }
                    util.pins.document(util.currentUser).delete()
                    util.instance.reference.child(util.currentUser + "_profile").delete()
                    FirebaseAuth.getInstance().currentUser!!.delete().addOnSuccessListener {
                        Toast.makeText(this, "정상적으로 탈퇴되었습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this,MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
                .setNegativeButton("아니오") { dialogInterface: DialogInterface, i: Int -> }
                .show()
        }

        // 권한에 따른 레이아웃 처리

        util.pins.document(userEmail).get().addOnSuccessListener {integrity ->

            // 회원 무결성이 잘못되었을 때

            if (integrity.data == null && userEmail == util.currentUser) {
                Toast.makeText(this, "회원 탈퇴를 마저 진행해주세요.", Toast.LENGTH_SHORT).show()
                findViewById<Button>(R.id.handler).visibility = View.GONE
                findViewById<Button>(R.id.imageSave).visibility = View.GONE
                findViewById<Button>(R.id.my_pin).visibility = View.GONE
                findViewById<Button>(R.id.sns).visibility = View.GONE
                
            // 회원 무결성 점검 완료
                
            } else {
                
                // 유저 프로필 동기화
                
                util.instance.getReference(userEmail + "_profile")
                    .downloadUrl
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if(task.isSuccessful){
                            Glide.with(this)
                                .load(task.result)
                                .into(findViewById(R.id.imageView))
                        } else Toast.makeText(this,"먼저 유저 프로필을 설정해주세요.",Toast.LENGTH_LONG).show()
                    })
                
                // 유저 게시물 동기화
                
                util.posts.orderBy("time", Query.Direction.DESCENDING).get().addOnSuccessListener {
                    for (data in it) {
                        if (data["author"] == userEmail) {
                            val post = layoutInflater.inflate(R.layout.post_item, null, false);

                            // 게시물 프로필

                            util.instance.getReference(data["author"].toString() + "_profile")
                                .downloadUrl
                                .addOnCompleteListener(OnCompleteListener { task ->
                                    if(task.isSuccessful){
                                        Glide.with(this)
                                            .load(task.result)
                                            .into(post.findViewById(R.id.user_profile))
                                    }
                                })

                            // 게시물 이동 기능

                            post.setOnClickListener {
                                val intent = Intent(this, UserPostActivity::class.java)
                                intent.putExtra("uid", data.id)
                                startActivity(intent)
                            }

                            // 게시물 정보 동기화
                            
                            post.findViewById<TextView>(R.id.username).text = data["author"].toString();
                            post.findViewById<TextView>(R.id.post_title).text = data["post_name"].toString()
                            post.findViewById<TextView>(R.id.post_main).text = data["post_main"].toString()
                            post.findViewById<TextView>(R.id.post_date).text = data["time"].toString()//시간 추가
                            //  post.findViewById<TextView>(R.id.post_category).setText("카테고리: ${data["postcategory"].toString()}")
                            findViewById<LinearLayout>(R.id.my_post).addView(post)
                        }
                    }
                }

                // 유저 정보 동기화

                findViewById<TextView>(R.id.userid).text = userEmail
                val pinedList = integrity["pined_list"] as ArrayList<String>
                findViewById<TextView>(R.id.userpined).text = "핀 받은 수 : ${pinedList.size}"
                
                // 유저 정보창을 보고 있는 유저가 유저 정보창의 유저가 아닐 때
                
                if (userEmail != util.currentUser) {

                    // 핸들러 버튼 text 변경 및 핸들러 버튼 기능 설정 (핀)
                    
                    findViewById<Button>(R.id.handler).text = "핀 하기 / 풀기"
                    findViewById<Button>(R.id.handler).setOnClickListener {
                        val pinInfo = util.pins.document(util.currentUser)
                        val tPinInfo = util.pins.document(userEmail)
                        
                        // 유저 정보창을 보고 있는 유저의 핀 수정

                        pinInfo.get().addOnSuccessListener {
                            val myPinInfo = it["pin_list"] as ArrayList<String>
                            if (myPinInfo.find { field -> field == userEmail } == null)  myPinInfo.add(userEmail)
                            else myPinInfo.remove(userEmail)
                            pinInfo.set(hashMapOf(
                                "pin_list" to myPinInfo,
                                "pined_list" to it["pined_list"] as ArrayList<String>
                            ))
                            finish()
                            startActivity(intent)
                        }

                        // 유저 정보창의 유저의 받은 핀 수정

                        tPinInfo.get().addOnSuccessListener {
                            val targetPinInfo = it["pined_list"] as ArrayList<String>
                            if (targetPinInfo.find { field -> field == util.currentUser } == null) {
                                targetPinInfo.add(util.currentUser)
                                Toast.makeText(this, "해당 유저를 핀 하였습니다.", Toast.LENGTH_SHORT).show()
                            } else {
                                targetPinInfo.remove(util.currentUser)
                                Toast.makeText(this, "해당 유저를 핀 풀었습니다.", Toast.LENGTH_SHORT).show()
                            }
                            tPinInfo.set(hashMapOf(
                                "pin_list" to it["pin_list"] as ArrayList<String>,
                                "pined_list" to targetPinInfo
                            ))
                        }
                    }

                    // 내 정보 버튼 활성화

                    findViewById<Button>(R.id.user_info).setOnClickListener {
                        val intent = Intent(this, UserInfoActivity::class.java)
                        startActivity(intent)
                    }

                    // 권한을 보유하지 않은 버튼 숨김 처리
                    
                    findViewById<Button>(R.id.imageSave).visibility = View.GONE
                    findViewById<Button>(R.id.logout).visibility = View.GONE
                    findViewById<Button>(R.id.withdraw).visibility = View.GONE
                    findViewById<Button>(R.id.my_pin).visibility = View.GONE

                // 유저 정보창을 보고 있는 유저가 유저 정보창의 유저일 때

                } else {

                    // 핸들러 버튼 기능 설정 (프로필 변경)

                    findViewById<Button>(R.id.handler).setOnClickListener {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                                requestPermissions(permissions, PERMISSION_CODE);
                            } else pickImageFromGallery();
                        } else pickImageFromGallery();
                    }
                    
                    // 프로필 저장 버튼

                    findViewById<Button>(R.id.imageSave).setOnClickListener {
                        val bitmap =  (findViewById<ImageView>(R.id.imageView).drawable as BitmapDrawable).bitmap
                        val baos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val data = baos.toByteArray()
                        util.instance.reference.child(util.currentUser + "_profile").putBytes(data)
                            .addOnFailureListener {
                                Toast.makeText(this, "업로드 실패", Toast.LENGTH_LONG).show()
                            }.addOnSuccessListener { taskSnapshot ->
                                Toast.makeText(this, "업로드 성공", Toast.LENGTH_LONG).show()
                            }
                    }
                    
                    // 내 핀 정보 버튼

                    findViewById<Button>(R.id.my_pin).setOnClickListener {
                        val intent = Intent(this, MyPinActivity::class.java)
                        startActivity(intent)
                    }
                }
                
                // sns 버튼

                findViewById<Button>(R.id.sns).setOnClickListener {
                    val intent = Intent(this, SnsActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
    }

    //handle requested permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size >0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    pickImageFromGallery()
                }
                else{
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            findViewById<ImageView>(R.id.imageView).setImageURI(data?.data)
        }
    }
}
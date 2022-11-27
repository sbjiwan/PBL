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


class UserInfoActivity : AppCompatActivity() {
    private lateinit var userEmail : String

    val storage = Firebase.storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_info)

        if (intent.getStringExtra("user") == null) userEmail = Firebase.auth.currentUser?.email.toString()
        else userEmail = intent.getStringExtra("user")!!

        Firebase.firestore.collection("user_pins").document(userEmail).get().addOnSuccessListener {
            if (it.data != null) {
                val pinedList = it["pined_list"] as ArrayList<String>
                findViewById<TextView>(R.id.userpined).text = "핀 받은 수 : ${pinedList.size}"
            }
        }

        val ref = FirebaseStorage.getInstance().getReference(userEmail + "_profile")

        ref.downloadUrl
            .addOnCompleteListener(OnCompleteListener { task ->
                if(task.isSuccessful){
                    Glide.with(this)
                        .load(task.result)
                        .into(findViewById(R.id.imageView))
                } else {
                    Toast.makeText(this,"먼저 유저 프로필을 설정해주세요.",Toast.LENGTH_LONG).show()
                }
            })

        findViewById<TextView>(R.id.userid).text = userEmail
        Firebase.firestore.collection("post_list").orderBy("time", Query.Direction.DESCENDING).get().addOnSuccessListener {
            for (data in it) {
                if (data["author"] == userEmail) {
                    val post = layoutInflater.inflate(R.layout.post_item, null, false);

                    val ref = FirebaseStorage.getInstance().getReference(data["author"].toString() + "_profile")

                    ref.downloadUrl
                        .addOnCompleteListener(OnCompleteListener { task ->
                            if(task.isSuccessful){
                                Glide.with(this)
                                    .load(task.result)
                                    .into(post.findViewById(R.id.user_profile))
                            }
                        })
                    post.setOnClickListener {
                        val intent = Intent(this, UserPostActivity::class.java)
                        intent.putExtra("uid", data.id)
                        startActivity(intent)
                    }
                    post.findViewById<TextView>(R.id.username).text = data["author"].toString();
                    post.findViewById<TextView>(R.id.post_title).text = data["post_name"].toString()
                    post.findViewById<TextView>(R.id.post_main).text = data["post_main"].toString()
                    post.findViewById<TextView>(R.id.post_date).text = data["time"].toString()//시간 추가
                    //  post.findViewById<TextView>(R.id.post_category).setText("카테고리: ${data["postcategory"].toString()}")
                    findViewById<LinearLayout>(R.id.my_post).addView(post)
                }
            }
        }
        if (userEmail != Firebase.auth.currentUser?.email.toString()) {
            findViewById<Button>(R.id.handler).text = "핀 하기 / 풀기"
            findViewById<Button>(R.id.handler).setOnClickListener {
                val pinInfo = Firebase.firestore.collection("user_pins").document(Firebase.auth.currentUser?.email.toString())
                val tPinInfo = Firebase.firestore.collection("user_pins").document(userEmail)
                pinInfo.get().addOnSuccessListener {
                    val myPinInfo = it["pin_list"] as ArrayList<String>
                    if (myPinInfo.find { field -> field == userEmail } == null)  myPinInfo.add(userEmail)
                    else myPinInfo.remove(userEmail)
                    pinInfo.set(hashMapOf(
                        "pin_list" to myPinInfo,
                        "pined_list" to it["pined_list"] as ArrayList<String>
                    ))
                    finish();
                    startActivity(intent)
                }
                tPinInfo.get().addOnSuccessListener {
                    val targetPinInfo = it["pined_list"] as ArrayList<String>
                    if (targetPinInfo.find { field -> field == Firebase.auth.currentUser!!.email.toString() } == null) {
                        targetPinInfo.add(Firebase.auth.currentUser!!.email.toString())
                        Toast.makeText(this, "해당 유저를 핀 하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        targetPinInfo.remove(Firebase.auth.currentUser!!.email.toString())
                        Toast.makeText(this, "해당 유저를 핀 풀었습니다.", Toast.LENGTH_SHORT).show()
                    }
                    tPinInfo.set(hashMapOf(
                        "pin_list" to it["pin_list"] as ArrayList<String>,
                        "pined_list" to targetPinInfo
                    ))
                }
                finish();
                startActivity(intent)
            }
            findViewById<Button>(R.id.imageSave).visibility = View.GONE
            findViewById<Button>(R.id.logout).visibility = View.GONE
            findViewById<Button>(R.id.withdraw).visibility = View.GONE
            findViewById<Button>(R.id.my_pin).visibility = View.GONE
            findViewById<Button>(R.id.user_info).setOnClickListener {
                val intent = Intent(this, UserInfoActivity::class.java)
                startActivity(intent)
            }
        } else {
            findViewById<Button>(R.id.handler).setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED
                    ) {
                        //permission denied
                        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                        //show popup to request runtime permission
                        requestPermissions(permissions, PERMISSION_CODE);
                    } else {
                        //permission already granted
                        pickImageFromGallery();
                    }
                } else {
                    //system OS is < Marshmallow
                    pickImageFromGallery();
                }
            }

            findViewById<Button>(R.id.imageSave).setOnClickListener {
                val bitmap =  (findViewById<ImageView>(R.id.imageView).drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                var uploadTask = FirebaseStorage.getInstance().getReference().child(Firebase.auth.currentUser?.email.toString() + "_profile").putBytes(data)
                uploadTask
                    .addOnFailureListener {
                        Toast.makeText(this, "업로드 실패", Toast.LENGTH_LONG).show()
                    }.addOnSuccessListener { taskSnapshot ->
                        Toast.makeText(this, "업로드 성공", Toast.LENGTH_LONG).show()
                    }
            }

            findViewById<Button>(R.id.logout).setOnClickListener {
                Firebase.auth.signOut()
                val intent = Intent(this,MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

            findViewById<Button>(R.id.withdraw).setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("경고")
                    .setMessage("정말로 탈퇴하시겠습니까? (한번 결정한 내용은 되돌릴 수 없습니다.)")
                    .setPositiveButton("네") { dialogInterface: DialogInterface, i: Int ->
                        Firebase.firestore.collection("post_list").get().addOnSuccessListener {
                            Toast.makeText(this, "만약, '정상적으로 탈퇴되었습니다'라는 문구가 안나온다면, 재 로그인 후 탈퇴를 다시 진행해주세요ㅁ.", Toast.LENGTH_SHORT).show()
                            for (data in it) {
                                if (data["author"].toString() == Firebase.auth.currentUser?.email.toString()) data.reference.delete()
                                else {
                                    val comments = data["comment"] as ArrayList<MutableMap<String, String>>
                                    for (comment in comments) {
                                        if (comment["author"].toString() == Firebase.auth.currentUser?.email.toString()) comments.remove(comment)
                                    }
                                    val postInfo = mutableMapOf<String, Any>(
                                        "author" to data.data["author"] as String,
                                        "post_name" to data.data["post_name"] as String,
                                        "post_main" to data.data["post_main"] as String,
                                        "time" to data.data["time"] as String,
                                        "comment" to comments
                                    )
                                    data.reference.update(postInfo)
                                }
                            }
                        }
                        Firebase.firestore.collection("user_pins").get().addOnSuccessListener {
                            for (data in it) {
                                val pinedData = data["pined_list"] as ArrayList<String>
                                for (pinedQuery in pinedData) if (pinedQuery == Firebase.auth.currentUser?.email.toString()) pinedData.remove(pinedQuery)
                                data.reference.set(hashMapOf(
                                    "pin_list" to data["pin_list"] as ArrayList<String>,
                                    "pined_list" to pinedData
                                ))
                            }
                        }
                        Firebase.firestore.collection("user_pins").document(Firebase.auth.currentUser?.email.toString()).delete()
                        FirebaseStorage.getInstance().getReference().child(Firebase.auth.currentUser?.email.toString() + "_profile").delete()
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
            findViewById<Button>(R.id.my_pin).setOnClickListener {
                val intent = Intent(this, MyPinActivity::class.java)
                startActivity(intent)
            }
        }

        findViewById<Button>(R.id.sns).setOnClickListener {
            Firebase.firestore.collection("user_pins").document(Firebase.auth.currentUser?.email.toString()).get().addOnSuccessListener {
                if (it.data == null) {
                    Toast.makeText(this, "삭제중인 계정임을 확인. 삭제를 재진행합니다.", Toast.LENGTH_SHORT).show()
                    FirebaseAuth.getInstance().currentUser!!.delete().addOnSuccessListener {
                        Toast.makeText(this, "정상적으로 탈퇴되었습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this,MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                } else {
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
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            findViewById<ImageView>(R.id.imageView).setImageURI(data?.data)
        }
    }


}



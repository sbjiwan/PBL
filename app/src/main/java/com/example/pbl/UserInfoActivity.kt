package com.example.pbl

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


class UserInfoActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    val storage = Firebase.storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_info)

        auth = Firebase.auth
        val userdb = Firebase.firestore.collection("user_info")
        findViewById<TextView>(R.id.userid).setText(auth.currentUser?.email.toString())
        Firebase.firestore.collection("post_list").orderBy("time", Query.Direction.DESCENDING).get().addOnSuccessListener {
            for (data in it) {
                if (data["author"] == Firebase.auth.currentUser?.email.toString()) {
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




        findViewById<Button>(R.id.edit_profile).setOnClickListener {

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

        //이미지 다운로드
        val ref = FirebaseStorage.getInstance().getReference(Firebase.auth.currentUser?.email.toString() + "_profile")

        ref.downloadUrl
            .addOnCompleteListener(OnCompleteListener { task ->
                if(task.isSuccessful){
                    Glide.with(this)
                        .load(task.result)
                        .into(findViewById(R.id.imageView))
                }else{
                    Toast.makeText(this,"먼저 유저 프로필을 설정해주세요.",Toast.LENGTH_LONG).show()
                }
            })


        findViewById<Button>(R.id.imageSave).setOnClickListener {
                //check runtime permission

                //이미지 업로드
                // Get the data from an ImageView as bytes
                //val mountainsRef =  FirebaseStorage.getInstance().getReference().child("mountain.jpg")

                val bitmap =
                    (findViewById<ImageView>(R.id.imageView).drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                var uploadTask = FirebaseStorage.getInstance().getReference()
                    .child(Firebase.auth.currentUser?.email.toString() + "_profile").putBytes(data)
                uploadTask.addOnFailureListener {
                    Toast.makeText(this, "업로드 실패", Toast.LENGTH_LONG).show()

                    // Handle unsuccessful uploads
                }.addOnSuccessListener { taskSnapshot ->
                    Toast.makeText(this, "업로드 성공", Toast.LENGTH_LONG).show()
                    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                    // ...
                }

            }







        findViewById<Button>(R.id.sns).setOnClickListener {
            val intent = Intent(this, SnsActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.logout).setOnClickListener {
            auth.signOut()
            val intent = Intent(this,MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }


    //->프로필 작성
   /*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(resultCode,resultCode,data)
        if(resultCode == RESULT_OK && requestCode == 100){
            findViewById<ImageView>(R.id.imageView).setImageURI(data?.data)
        }
    }
*/

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



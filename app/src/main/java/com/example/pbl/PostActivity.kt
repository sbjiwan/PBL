package com.example.pbl

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pbl.util.FirebaseUtil
import com.google.android.gms.tasks.OnCompleteListener
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class PostActivity : AppCompatActivity() {
    private val util = FirebaseUtil()
    private var statePort by Delegates.notNull<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_add)

        // lateinit 변수 초기화

        statePort =
            if (intent.getStringExtra("uid") == null) 0;
            else {
                util.posts.document(intent.getStringExtra("uid")!!).get().addOnSuccessListener {
                    findViewById<EditText>(R.id.namefield).setText(it["post_name"].toString())
                    findViewById<EditText>(R.id.mainfield).setText(it["post_main"].toString())
                    val tester = resources.getStringArray(R.array.post_category).toCollection(ArrayList<String>())
                    findViewById<Spinner>(R.id.post_category).setSelection(tester.indexOf(it["post_category"].toString()))

                    // 게시글의 이미지가 있는지 확인

                    util.instance.getReference(intent.getStringExtra("uid")+ "_image")
                        .downloadUrl
                        .addOnCompleteListener(OnCompleteListener { task ->
                            if(task.isSuccessful){
                                Glide.with(this)
                                    .load(task.result)
                                    .into(findViewById(R.id.imageSave))
                            }
                        })
                }
                1
        }

        // 취소 버튼

        findViewById<Button>(R.id.cancel).setOnClickListener {
            val intent = Intent(this, SnsActivity::class.java)
            startActivity(intent)
        }

        var existPicture : Boolean = false;
        // 사진 가져오기

        findViewById<ImageView>(R.id.imageSave).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                    requestPermissions(permissions, PERMISSION_CODE);
                } else pickImageFromGallery()
            } else pickImageFromGallery()
        }
        
        // 사진 삭제하기
        findViewById<Button>(R.id.imageDelete).setOnClickListener {
            findViewById<ImageView>(R.id.imageSave).setImageDrawable(getDrawable(R.drawable.get_photo))
        }

        // 카테고리 스피너
        
        findViewById<Spinner>(R.id.post_category).adapter =
            ArrayAdapter.createFromResource(this, R.array.post_category,android.R.layout.simple_spinner_item)

        // 게시 버튼

        findViewById<Button>(R.id.post_add).setOnClickListener {
            val postname = findViewById<EditText>(R.id.namefield).text.toString()
            val postmain = findViewById<EditText>(R.id.mainfield).text.toString()
            if (postname.isNotEmpty() && postmain.isNotEmpty()) {
                val hashMap = hashMapOf<String, Any>(
                    "author" to util.currentUser,
                    "post_name" to postname,
                    "post_main" to postmain,
                    "post_category" to findViewById<Spinner>(R.id.post_category).selectedItem.toString(),
                    "time" to getTime(),
                    "comment" to ArrayList<MutableMap<String, String>>()
                )

                // 사진 데이터 올리기

                if (statePort == 0) {
                    util.posts.add(hashMap).addOnSuccessListener {document->
                        val bitmap =  (findViewById<ImageView>(R.id.imageSave).drawable as BitmapDrawable).bitmap
                        val baos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val data = baos.toByteArray()
                        util.instance.reference.child(document.id + "_image").putBytes(data)
                            .addOnFailureListener {
                                Toast.makeText(this, "업로드 실패", Toast.LENGTH_LONG).show()
                            }.addOnSuccessListener { taskSnapshot ->
                                Toast.makeText(this, "업로드 성공", Toast.LENGTH_LONG).show()
                            }
                    }
                }
                else {
                    util.posts.document(intent.getStringExtra("uid")!!).update(hashMap)
                    val bitmap =  (findViewById<ImageView>(R.id.imageSave).drawable as BitmapDrawable).bitmap
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    util.instance.reference.child(intent.getStringExtra("uid")!! + "_image").putBytes(data)
                        .addOnFailureListener {
                            Toast.makeText(this, "업로드 실패", Toast.LENGTH_LONG).show()
                        }.addOnSuccessListener { taskSnapshot ->
                            Toast.makeText(this, "업로드 성공", Toast.LENGTH_LONG).show()
                        }
                }
                val intent = Intent(this, SnsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else Toast.makeText(this, "게시글 이름이나 본문이 비어있을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object {
        //image pick code
        private const val IMAGE_PICK_CODE = 1000;
        //Permission code
        private const val PERMISSION_CODE = 1001;
    }

    private fun getTime(): String {
        val currentDayAndTime = Calendar.getInstance().time
        return SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA).format(currentDayAndTime)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            findViewById<ImageView>(R.id.imageSave).setImageURI(data?.data)
        }
    }
}
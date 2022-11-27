package com.example.pbl.Utils

import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pbl.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class FirebaseUtil() {
    private val currentUser = Firebase.auth.currentUser?.email.toString()
    private val storage = Firebase.firestore
    fun profileLoad(activity : AppCompatActivity) {

    }
}
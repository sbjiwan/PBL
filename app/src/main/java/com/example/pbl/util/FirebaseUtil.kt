package com.example.pbl.util

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class FirebaseUtil {
    val currentUser = Firebase.auth.currentUser!!.email.toString()
    val instance = FirebaseStorage.getInstance()
    val appData = Firebase.firestore.collection("app_data")
    val posts = Firebase.firestore.collection("post_list")
    val pins = Firebase.firestore.collection("user_pins")
}
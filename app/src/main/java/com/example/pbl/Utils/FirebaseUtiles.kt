package com.example.pbl.Utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class FirebaseUtiles {

    companion object {

        private var auth:FirebaseAuth=FirebaseAuth.getInstance()
        var db : FirebaseFirestore = FirebaseFirestore.getInstance()

        fun getUid():String{
            return auth.currentUser?.uid.toString()
        }

    }

}
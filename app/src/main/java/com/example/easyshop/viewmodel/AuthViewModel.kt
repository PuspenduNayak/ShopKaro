package com.example.easyshop.viewmodel

import androidx.lifecycle.ViewModel
import com.example.easyshop.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class AuthViewModel : ViewModel() {

    private val auth = Firebase.auth
    private val fireStore = Firebase.firestore

    fun login(
        email: String,
        password: String,
        onResult: (UserModel?, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid
                    if (userId != null) {
                        fireStore.collection("users").document(userId)
                            .get()
                            .addOnSuccessListener { document ->
                                val userModel = document.toObject(UserModel::class.java)
                                onResult(userModel, null)
                            }
                            .addOnFailureListener { exception ->
                                onResult(null, exception.localizedMessage)
                            }
                    }
                } else {
                    onResult(null, task.exception?.localizedMessage)
                }
            }
    }

    fun signup(
        email: String,
        name: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val userId = it.result?.user?.uid
                    val userModel = UserModel(userId!!, email, name, admin = false)
                    fireStore.collection("users").document(userId)
                        .set(userModel)
                        .addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                onResult(true, null)
                            } else {
                                onResult(false, "Something went wrong")
                            }
                        }
                } else {
                    onResult(false, it.exception?.localizedMessage)
                }
            }
    }
}
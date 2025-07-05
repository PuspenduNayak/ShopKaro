package com.example.easyshop.model


data class UserModel(
    val uid : String = "",
    val email : String = "",
    val name : String = "",
    val cartItems : Map<String, Long> = emptyMap(),
    val address: String = ""
)

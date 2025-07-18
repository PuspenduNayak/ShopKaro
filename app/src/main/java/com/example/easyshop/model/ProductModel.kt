package com.example.easyshop.model

data class ProductModel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val price: String = "",
    val actualPrice: String = "",
    val category: String = "",
    val images: List<String> = emptyList(),
    val otherDetails : Map<String, String> = emptyMap()
)

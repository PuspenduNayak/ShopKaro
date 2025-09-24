package com.example.easyshop.pages

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.unit.dp
import com.example.easyshop.component.ProductItemView
import com.example.easyshop.model.CategoryModel
import com.example.easyshop.model.ProductModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun CategoryProductsPage(modifier: Modifier = Modifier, categoryId: String) {
    val productList = remember {
        mutableStateOf<List<ProductModel>>(emptyList())
    }

    LaunchedEffect(Unit) {
        Firebase.firestore.collection("data")
            .document("stock")
            .collection("products")
            .whereEqualTo("category", categoryId)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val resultList = it.result.documents.mapNotNull { doc ->
                        doc.toObject(ProductModel::class.java)
                    }
                    productList.value = resultList
                }
            }
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, 50.dp)
        ) {
            items(productList.value.chunked(2)) { rowItem ->
                Row {
                    rowItem.forEach {
                        ProductItemView(Modifier.weight(1f), it)
                    }
                    if (rowItem.size == 1) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
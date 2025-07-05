package com.example.easyshop.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.easyshop.component.BannerView
import com.example.easyshop.component.CategoriesView
import com.example.easyshop.component.HeaderView
import com.example.easyshop.component.ProductItemView
import com.example.easyshop.model.ProductModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun HomPage(modifier: Modifier = Modifier) {

    val productList = remember {
        mutableStateOf<List<ProductModel>>(emptyList())
    }

    LaunchedEffect(Unit) {
        Firebase.firestore.collection("data")
            .document("stock")
            .collection("products")
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val resultList = it.result.documents.mapNotNull { doc ->
                        doc.toObject(ProductModel::class.java)
                    }
                    productList.value = resultList
                }
            }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        HeaderView(modifier)
        Spacer(Modifier.height(10.dp))
        BannerView(modifier = Modifier.height(130.dp))
        Spacer(Modifier.height(10.dp))
        CategoriesView(modifier)


        //Rest of the home pages
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // This makes LazyColumn take remaining space
                .padding(5.dp)
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
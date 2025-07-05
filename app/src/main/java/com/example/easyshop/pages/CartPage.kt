package com.example.easyshop.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.easyshop.GlobalNavigation
import com.example.easyshop.component.CartItemView
import com.example.easyshop.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@Composable
fun CartPage(modifier: Modifier = Modifier) {

    val userModel = remember {
        mutableStateOf(UserModel())
    }

    DisposableEffect(Unit) {
        var listener = Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)
            .addSnapshotListener { it, _ ->
                if (it != null) {
                    val result = it.toObject(UserModel::class.java)
                    if (result != null) {
                        userModel.value = result
                    }
                }
            }
        onDispose {
            listener.remove()
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Your Cart", style = TextStyle(
                fontSize = 22.sp, fontWeight = FontWeight.Bold
            )
        )
        Spacer(Modifier.height(10.dp))
        if (userModel.value.cartItems.isEmpty()) {
            Column (
                modifier = Modifier
                    .fillMaxSize(), // Fill the available space
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Cart is Empty",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 32.sp,
                    color = Color.Gray
                )
                Spacer(Modifier.height(8.dp))
                IconButton(onClick = {
                    GlobalNavigation.navController.navigate("home")
                }) {
                    Icon(Icons.Default.ShoppingCart, "Remove from cart")
                }
            }

        } else {
            LazyColumn(
                Modifier.weight(1f)
            ) {
                items(userModel.value.cartItems.toList(), key = {it.first}) { (productId, qty) ->
                    CartItemView(Modifier, productId, qty)
                }
            }
            Button(
                onClick = {
                    GlobalNavigation.navController.navigate("checkout")
                },
                Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Checkout", fontSize = 16.sp)
            }
        }

    }
}
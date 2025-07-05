package com.example.easyshop.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.easyshop.AppUtil
import com.example.easyshop.GlobalNavigation
import com.example.easyshop.model.ProductModel
import com.example.easyshop.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@Composable
fun CheckOutPage(modifier: Modifier = Modifier) {

    val userModel = remember {
        mutableStateOf(UserModel())
    }

    val productList = remember {
        mutableStateListOf(ProductModel())
    }

    val subTotal = remember {
        mutableFloatStateOf(0f)
    }

    val discount = remember {
        mutableFloatStateOf(0f)
    }

    val tax = remember {
        mutableFloatStateOf(0f)
    }

    val total = remember {
        mutableFloatStateOf(0f)
    }


    fun calculateAndAssign() {
        productList.forEach {
            if (it.actualPrice.isNotEmpty()) {
                val qty = userModel.value.cartItems[it.id] ?: 0
                subTotal.floatValue += it.actualPrice.toFloat() * qty
            }
        }

        discount.floatValue = subTotal.floatValue * (AppUtil.getDiscountPercentage()) / 100
        tax.floatValue = subTotal.floatValue * (AppUtil.getTaxPercentage()) / 100

        total.floatValue =
            "%.2f".format(subTotal.floatValue - discount.floatValue + tax.floatValue).toFloat()

    }

    LaunchedEffect(Unit) {
        Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().uid!!)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val result = it.result.toObject(UserModel::class.java)
                    if (result != null) {
                        userModel.value = result

                        Firebase.firestore.collection("data")
                            .document("stock").collection("products")
                            .whereIn("id", userModel.value.cartItems.keys.toList())
                            .get().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val resultProduct =
                                        task.result.toObjects(ProductModel::class.java)
                                    productList.addAll(resultProduct)
                                    calculateAndAssign()
                                }
                            }
                    }
                }
            }
    }

    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp, 50.dp)
    ) {
        Text("Checkout", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        Text("Deliver to : ", fontWeight = FontWeight.Bold)
        Text(text = userModel.value.name)
        Text(text = userModel.value.address)

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        RowCheckoutItems("Subtotal", subTotal.floatValue.toString())
        Spacer(Modifier.height(8.dp))
        RowCheckoutItems("Discount (-)", discount.floatValue.toString())
        Spacer(Modifier.height(8.dp))
        RowCheckoutItems("Tax (+)", tax.floatValue.toString())

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "To Pay",
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "₹" + total.floatValue.toString(),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                AppUtil.startPayment(total.floatValue)
            },
            Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Pay Now", fontSize = 16.sp)
        }

    }
}

@Composable
fun RowCheckoutItems(title: String, value: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Text("₹$value", fontSize = 18.sp)
    }
}
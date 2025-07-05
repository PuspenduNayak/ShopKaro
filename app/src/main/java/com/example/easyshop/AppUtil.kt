package com.example.easyshop

import android.app.Activity
import android.widget.Toast
import android.content.Context
import com.example.easyshop.model.OrderModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.razorpay.Checkout
import org.json.JSONObject
import java.util.UUID

object AppUtil {
    fun showToast(context: Context, message: String, shorT: Boolean = false) {
        if (shorT)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun addItemToCart(productId: String, context: Context) {
        val userDoc = Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)

        userDoc.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val currentCart = it.result.get("cartItems") as? Map<String, Long> ?: emptyMap()
                val currentQuantity = currentCart[productId] ?: 0
                val updatedQuantity = currentQuantity + 1

                val updatedCart = mapOf("cartItems.$productId" to updatedQuantity)

                userDoc.update(updatedCart)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            showToast(context, "Item added to the cart", true)
                        } else {
                            showToast(context, "Failed adding item to the cart", true)
                        }
                    }
            }
        }
    }

    fun removeFromCart(productId: String, context: Context, removeAll: Boolean = false) {
        val userDoc = Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)

        userDoc.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val currentCart = it.result.get("cartItems") as? Map<String, Long> ?: emptyMap()
                val currentQuantity = currentCart[productId] ?: 0
                val updatedQuantity = currentQuantity - 1

                val updatedCart =
                    if (updatedQuantity <= 0 || removeAll)
                        mapOf("cartItems.$productId" to FieldValue.delete())
                    else
                        mapOf("cartItems.$productId" to updatedQuantity)

                userDoc.update(updatedCart)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            showToast(context, "Item removed to the cart", true)
                        } else {
                            showToast(context, "Failed removing item to the cart", true)
                        }
                    }
            }
        }
    }

    fun clearCartAndAddToOrder() {
        val userDoc = Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)

        userDoc.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val currentCart = it.result.get("cartItems") as? Map<String, Long> ?: emptyMap()

                val order = OrderModel(
                    "ORD_" + UUID.randomUUID().toString().replace("-", "").take(10).uppercase(),
                    Timestamp.now(),
                    userDoc.id,
                    currentCart,
                    "ORDERED",
                    it.result.get("address") as String
                )

                Firebase.firestore.collection("orders")
                    .document(order.id).set(order)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            userDoc.update("cartItems", FieldValue.delete())
                        }
                    }
            }
        }
    }


    fun getDiscountPercentage(): Float {
        return 10.0f
    }

    fun getTaxPercentage(): Float {
        return 28.0f
    }


    fun razorpayApiKey(): String {
        return "rzp_test_TKVgN4rJgtavh1"
    }

    fun startPayment(amount: Float) {
        val checkOut = Checkout()
        checkOut.setKeyID(razorpayApiKey())

        val options = JSONObject()
        options.put("name", "Easy Shop")
        options.put("description", "")
        options.put("currency", "INR")
        options.put("amount", (amount * 100).toInt())

        checkOut.open(GlobalNavigation.navController.context as Activity, options)
    }

}
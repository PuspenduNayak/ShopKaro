package com.example.easyshop

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.easyshop.ui.theme.EasyShopTheme
import com.razorpay.PaymentResultListener

class MainActivity : ComponentActivity(), PaymentResultListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EasyShopTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavigation()
                }
            }
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        AppUtil.clearCartAndAddToOrder()

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Payment Successful")
            .setMessage("Thank you your payment was completed successfully and your order has been placed")
            .setPositiveButton("OK") { _, _ ->
                val navController = GlobalNavigation.navController
                navController.popBackStack()
                navController.navigate("home")
            }
            .setCancelable(false)
            .show()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        AppUtil.showToast(this, "Payment Failed")
    }
}

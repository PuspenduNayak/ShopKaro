package com.example.easyshop

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.CompositionLocalProvider
import com.example.easyshop.ui.theme.EasyShopTheme
import com.example.easyshop.ui.theme.LocalIsDarkTheme
import com.example.easyshop.ui.theme.LocalToggleDarkTheme
import com.razorpay.PaymentResultListener

class MainActivity : ComponentActivity(), PaymentResultListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // single source of truth for theme
            var darkMode by rememberSaveable { mutableStateOf(false) }

            // Provide both the boolean and a toggle function to the whole tree
            CompositionLocalProvider(
                LocalIsDarkTheme provides darkMode,
                LocalToggleDarkTheme provides { darkMode = !darkMode }
            ) {
                EasyShopTheme(darkTheme = darkMode) {
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

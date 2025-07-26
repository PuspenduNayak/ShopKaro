package com.example.easyshop.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.easyshop.GlobalNavigation
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AdminHomeScreen(modifier: Modifier = Modifier, navController: NavController) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { navController.navigate("add-product") }) {
            Text("Add Product")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("admin-product-list") }) {
            Text("All Products")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("admin-orders") }) {
            Text("View All Orders")
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = {
            FirebaseAuth.getInstance().signOut()
            // Use the global navigator to return to the auth screen
            GlobalNavigation.navController.navigate("auth") {
                // Clear the entire back stack
                popUpTo(0)
            }
        }) {
            Text("Logout")
        }
    }
}

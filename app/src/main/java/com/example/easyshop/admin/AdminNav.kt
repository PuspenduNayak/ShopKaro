package com.example.easyshop.admin

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AdminNav(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "admin-home") {
        composable("admin-home") {
            AdminHomeScreen(modifier, navController)
        }
        composable("add-product") {
            AddProductScreen(modifier, navController)
        }
        composable("admin-product-list") {
            AdminProductListScreen(modifier)
        }
        composable("admin-orders") {
            AdminOrdersScreen(modifier)
        }
    }
}
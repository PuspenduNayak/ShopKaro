package com.example.easyshop

import com.example.easyshop.admin.AdminNav
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.easyshop.model.UserModel
import com.example.easyshop.pages.CategoryProductsPage
import com.example.easyshop.component.ProductDetailsPage
import com.example.easyshop.pages.CheckOutPage
import com.example.easyshop.pages.OrdersPage
import com.example.easyshop.screen.AuthScreen
import com.example.easyshop.screen.HomeScreen
import com.example.easyshop.screen.LoginScreen
import com.example.easyshop.screen.SignupScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    GlobalNavigation.navController = navController

    var userModel by remember { mutableStateOf<UserModel?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val currentUser = Firebase.auth.currentUser

    // This effect runs when the app starts to fetch the user's role
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            Firebase.firestore.collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    userModel = document.toObject(UserModel::class.java)
                    isLoading = false
                }
                .addOnFailureListener {
                    // Handle error, maybe user document doesn't exist
                    isLoading = false
                }
        } else {
            isLoading = false
        }
    }

    if (isLoading) {
        // Show a loading spinner while we check the user's role
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val startDestination = when {
            currentUser == null -> "auth"
            userModel?.admin == true -> "admin"
            else -> "home"
        }

        NavHost(navController = navController, startDestination = startDestination) {
            composable("auth") {
                AuthScreen(modifier, navController)
            }
            composable("login") {
                LoginScreen(modifier, navController)
            }
            composable("signup") {
                SignupScreen(modifier, navController)
            }
            composable("home") {
                HomeScreen(modifier, navController)
            }
            composable("admin") {
                AdminNav(modifier)
            }
            composable("category-products/{categoryId}") {
                val categoryId = it.arguments?.getString("categoryId")
                CategoryProductsPage(modifier, categoryId ?: "")
            }
            composable("product-details/{productId}") {
                val productId = it.arguments?.getString("productId")
                ProductDetailsPage(modifier, productId ?: "")
            }

            composable("checkout") {
                CheckOutPage(modifier)
            }
            composable("orders") {
                OrdersPage(modifier)
            }
        }
    }
}


object GlobalNavigation {
    lateinit var navController: NavController
}





/*import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.easyshop.pages.CategoryProductsPage
import com.example.easyshop.component.ProductDetailsPage
import com.example.easyshop.pages.CheckOutPage
import com.example.easyshop.pages.OrdersPage
import com.example.easyshop.screen.AuthScreen
import com.example.easyshop.screen.HomeScreen
import com.example.easyshop.screen.LoginScreen
import com.example.easyshop.screen.SignupScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    GlobalNavigation.navController = navController
    val isLoggedIn = Firebase.auth.currentUser!=null
    val firstPage = if(isLoggedIn)"home" else "auth"

    NavHost(navController = navController, startDestination = firstPage) {
        composable("auth"){
            AuthScreen(modifier, navController)
        }
        composable("login"){
            LoginScreen(modifier, navController)
        }
        composable("signup"){
            SignupScreen(modifier, navController)
        }
        composable("home"){
            HomeScreen(modifier, navController)
        }
        composable("category-products/{categoryId}"){
            var categoryId = it.arguments?.getString("categoryId")
            CategoryProductsPage(modifier,categoryId?:"")
        }
        composable("product-details/{productId}"){
            var productId = it.arguments?.getString("productId")
            ProductDetailsPage(modifier,productId?:"")
        }

        composable("checkout"){
            CheckOutPage(modifier)
        }
        composable("orders"){
            OrdersPage(modifier)
        }
    }
}

object GlobalNavigation{
    lateinit var navController: NavController
}*/

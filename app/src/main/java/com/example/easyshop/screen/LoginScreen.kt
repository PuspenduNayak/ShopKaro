package com.example.easyshop.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.easyshop.AppUtil
import com.example.easyshop.R
import com.example.easyshop.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome back!",
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                fontSize = 30.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Sign in to your account",
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                fontSize = 20.sp,
                fontFamily = FontFamily.Monospace,
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        Image(
            painter = painterResource(id = R.drawable.login_banner),
            contentDescription = "Login Banner",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email address") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        // 🔹 Forgot password text
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Forgot Password?",
            color = Color.Blue,
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.End)
                .clickable {
                    if (email.isNotEmpty()) {
                        FirebaseAuth.getInstance()
                            .sendPasswordResetEmail(email)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    AppUtil.showToast(context, "Password reset email sent to $email")
                                } else {
                                    AppUtil.showToast(context, "Failed: ${task.exception?.message}")
                                }
                            }
                    } else {
                        AppUtil.showToast(context, "Enter your email first")
                    }
                }
        )

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                isLoading = true
                authViewModel.login(email, password) { userModel, errorMessage ->
                    isLoading = false
                    if (userModel != null) {
                        val route = if (userModel.admin) "admin" else "home"
                        navController.navigate(route) {
                            popUpTo("auth") { inclusive = true }
                        }
                    } else {
                        AppUtil.showToast(context, errorMessage ?: "Something went wrong")
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text(text = if (isLoading) "Logging in" else "Login", fontSize = 22.sp)
        }
    }
}
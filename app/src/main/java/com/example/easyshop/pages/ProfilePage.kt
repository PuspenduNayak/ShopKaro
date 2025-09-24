package com.example.easyshop.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.easyshop.AppUtil
import com.example.easyshop.GlobalNavigation
import com.example.easyshop.model.UserModel
import com.example.easyshop.ui.theme.LocalIsDarkTheme
import com.example.easyshop.ui.theme.LocalToggleDarkTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@Composable
fun ProfilePage(modifier: Modifier = Modifier) {
    // read the global theme state & toggle
    val isDark = LocalIsDarkTheme.current
    val toggleDark = LocalToggleDarkTheme.current

    val userModel = remember { mutableStateOf(UserModel()) }
    var addressInput by remember { mutableStateOf(userModel.value.address) }
    var emailInput by remember { mutableStateOf(userModel.value.email) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)
            .get().addOnSuccessListener { snapshot ->
                snapshot.toObject(UserModel::class.java)?.let {
                    userModel.value = it
                    addressInput = it.address
                    emailInput = it.email
                }
            }
    }

    // Do NOT wrap this screen again in EasyShopTheme — the app theme is already provided in MainActivity
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp, 50.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Profile",
                style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold)
            )

            IconButton(onClick = { toggleDark() }) {
                Icon(
                    imageVector = if (isDark) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                    contentDescription = "Toggle Theme"
                )
            }
        }

        Icon(
            Icons.Default.AccountCircle,
            contentDescription = "User Icon",
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        )

        Text(
            userModel.value.name,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(20.dp))

        Text("Address:", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = addressInput,
            onValueChange = { addressInput = it },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                if (addressInput.isNotEmpty()) {
                    Firebase.firestore.collection("users")
                        .document(FirebaseAuth.getInstance().currentUser?.uid!!)
                        .update("address", addressInput)
                        .addOnSuccessListener {
                            AppUtil.showToast(context, "Address updated successfully")
                        }
                } else {
                    AppUtil.showToast(context, "Address can't be empty")
                }
            })
        )

        Spacer(Modifier.height(12.dp))

        Text("Email:", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = emailInput,
            onValueChange = { emailInput = it },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                if (emailInput.isNotEmpty()) {
                    Firebase.firestore.collection("users")
                        .document(FirebaseAuth.getInstance().currentUser?.uid!!)
                        .update("email", emailInput)
                        .addOnSuccessListener {
                            AppUtil.showToast(context, "Email updated successfully")
                        }
                } else {
                    AppUtil.showToast(context, "Email can't be empty")
                }
            })
        )

        Spacer(Modifier.height(12.dp))

        Text("Number of items in cart:", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Text(userModel.value.cartItems.values.sum().toString())

        Spacer(Modifier.height(12.dp))

        Text(
            "View my orders",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { GlobalNavigation.navController.navigate("orders") }
                .padding(vertical = 16.dp)
        )

        TextButton(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                val navController = GlobalNavigation.navController
                navController.popBackStack()
                navController.navigate("auth")
            },
            Modifier.fillMaxWidth()
        ) {
            Text("Sign out", fontSize = 18.sp)
        }
    }
}

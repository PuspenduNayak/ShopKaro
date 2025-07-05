package com.example.easyshop.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.easyshop.AppUtil
import com.example.easyshop.GlobalNavigation
import com.example.easyshop.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.nio.file.WatchEvent

@Composable
fun ProfilePage(modifier: Modifier = Modifier) {

    val userModel = remember {
        mutableStateOf(UserModel())
    }
    var addressInput by remember {
        mutableStateOf(userModel.value.address)
    }
    var emailInput by remember {
        mutableStateOf(userModel.value.email)
    }

    var context = LocalContext.current

    LaunchedEffect(Unit) {
        Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val result = it.result.toObject(UserModel::class.java)
                    if (result != null) {
                        userModel.value = result
                        addressInput = userModel.value.address
                        emailInput = userModel.value.email
                    }

                }
            }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp, 50.dp)
    ) {
        Text(
            text = "Your Profile", style = TextStyle(
                fontSize = 22.sp, fontWeight = FontWeight.Bold
            )
        )

        //Image
        Icon(
            Icons.Default.AccountCircle, "User Icon", modifier = Modifier
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

        Text("Address: ", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = addressInput,
            onValueChange = {
                addressInput = it

            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                //Update to firebase
                if (addressInput.isNotEmpty()) {
                    Firebase.firestore.collection("users")
                        .document(FirebaseAuth.getInstance().currentUser?.uid!!)
                        .update("address", addressInput)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                AppUtil.showToast(context, "Address Updated successfully")
                            }
                        }
                } else {
                    AppUtil.showToast(context, "Address can't be empty")
                }
            })
        )


        Spacer(Modifier.height(12.dp))
        Text("Email: ", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = emailInput,
            onValueChange = {
                emailInput = it
            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                //Update to firebase
                if (emailInput.isNotEmpty()) {
                    Firebase.firestore.collection("users")
                        .document(FirebaseAuth.getInstance().currentUser?.uid!!)
                        .update("email", emailInput)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                AppUtil.showToast(context, "Email Updated successfully")
                            }
                        }
                } else {
                    AppUtil.showToast(context, "Email can't be empty")
                }
            })
        )

        Spacer(Modifier.height(12.dp))
        Text(
            "Number of item in cart.",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        Text(userModel.value.cartItems.values.sum().toString())

        Spacer(Modifier.height(12.dp))
        Text(
            "View my orders",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    GlobalNavigation.navController.navigate("orders")
                }
                .padding(vertical = 16.dp)
        )

        TextButton(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                val navController = GlobalNavigation.navController
                navController.popBackStack()
                navController.navigate("auth")
            },
            Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        ) {
            Text("Sign out", fontSize = 18.sp)
        }


    }

}
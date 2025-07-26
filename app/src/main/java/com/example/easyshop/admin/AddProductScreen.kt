package com.example.easyshop.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.easyshop.AppUtil
import com.example.easyshop.GlobalNavigation
import com.example.easyshop.model.CategoryModel
import com.example.easyshop.model.ProductModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var actualPrice by remember { mutableStateOf("") }
    val context = LocalContext.current

    // State for the category dropdown
    var categories by remember { mutableStateOf<List<CategoryModel>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf<CategoryModel?>(null) }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    // Fetch categories from Firestore
    LaunchedEffect(Unit) {
        Firebase.firestore.collection("data")
            .document("stock")
            .collection("Categories")
            .get()
            .addOnSuccessListener { result ->
                categories = result.toObjects(CategoryModel::class.java)
            }
            .addOnFailureListener {
                AppUtil.showToast(context, "Failed to load categories.")
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp, 50.dp)
    ) {
        Text("Add New Product", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = actualPrice,
            onValueChange = { actualPrice = it },
            label = { Text("Actual Price") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Category Dropdown Menu
        ExposedDropdownMenuBox(
            expanded = isCategoryDropdownExpanded,
            onExpandedChange = { isCategoryDropdownExpanded = !isCategoryDropdownExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                value = selectedCategory?.name ?: "",
                onValueChange = {},
                label = { Text("Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
            )
            ExposedDropdownMenu(
                expanded = isCategoryDropdownExpanded,
                onDismissRequest = { isCategoryDropdownExpanded = false },
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            selectedCategory = category
                            isCategoryDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val newProduct = ProductModel(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    description = description,
                    price = price,
                    actualPrice = actualPrice,
                    category = selectedCategory?.id ?: "" // Use the ID of the selected category
                )
                Firebase.firestore.collection("data").document("stock")
                    .collection("products").document(newProduct.id)
                    .set(newProduct)
                    .addOnSuccessListener {
                        AppUtil.showToast(context, "Product added successfully")
                        // Navigate back after successful addition
                        GlobalNavigation.navController.popBackStack()
                    }
                    .addOnFailureListener {
                        AppUtil.showToast(context, "Failed to add product")
                    }
                GlobalNavigation.navController.navigate("admin")
            },
            modifier = Modifier.fillMaxWidth(),
            // Disable the button until a category is selected and fields are filled
            enabled = selectedCategory != null && title.isNotBlank() && actualPrice.isNotBlank()
        ) {
            Text("Add Product")
        }
    }
}



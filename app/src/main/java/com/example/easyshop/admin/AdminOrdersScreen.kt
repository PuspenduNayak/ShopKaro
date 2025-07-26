package com.example.easyshop.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.easyshop.AppUtil
import com.example.easyshop.model.OrderModel
import com.example.easyshop.model.ProductModel
import com.example.easyshop.model.UserModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminOrdersScreen(modifier: Modifier = Modifier) {
    var orders by remember { mutableStateOf<List<OrderModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        Firebase.firestore.collection("orders")
            .get()
            .addOnSuccessListener { result ->
                orders = result.toObjects(OrderModel::class.java)
                isLoading = false
            }
            .addOnFailureListener { exception ->
                errorMessage = "Failed to load orders: ${exception.message}"
                isLoading = false
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp, 50.dp)
    ) {
        Text(
            text = "All Customer Orders",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                }
            }
            orders.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No orders found.")
                }
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(orders) { order ->
                        AdminOrderItem(order = order)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderItem(order: OrderModel) {
    val context = LocalContext.current
    var customer by remember { mutableStateOf<UserModel?>(null) }
    var isStatusDropdownExpanded by remember { mutableStateOf(false) }
    val orderStatuses = listOf("ORDERED", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED")
    var currentStatus by remember { mutableStateOf(order.status) }

    // Fetch customer details
    LaunchedEffect(order.userId) {
        Firebase.firestore.collection("users").document(order.userId).get()
            .addOnSuccessListener { document ->
                customer = document.toObject(UserModel::class.java)
            }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Order #${order.id}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            customer?.let {
                Text("Customer: ${it.name}", fontWeight = FontWeight.SemiBold)
                Text("Email: ${it.email}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, "Date", Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(formatDate(order.date.toDate()), fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.LocationOn, "Address", Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(order.address, fontSize = 14.sp, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Text("Items (${order.items.size})", fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            order.items.forEach { (productId, quantity) ->
                OrderProductItem(productId = productId, quantity = quantity)
                Spacer(modifier = Modifier.height(4.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Order Status:", fontWeight = FontWeight.SemiBold)
                ExposedDropdownMenuBox(
                    expanded = isStatusDropdownExpanded,
                    onExpandedChange = { isStatusDropdownExpanded = !isStatusDropdownExpanded }
                ) {
                    TextField(
                        modifier = Modifier.menuAnchor(),
                        readOnly = true,
                        value = currentStatus,
                        onValueChange = {},
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStatusDropdownExpanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    )
                    ExposedDropdownMenu(
                        expanded = isStatusDropdownExpanded,
                        onDismissRequest = { isStatusDropdownExpanded = false },
                    ) {
                        orderStatuses.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status) },
                                onClick = {
                                    Firebase.firestore.collection("orders").document(order.id)
                                        .update("status", status)
                                        .addOnSuccessListener {
                                            AppUtil.showToast(context, "Order status updated")
                                            currentStatus = status
                                            isStatusDropdownExpanded = false
                                        }
                                        .addOnFailureListener { AppUtil.showToast(context, "Failed to update status") }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderProductItem(productId: String, quantity: Long) {
    var product by remember { mutableStateOf<ProductModel?>(null) }
    LaunchedEffect(productId) {
        Firebase.firestore.collection("data").document("stock")
            .collection("products").document(productId).get()
            .addOnSuccessListener { product = it.toObject(ProductModel::class.java) }
    }
    product?.let { prod ->
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(prod.images.firstOrNull(), prod.title, Modifier.size(40.dp).padding(end = 8.dp))
            Column(Modifier.weight(1f)) {
                Text(prod.title, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                Text("₹${prod.actualPrice} x $quantity", fontSize = 12.sp)
            }
            Text("₹${(prod.actualPrice.toFloatOrNull() ?: 0f) * quantity}", fontSize = 14.sp)
        }
    }
}

private fun formatDate(date: Date): String {
    return SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()).format(date)
}

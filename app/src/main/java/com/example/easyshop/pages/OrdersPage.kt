package com.example.easyshop.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.easyshop.model.OrderModel
import com.example.easyshop.model.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrdersPage(modifier: Modifier = Modifier) {
    var orders by remember { mutableStateOf<List<OrderModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            Firebase.firestore.collection("orders")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener { task ->
                    isLoading = false
                    if (task.isSuccessful) {
                        val ordersList = task.result.toObjects(OrderModel::class.java)
                        orders = ordersList
                    } else {
                        errorMessage = "Failed to load orders"
                    }
                }
        } else {
            isLoading = false
            errorMessage = "User not logged in"
        }
    }

    // Apply the theme's background color to the whole screen
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp, 50.dp)
        ) {
            Text(
                text = "My Orders",
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
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = "No orders",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No orders yet")
                        }
                    }
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(orders) { order ->
                            OrderItem(order = order)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItem(order: OrderModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order #${order.id}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                val statusColor = when (order.status) {
                    "ORDERED", "DELIVERED" -> MaterialTheme.colorScheme.primary
                    "PROCESSING" -> MaterialTheme.colorScheme.secondary
                    "SHIPPED" -> Color(0xFF2196F3) // Consider adding this to your theme colors
                    "CANCELLED" -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = statusColor),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = order.status,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ... (rest of the OrderItem content is the same)
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
                Text("₹${prod.actualPrice} x $quantity", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("₹${(prod.actualPrice.toFloatOrNull() ?: 0f) * quantity}", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}


private fun formatDate(date: Date): String {
    return SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()).format(date)
}
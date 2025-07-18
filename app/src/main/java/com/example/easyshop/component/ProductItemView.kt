package com.example.easyshop.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.easyshop.AppUtil
import com.example.easyshop.GlobalNavigation
import com.example.easyshop.model.ProductModel

@Composable
fun ProductItemView(modifier: Modifier = Modifier, product: ProductModel) {

    var context = LocalContext.current

    Card(
        modifier = modifier
            .padding(8.dp)
            .clickable{
                GlobalNavigation.navController.navigate("product-details/"+product.id)
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            Modifier.padding(12.dp),
        ) {
            AsyncImage(
                product.images.firstOrNull(),
                product.title,
                Modifier
                    .height(120.dp)
                    .fillMaxWidth()
            )
            Text(
                text = product.title,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp)
            )

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "₹" + product.price,
                    fontSize = 14.sp,
                    style = TextStyle(textDecoration = TextDecoration.LineThrough)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "₹" + product.actualPrice,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            IconButton(onClick = {
                AppUtil.addItemToCart(product.id, context)
            }, Modifier.align(Alignment.CenterHorizontally)) {
                Icon(Icons.Default.ShoppingCart, "Add to Cart")
            }
        }
    }
}
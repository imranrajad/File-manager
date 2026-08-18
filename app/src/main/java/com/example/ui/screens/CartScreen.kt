package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CartItemWithProduct
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandCardBorder
import com.example.ui.theme.BrandError
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandSuccess
import com.example.ui.theme.BrandSurface
import com.example.ui.theme.BrandSurfaceVariant
import com.example.ui.theme.BrandText
import com.example.ui.theme.BrandTextSecondary
import com.example.ui.viewmodel.ShopViewModel

@Composable
fun CartScreen(
    viewModel: ShopViewModel,
    onNavigateToCheckout: () -> Unit,
    onNavigateToBrowse: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val appliedPromo by viewModel.appliedPromoCode.collectAsState()
    val promoDiscountPercent by viewModel.promoDiscountPercent.collectAsState()

    var promoInput by remember { mutableStateOf("") }

    val subtotal = cartItems.sumOf { it.totalPrice }
    val discountAmount = if (promoDiscountPercent > 0) (subtotal * promoDiscountPercent / 100.0) else 0.0
    val shipping = if (subtotal > 0) 0.0 else 0.0 // Free tech shipping
    val tax = (subtotal - discountAmount) * 0.08 // 8% sales tax
    val grandTotal = (subtotal - discountAmount + tax).coerceAtLeast(0.0)

    if (cartItems.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.RemoveShoppingCart,
            title = "Your Shopping Cart is Empty",
            subtitle = "Looks like you haven't added any futuristic tech items yet.",
            actionText = "Explore Electronics",
            onActionClick = onNavigateToBrowse
        )
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 90.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Cart Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Shopping Cart (${cartItems.size})",
                        color = BrandText,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Clear Cart",
                        color = BrandError,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { viewModel.clearCart() }
                            .testTag("clear_cart_action")
                    )
                }
            }

            // Cart Items List
            items(cartItems) { item ->
                CartItemRow(
                    item = item,
                    onIncrease = { viewModel.updateCartQuantity(item.cartItem.cartItemId, item.cartItem.quantity + 1) },
                    onDecrease = { viewModel.updateCartQuantity(item.cartItem.cartItemId, item.cartItem.quantity - 1) },
                    onRemove = { viewModel.removeFromCart(item.cartItem.cartItemId) }
                )
            }

            // Promo Code Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BrandSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalOffer,
                                contentDescription = null,
                                tint = BrandAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Promo & Discount Code",
                                color = BrandText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (appliedPromo != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(BrandSuccess.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = BrandSuccess)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "$appliedPromo ($promoDiscountPercent% OFF Applied)",
                                        color = BrandSuccess,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.removePromoCode() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove Promo", tint = BrandTextSecondary)
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = promoInput,
                                    onValueChange = { promoInput = it },
                                    placeholder = { Text("Enter code (e.g. TECHPRO20)", color = BrandTextSecondary, fontSize = 12.sp) },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("promo_code_input"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BrandAccent,
                                        unfocusedBorderColor = BrandCardBorder,
                                        cursorColor = BrandAccent,
                                        focusedTextColor = BrandText,
                                        unfocusedTextColor = BrandText
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (viewModel.applyPromoCode(promoInput)) {
                                            promoInput = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("apply_promo_btn")
                                ) {
                                    Text("Apply", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Quick sample chips
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("TECHPRO20", "YOUBTECH").forEach { chipCode ->
                                    Surface(
                                        color = BrandSurfaceVariant,
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.clickable {
                                            promoInput = chipCode
                                            viewModel.applyPromoCode(chipCode)
                                        }
                                    ) {
                                        Text(
                                            text = "$chipCode (Tap to apply)",
                                            color = BrandAccent,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Order Summary Bill Details
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BrandSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Order Summary",
                            color = BrandText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        BillRow(title = "Subtotal", amount = "$${String.format("%.2f", subtotal)}")
                        if (discountAmount > 0) {
                            BillRow(
                                title = "Discount ($appliedPromo)",
                                amount = "-$${String.format("%.2f", discountAmount)}",
                                color = BrandSuccess
                            )
                        }
                        BillRow(title = "Estimated Sales Tax (8%)", amount = "$${String.format("%.2f", tax)}")
                        BillRow(title = "Express Courier Shipping", amount = "FREE", color = BrandAccent)

                        HorizontalDivider(
                            color = BrandCardBorder,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Grand Total",
                                color = BrandText,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$${String.format("%.2f", grandTotal)}",
                                color = BrandAccent,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            // Checkout CTA Button
            item {
                Button(
                    onClick = onNavigateToCheckout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("proceed_to_checkout_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Proceed to Secure Checkout",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItemWithProduct,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BrandCardBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = BrandSurface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            AsyncImage(
                model = item.product.imageUrl,
                contentDescription = item.product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(75.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(BrandSurfaceVariant)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.name,
                    color = BrandText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Color: ${item.cartItem.selectedColor}",
                    color = BrandTextSecondary,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "$${String.format("%.2f", item.product.price)}",
                    color = BrandAccent,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Stepper & Delete
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Remove",
                        tint = BrandError.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(BrandSurfaceVariant, RoundedCornerShape(8.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = onDecrease,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = BrandText, modifier = Modifier.size(14.dp))
                    }
                    Text(
                        text = "${item.cartItem.quantity}",
                        color = BrandText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                    IconButton(
                        onClick = onIncrease,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = BrandText, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun BillRow(
    title: String,
    amount: String,
    color: Color = BrandText
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = BrandTextSecondary,
            fontSize = 13.sp
        )
        Text(
            text = amount,
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

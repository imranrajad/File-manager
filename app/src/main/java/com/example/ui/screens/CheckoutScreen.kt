package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandCardBorder
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandSuccess
import com.example.ui.theme.BrandSurface
import com.example.ui.theme.BrandSurfaceVariant
import com.example.ui.theme.BrandText
import com.example.ui.theme.BrandTextSecondary
import com.example.ui.viewmodel.ShopViewModel

enum class PaymentType(val title: String, val subtitle: String, val icon: ImageVector) {
    CARD("Credit / Debit Card", "Visa, Mastercard, Amex (256-bit SSL)", Icons.Default.CreditCard),
    UPI("UPI / Google Pay", "Instant payment with UPI QR or ID", Icons.Default.QrCode),
    NET_BANKING("Net Banking", "All major national & private banks", Icons.Default.AccountBalance),
    COD("Cash on Delivery", "Pay when delivered to your door", Icons.Default.Payments)
}

@Composable
fun CheckoutScreen(
    viewModel: ShopViewModel,
    onBack: () -> Unit,
    onOrderPlaced: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val promoDiscountPercent by viewModel.promoDiscountPercent.collectAsState()
    val appliedPromo by viewModel.appliedPromoCode.collectAsState()

    var name by remember { mutableStateOf("Alex Morgan") }
    var email by remember { mutableStateOf("alex.morgan@example.com") }
    var phone by remember { mutableStateOf("+1 (555) 382-9012") }
    var streetAddress by remember { mutableStateOf("742 Evergreen Terrace, Suite 4B") }
    var city by remember { mutableStateOf("San Francisco") }
    var zipCode by remember { mutableStateOf("94107") }
    var billingSameAsShipping by remember { mutableStateOf(true) }
    var billingAddress by remember { mutableStateOf("") }
    
    var selectedPayment by remember { mutableStateOf(PaymentType.CARD) }
    var cardNumber by remember { mutableStateOf("4532 •••• •••• 8892") }
    var cardExpiry by remember { mutableStateOf("08/29") }
    var cardCvv by remember { mutableStateOf("724") }
    var upiId by remember { mutableStateOf("alex@okhdfcbank") }

    var isProcessing by remember { mutableStateOf(false) }

    val subtotal = cartItems.sumOf { it.totalPrice }
    val discountAmount = if (promoDiscountPercent > 0) (subtotal * promoDiscountPercent / 100.0) else 0.0
    val tax = (subtotal - discountAmount) * 0.08
    val grandTotal = (subtotal - discountAmount + tax).coerceAtLeast(0.0)

    val itemsSummary = cartItems.joinToString(", ") { "${it.product.name} (x${it.cartItem.quantity})" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBackground)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = BrandText
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Secure Checkout",
                    color = BrandText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = BrandSuccess,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "256-Bit Encrypted Transaction",
                        color = BrandSuccess,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Shipping Details Card
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
                                imageVector = Icons.Default.LocalShipping,
                                contentDescription = null,
                                tint = BrandAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "1. Shipping & Contact Information",
                                color = BrandText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        CheckoutTextField(value = name, onValueChange = { name = it }, label = "Full Name")
                        Spacer(modifier = Modifier.height(10.dp))
                        CheckoutTextField(value = email, onValueChange = { email = it }, label = "Email Address")
                        Spacer(modifier = Modifier.height(10.dp))
                        CheckoutTextField(value = phone, onValueChange = { phone = it }, label = "Phone Number")
                        Spacer(modifier = Modifier.height(10.dp))
                        CheckoutTextField(value = streetAddress, onValueChange = { streetAddress = it }, label = "Delivery Street Address")
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CheckoutTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = "City",
                                modifier = Modifier.weight(1.2f)
                            )
                            CheckoutTextField(
                                value = zipCode,
                                onValueChange = { zipCode = it },
                                label = "Zip Code",
                                modifier = Modifier.weight(0.8f)
                            )
                        }
                    }
                }
            }

            // Billing Address Option
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BrandSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { billingSameAsShipping = !billingSameAsShipping }
                        ) {
                            Checkbox(
                                checked = billingSameAsShipping,
                                onCheckedChange = { billingSameAsShipping = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = BrandPrimary,
                                    uncheckedColor = BrandTextSecondary
                                )
                            )
                            Text(
                                text = "Billing address same as shipping address",
                                color = BrandText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (!billingSameAsShipping) {
                            Spacer(modifier = Modifier.height(8.dp))
                            CheckoutTextField(
                                value = billingAddress,
                                onValueChange = { billingAddress = it },
                                label = "Billing Address"
                            )
                        }
                    }
                }
            }

            // Payment Method Selection
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
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = BrandAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "2. Select Payment Method",
                                color = BrandText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        PaymentType.values().forEach { paymentType ->
                            val isSelected = selectedPayment == paymentType
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) BrandSurfaceVariant else Color.Transparent)
                                    .clickable { selectedPayment = paymentType }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedPayment = paymentType },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = BrandAccent,
                                        unselectedColor = BrandTextSecondary
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = paymentType.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) BrandAccent else BrandTextSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = paymentType.title,
                                        color = BrandText,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = paymentType.subtitle,
                                        color = BrandTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        // Input fields for Card or UPI
                        if (selectedPayment == PaymentType.CARD) {
                            Spacer(modifier = Modifier.height(10.dp))
                            CheckoutTextField(value = cardNumber, onValueChange = { cardNumber = it }, label = "Card Number")
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CheckoutTextField(value = cardExpiry, onValueChange = { cardExpiry = it }, label = "Expiry (MM/YY)", modifier = Modifier.weight(1f))
                                CheckoutTextField(value = cardCvv, onValueChange = { cardCvv = it }, label = "CVV / CVC", modifier = Modifier.weight(1f))
                            }
                        } else if (selectedPayment == PaymentType.UPI) {
                            Spacer(modifier = Modifier.height(10.dp))
                            CheckoutTextField(value = upiId, onValueChange = { upiId = it }, label = "UPI ID / VPA")
                        }
                    }
                }
            }

            // Order Review Summary
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BrandSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "3. Review Order Details",
                            color = BrandText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = itemsSummary,
                            color = BrandTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        BillRow(title = "Subtotal", amount = "$${String.format("%.2f", subtotal)}")
                        if (discountAmount > 0) {
                            BillRow(title = "Promo Discount ($appliedPromo)", amount = "-$${String.format("%.2f", discountAmount)}", color = BrandSuccess)
                        }
                        BillRow(title = "Tax (8%)", amount = "$${String.format("%.2f", tax)}")
                        BillRow(title = "Shipping", amount = "FREE", color = BrandAccent)
                        HorizontalDivider(color = BrandCardBorder, modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Final Total", color = BrandText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(text = "$${String.format("%.2f", grandTotal)}", color = BrandAccent, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }

        // Bottom Confirm Button
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = BrandSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, BrandCardBorder)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = {
                        if (!isProcessing) {
                            isProcessing = true
                            val finalBilling = if (billingSameAsShipping) "$streetAddress, $city $zipCode" else billingAddress
                            val finalShipping = "$streetAddress, $city $zipCode"
                            viewModel.placeOrder(
                                customerName = name,
                                email = email,
                                phone = phone,
                                shippingAddress = finalShipping,
                                billingAddress = finalBilling,
                                paymentMethod = selectedPayment.title,
                                subtotal = subtotal,
                                discount = discountAmount,
                                tax = tax,
                                total = grandTotal,
                                itemsSummary = itemsSummary
                            ) { createdOrder ->
                                isProcessing = false
                                onOrderPlaced(createdOrder.orderId)
                            }
                        }
                    },
                    enabled = !isProcessing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("confirm_place_order_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pay $${String.format("%.2f", grandTotal)} & Place Order",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CheckoutTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = BrandTextSecondary, fontSize = 12.sp) },
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandAccent,
            unfocusedBorderColor = BrandCardBorder,
            focusedTextColor = BrandText,
            unfocusedTextColor = BrandText,
            cursorColor = BrandAccent
        )
    )
}

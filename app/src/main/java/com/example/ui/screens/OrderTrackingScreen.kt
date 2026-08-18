package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatusStep
import com.example.ui.components.EmptyStateView
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrderTrackingScreen(
    viewModel: ShopViewModel,
    highlightOrderId: String? = null,
    onNavigateToBrowse: () -> Unit,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.orders.collectAsState()
    var selectedOrderId by remember(highlightOrderId, orders) {
        mutableStateOf(highlightOrderId ?: orders.firstOrNull()?.orderId)
    }

    val selectedOrder = orders.find { it.orderId == selectedOrderId } ?: orders.firstOrNull()

    if (orders.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.LocalShipping,
            title = "No Orders Placed Yet",
            subtitle = "When you order next-gen gadgets from You B Tech, track your delivery status and live progress here.",
            actionText = "Browse Products",
            onActionClick = onNavigateToBrowse
        )
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBackground)
            .padding(bottom = 85.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Text(
                text = "Order Tracking & History",
                color = BrandText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Horizontal list of user orders
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(orders) { order ->
                    val isSelected = order.orderId == selectedOrder?.orderId
                    Card(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedOrderId = order.orderId }
                            .border(
                                1.5.dp,
                                if (isSelected) BrandAccent else BrandCardBorder,
                                RoundedCornerShape(12.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) BrandPrimary.copy(alpha = 0.2f) else BrandSurface
                        )
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                            Text(
                                text = "#${order.orderId}",
                                color = BrandText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$${String.format("%.2f", order.totalAmount)} • ${order.status}",
                                color = if (order.status == "DELIVERED") BrandSuccess else BrandAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Active Selected Order Tracking Detail Card
        if (selectedOrder != null) {
            item {
                val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
                val orderDateStr = dateFormat.format(Date(selectedOrder.createdAt))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BrandSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Order #${selectedOrder.orderId}",
                                    color = BrandText,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Placed on $orderDateStr",
                                    color = BrandTextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            Surface(
                                color = if (selectedOrder.status == "DELIVERED") BrandSuccess.copy(alpha = 0.2f) else BrandPrimary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = selectedOrder.status,
                                    color = if (selectedOrder.status == "DELIVERED") BrandSuccess else BrandAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = BrandCardBorder)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Tracking Info
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BrandSurfaceVariant, RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Tracking Number",
                                    color = BrandTextSecondary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = selectedOrder.trackingNumber,
                                    color = BrandAccent,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Estimated Delivery",
                                    color = BrandTextSecondary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = selectedOrder.estimatedDelivery,
                                    color = BrandText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Live Tracking Timeline
                        Text(
                            text = "Live Order Progress",
                            color = BrandText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        TrackingTimeline(currentStatus = selectedOrder.status)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Status advancement simulator button
                        if (selectedOrder.status != "DELIVERED") {
                            Button(
                                onClick = {
                                    viewModel.advanceOrderStatus(selectedOrder.orderId, selectedOrder.status)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("simulate_courier_scan_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandSurfaceVariant),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = BrandAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Simulate Courier Scan (Next Step)", color = BrandText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Items & Delivery Address Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BrandSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Items Ordered",
                            color = BrandText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = selectedOrder.itemSummaries,
                            color = BrandTextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = BrandCardBorder)
                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Delivery Address",
                            color = BrandText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${selectedOrder.customerName}\n${selectedOrder.shippingAddress}\nContact: ${selectedOrder.customerPhone}",
                            color = BrandTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = BrandCardBorder)
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Payment: ${selectedOrder.paymentMethod}", color = BrandTextSecondary, fontSize = 12.sp)
                            Text("Total Paid: $${String.format("%.2f", selectedOrder.totalAmount)}", color = BrandAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrackingTimeline(currentStatus: String) {
    val steps = OrderStatusStep.values()
    val currentStepIndex = when (currentStatus) {
        "PLACED" -> 0
        "CONFIRMED" -> 1
        "SHIPPED" -> 2
        "OUT_FOR_DELIVERY" -> 3
        "DELIVERED" -> 4
        else -> 1
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        steps.forEachIndexed { index, step ->
            val isCompleted = index <= currentStepIndex
            val isCurrent = index == currentStepIndex

            val iconColor by animateColorAsState(
                targetValue = if (isCompleted) BrandAccent else BrandTextSecondary.copy(alpha = 0.4f),
                label = "iconColor"
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                // Indicator column (circle & connecting line)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = if (isCompleted) BrandPrimary else BrandSurfaceVariant,
                                shape = CircleShape
                            )
                            .border(
                                width = if (isCurrent) 2.dp else 0.dp,
                                color = if (isCurrent) BrandAccent else Color.Transparent,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    if (index < steps.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(36.dp)
                                .background(if (index < currentStepIndex) BrandPrimary else BrandCardBorder)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Text detail
                Column(modifier = Modifier.padding(bottom = if (index < steps.size - 1) 16.dp else 0.dp)) {
                    Text(
                        text = step.label,
                        color = if (isCompleted) BrandText else BrandTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal
                    )
                    Text(
                        text = step.description,
                        color = BrandTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

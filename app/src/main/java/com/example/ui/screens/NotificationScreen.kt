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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppNotification
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
fun NotificationScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val notifications by viewModel.notifications.collectAsState()
    val notifyNewDrops by viewModel.notifyNewDrops.collectAsState()
    val notifyStockAlerts by viewModel.notifyStockAlerts.collectAsState()
    val notifyOrderUpdates by viewModel.notifyOrderUpdates.collectAsState()
    val notifyExclusiveDiscounts by viewModel.notifyExclusiveDiscounts.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBackground)
            .padding(bottom = 85.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header & Mark all as read
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Push Notifications",
                    color = BrandText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                if (notifications.any { !it.isRead }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { viewModel.markAllNotificationsAsRead() }
                            .testTag("mark_all_read_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = null,
                            tint = BrandAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Mark all read",
                            color = BrandAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Test Simulated Notification Push Triggers (PRD Requirement)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BrandSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Simulate Real-Time Push Notification",
                        color = BrandText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Test live notifications for new product launches, stock restocks, and promotional drops.",
                        color = BrandTextSecondary,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.triggerTestNotification("DROP") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                        ) {
                            Text("New Drop", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { viewModel.triggerTestNotification("STOCK") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandSurfaceVariant)
                        ) {
                            Text("Stock Alert", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { viewModel.triggerTestNotification("PROMO") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandSurfaceVariant)
                        ) {
                            Text("Cyber Sale", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Notification Preferences Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BrandSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Notification Preferences",
                        color = BrandText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    PrefToggleRow(
                        title = "New Product Launches",
                        subtitle = "Alerts when next-gen tech drops",
                        checked = notifyNewDrops,
                        onCheckedChange = { viewModel.notifyNewDrops.value = it }
                    )
                    PrefToggleRow(
                        title = "In-Stock Restock Alerts",
                        subtitle = "When saved items become available",
                        checked = notifyStockAlerts,
                        onCheckedChange = { viewModel.notifyStockAlerts.value = it }
                    )
                    PrefToggleRow(
                        title = "Live Order & Pickup Updates",
                        subtitle = "Courier tracking and delivery alerts",
                        checked = notifyOrderUpdates,
                        onCheckedChange = { viewModel.notifyOrderUpdates.value = it }
                    )
                    PrefToggleRow(
                        title = "Exclusive Promo Codes",
                        subtitle = "Flash deals and VIP member savings",
                        checked = notifyExclusiveDiscounts,
                        onCheckedChange = { viewModel.notifyExclusiveDiscounts.value = it }
                    )
                }
            }
        }

        // Notification List
        if (notifications.isEmpty()) {
            item {
                EmptyStateView(
                    icon = Icons.Default.NotificationsNone,
                    title = "No Notifications",
                    subtitle = "You are all caught up! You'll receive updates on your orders and cyber sales here."
                )
            }
        } else {
            item {
                Text(
                    text = "Recent Notifications",
                    color = BrandText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(notifications) { notif ->
                NotificationItemRow(
                    notification = notif,
                    onClick = { viewModel.markNotificationAsRead(notif.id) }
                )
            }
        }
    }
}

@Composable
fun NotificationItemRow(
    notification: AppNotification,
    onClick: () -> Unit
) {
    val (icon, tint) = when (notification.type) {
        "ORDER" -> Icons.Default.ShoppingBag to BrandAccent
        "STOCK" -> Icons.Default.Inventory to BrandSuccess
        "DROP" -> Icons.Default.Bolt to Color(0xFFFBBF24)
        else -> Icons.Default.LocalOffer to BrandPrimary
    }

    val timeFormatted = SimpleDateFormat("MMM dd • hh:mm a", Locale.getDefault()).format(Date(notification.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(
                1.dp,
                if (!notification.isRead) BrandAccent.copy(alpha = 0.5f) else BrandCardBorder,
                RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.isRead) BrandSurfaceVariant.copy(alpha = 0.6f) else BrandSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(tint.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        color = BrandText,
                        fontSize = 13.sp,
                        fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.SemiBold
                    )
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(BrandAccent, CircleShape)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = notification.message,
                    color = BrandTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = timeFormatted,
                    color = BrandTextSecondary.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun PrefToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = BrandText, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(text = subtitle, color = BrandTextSecondary, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = BrandPrimary,
                uncheckedTrackColor = BrandSurfaceVariant
            )
        )
    }
}

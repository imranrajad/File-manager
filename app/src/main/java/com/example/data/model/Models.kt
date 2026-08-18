package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: Double,
    val originalPrice: Double = price * 1.25,
    val brand: String,
    val imageUrl: String,
    val description: String,
    val rating: Double,
    val reviewsCount: Int,
    val inStock: Boolean = true,
    val availableColors: List<String> = listOf("Space Gray", "Midnight Blue", "Cyan Glow"),
    val specs: Map<String, String> = emptyMap(),
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false
) {
    val discountPercent: Int
        get() = if (originalPrice > price) (((originalPrice - price) / originalPrice) * 100).toInt() else 0
}

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val cartItemId: String,
    val productId: String,
    val quantity: Int,
    val selectedColor: String = "Space Gray"
)

@Entity(tableName = "wishlist_items")
data class WishlistItemEntity(
    @PrimaryKey val wishlistId: String,
    val productId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val userId: String = "usr_youbtech_99",
    val customerName: String,
    val customerEmail: String,
    val customerPhone: String,
    val shippingAddress: String,
    val billingAddress: String,
    val paymentMethod: String,
    val subtotal: Double,
    val discount: Double,
    val tax: Double,
    val totalAmount: Double,
    val status: String, // PLACED, CONFIRMED, SHIPPED, OUT_FOR_DELIVERY, DELIVERED
    val trackingNumber: String,
    val trackingLink: String,
    val estimatedDelivery: String,
    val createdAt: Long = System.currentTimeMillis(),
    val itemSummaries: String // JSON or compact summary of items
)

@Entity(tableName = "notifications")
data class AppNotification(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val type: String, // ORDER, PROMO, STOCK, DROP
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

data class CartItemWithProduct(
    val cartItem: CartItemEntity,
    val product: Product
) {
    val totalPrice: Double get() = product.price * cartItem.quantity
}

enum class OrderStatusStep(val label: String, val description: String) {
    PLACED("Order Placed", "Your order has been securely received."),
    CONFIRMED("Confirmed", "Payment verified & processed by You B Tech."),
    SHIPPED("Shipped", "Package picked up by courier service."),
    OUT_FOR_DELIVERY("Out for Delivery", "Courier executive is near your location."),
    DELIVERED("Delivered", "Package safely handed over.")
}

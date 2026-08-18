package com.example.data.repository

import android.content.Context
import com.example.data.local.ShopDao
import com.example.data.local.ShopDatabase
import com.example.data.model.AppNotification
import com.example.data.model.CartItemEntity
import com.example.data.model.CartItemWithProduct
import com.example.data.model.OrderEntity
import com.example.data.model.Product
import com.example.data.model.WishlistItemEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

class ShopRepository(private val dao: ShopDao) {

    // Comprehensive Electronics and Tech Catalog for You B Tech
    val products = listOf(
        Product(
            id = "prod_101",
            name = "AeroPro Max Noise-Canceling Headphones",
            category = "Audio",
            price = 299.99,
            originalPrice = 379.99,
            brand = "SonicTech",
            imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&q=80",
            description = "Industry-leading active noise cancellation with 40mm beryllium drivers, spatial audio tracking, and up to 60 hours of high-fidelity wireless playback on a single charge.",
            rating = 4.9,
            reviewsCount = 428,
            inStock = true,
            availableColors = listOf("Space Gray", "Midnight Blue", "Cyan Glow", "Matte Black"),
            specs = mapOf(
                "Driver" to "40mm Beryllium High-Res",
                "Battery Life" to "60 Hours ANC On",
                "Bluetooth" to "5.4 with LDAC & aptX HD",
                "Weight" to "254g"
            ),
            isFeatured = true,
            isTrending = true
        ),
        Product(
            id = "prod_102",
            name = "Titanium Ultra 5G Smartphone 256GB",
            category = "Smartphones",
            price = 999.00,
            originalPrice = 1199.00,
            brand = "NexPhone",
            imageUrl = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800&q=80",
            description = "Aerospace-grade titanium chassis with next-gen AI silicon, 6.8\" AMOLED 120Hz display, and 200MP periscope zoom camera system with cinematic 8K video capture.",
            rating = 4.8,
            reviewsCount = 812,
            inStock = true,
            availableColors = listOf("Natural Titanium", "Deep Ocean Blue", "Stealth Black"),
            specs = mapOf(
                "Processor" to "Octa-core 3.4GHz AI Chipset",
                "Display" to "6.8\" LTPO AMOLED 1-120Hz",
                "Camera" to "200MP Main + 50MP Ultra-wide + 12MP Periscope",
                "Battery" to "5200mAh 100W Fast Charge"
            ),
            isFeatured = true,
            isTrending = true
        ),
        Product(
            id = "prod_103",
            name = "Zenith X1 Pro Creator Laptop 16\"",
            category = "Laptops",
            price = 1849.99,
            originalPrice = 2199.99,
            brand = "Veloce",
            imageUrl = "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=800&q=80",
            description = "Engineered for creators and engineers. Features 16\" Mini-LED 165Hz panel, 32GB LPDDR5X RAM, 1TB NVMe PCIe 4.0 SSD, and dual vapor-chamber cooling.",
            rating = 4.9,
            reviewsCount = 310,
            inStock = true,
            availableColors = listOf("Space Gray", "Silver Frost"),
            specs = mapOf(
                "Display" to "16\" 3.2K 165Hz Mini-LED (1600 nits)",
                "Memory" to "32GB Unified 6400MHz",
                "Storage" to "1TB PCIe Gen4 NVMe",
                "Ports" to "2x Thunderbolt 4, HDMI 2.1, SD Express"
            ),
            isFeatured = true,
            isTrending = false
        ),
        Product(
            id = "prod_104",
            name = "ChronoPulse Smartwatch Elite",
            category = "Wearables",
            price = 249.50,
            originalPrice = 319.00,
            brand = "PulseLife",
            imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&q=80",
            description = "Rugged titanium and sapphire glass fitness smartwatch with ECG, multi-band GPS, SpO2 sensor, sleep staging, and 14-day battery life.",
            rating = 4.7,
            reviewsCount = 590,
            inStock = true,
            availableColors = listOf("Titanium Gray", "Obsidian Black", "Solar Orange"),
            specs = mapOf(
                "Water Resistance" to "10 ATM (100m Dive)",
                "Sensors" to "ECG, Dual-Frequency GPS, BioTracker PPG",
                "Battery" to "Up to 14 Days Smart Mode",
                "Display" to "1.43\" AMOLED Always-On (2000 nits)"
            ),
            isFeatured = false,
            isTrending = true
        ),
        Product(
            id = "prod_105",
            name = "Apex RGB Mechanical Gaming Keyboard",
            category = "Gaming",
            price = 139.99,
            originalPrice = 179.99,
            brand = "RazerCore",
            imageUrl = "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800&q=80",
            description = "Ultra-fast optical-mechanical switches with hot-swappable PCB, sound dampening silicone gaskets, per-key RGB lighting, and magnetic wrist rest.",
            rating = 4.8,
            reviewsCount = 275,
            inStock = true,
            availableColors = listOf("Cyberpunk Black", "Arctic White"),
            specs = mapOf(
                "Switches" to "Linear Optical Gen-2 (45g Actuation)",
                "Keycaps" to "Double-shot PBT OEM Profile",
                "Polling Rate" to "8000Hz Hyper-polling",
                "Connectivity" to "2.4GHz Wireless / Bluetooth 5.2 / USB-C"
            ),
            isFeatured = false,
            isTrending = true
        ),
        Product(
            id = "prod_106",
            name = "Orbital 360 AI Home Security Camera 4K",
            category = "Smart Home",
            price = 119.00,
            originalPrice = 149.00,
            brand = "SonicTech",
            imageUrl = "https://images.unsplash.com/photo-1557438159-51eec7a6c9e8?w=800&q=80",
            description = "4K HDR security camera with 360-degree pan-tilt coverage, AI person & pet recognition, infrared night vision, and encrypted local storage.",
            rating = 4.6,
            reviewsCount = 188,
            inStock = true,
            availableColors = listOf("Pure White", "Slate Gray"),
            specs = mapOf(
                "Resolution" to "4K Ultra HD (3840 x 2160)",
                "Field of View" to "360° Horizontal, 110° Vertical",
                "Night Vision" to "Color Night Vision up to 30ft",
                "Audio" to "2-Way Full Duplex with Noise Shield"
            ),
            isFeatured = false,
            isTrending = false
        ),
        Product(
            id = "prod_107",
            name = "Vortex Swift Wireless Gaming Mouse",
            category = "Gaming",
            price = 89.99,
            originalPrice = 119.99,
            brand = "RazerCore",
            imageUrl = "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=800&q=80",
            description = "Featherlight 58g ergonomic esports mouse with 30,000 DPI optical sensor, optical switches rated for 90M clicks, and zero-latency wireless tech.",
            rating = 4.7,
            reviewsCount = 340,
            inStock = true,
            availableColors = listOf("Matte Black", "Cyan Glow"),
            specs = mapOf(
                "Sensor" to "Focus Pro 30K Optical",
                "Weight" to "58 Grams Ultra-Light",
                "Battery" to "90 Hours Continuous",
                "DPI" to "100 - 30,000 Adjustable"
            ),
            isFeatured = false,
            isTrending = false
        ),
        Product(
            id = "prod_108",
            name = "Nova 140W GaN Fast Charger & Hub",
            category = "Accessories",
            price = 69.99,
            originalPrice = 89.99,
            brand = "NexPhone",
            imageUrl = "https://images.unsplash.com/photo-1622445262464-84b1456045b6?w=800&q=80",
            description = "Compact gallium nitride (GaN) multi-port charging powerhouse with 3x USB-C PD 3.1 ports and 1x USB-A port. Charges laptops, tablets, and phones simultaneously.",
            rating = 4.9,
            reviewsCount = 412,
            inStock = true,
            availableColors = listOf("Space Gray", "Midnight Blue"),
            specs = mapOf(
                "Max Output" to "140W Single Port / 140W Shared",
                "Tech" to "GaNFast III with Smart Thermal Protect",
                "Ports" to "3x USB-C + 1x USB-A",
                "Certification" to "USB-IF, CE, FCC, RoHS"
            ),
            isFeatured = true,
            isTrending = false
        )
    )

    val categories = listOf("All", "Smartphones", "Laptops", "Audio", "Wearables", "Gaming", "Smart Home", "Accessories")
    val brands = listOf("All", "SonicTech", "NexPhone", "Veloce", "PulseLife", "RazerCore")

    // Room DB Cart Flow mapped with Product details
    val cartItems: Flow<List<CartItemWithProduct>> = dao.getAllCartItems().map { list ->
        list.mapNotNull { item ->
            val prod = products.find { it.id == item.productId }
            prod?.let { CartItemWithProduct(item, it) }
        }
    }

    val wishlistIds: Flow<Set<String>> = dao.getAllWishlistItems().map { list ->
        list.map { it.productId }.toSet()
    }

    val orders: Flow<List<OrderEntity>> = dao.getAllOrders()
    val notifications: Flow<List<AppNotification>> = dao.getAllNotifications()

    fun getProductById(id: String): Product? {
        return products.find { it.id == id }
    }

    // Cart actions
    suspend fun addToCart(productId: String, quantity: Int = 1, color: String = "Space Gray") {
        val existingItem = dao.getAllCartItems()
        val cartItemId = "cart_${productId}_${color.replace(" ", "_")}"
        dao.insertOrUpdateCartItem(
            CartItemEntity(
                cartItemId = cartItemId,
                productId = productId,
                quantity = quantity,
                selectedColor = color
            )
        )
    }

    suspend fun updateCartQuantity(cartItemId: String, quantity: Int) {
        if (quantity <= 0) {
            dao.deleteCartItem(cartItemId)
        } else {
            dao.updateCartQuantity(cartItemId, quantity)
        }
    }

    suspend fun removeCartItem(cartItemId: String) {
        dao.deleteCartItem(cartItemId)
    }

    suspend fun clearCart() {
        dao.clearCart()
    }

    // Wishlist actions
    suspend fun toggleWishlist(productId: String, isInWishlist: Boolean) {
        if (isInWishlist) {
            dao.removeFromWishlist(productId)
        } else {
            dao.addToWishlist(WishlistItemEntity(wishlistId = "wish_${productId}", productId = productId))
        }
    }

    // Orders & Checkout
    suspend fun createOrder(
        customerName: String,
        email: String,
        phone: String,
        shippingAddress: String,
        billingAddress: String,
        paymentMethod: String,
        subtotal: Double,
        discount: Double,
        tax: Double,
        total: Double,
        itemsSummary: String
    ): OrderEntity {
        val orderNumber = (100000..999999).random()
        val orderId = "YBT-$orderNumber"
        val trackingNum = "TRK-YBT-${(1000000..9999999).random()}"
        
        val newOrder = OrderEntity(
            orderId = orderId,
            customerName = customerName,
            customerEmail = email,
            customerPhone = phone,
            shippingAddress = shippingAddress,
            billingAddress = billingAddress,
            paymentMethod = paymentMethod,
            subtotal = subtotal,
            discount = discount,
            tax = tax,
            totalAmount = total,
            status = "CONFIRMED",
            trackingNumber = trackingNum,
            trackingLink = "https://youbtech.com/track/$trackingNum",
            estimatedDelivery = "In 2 - 3 Business Days",
            createdAt = System.currentTimeMillis(),
            itemSummaries = itemsSummary
        )

        dao.insertOrder(newOrder)
        dao.clearCart()

        // Push order confirmation notification
        dao.insertNotification(
            AppNotification(
                id = UUID.randomUUID().toString(),
                title = "Order #$orderId Confirmed!",
                message = "Thank you $customerName! Your payment of $$${String.format("%.2f", total)} via $paymentMethod is verified. Tracking: $trackingNum",
                type = "ORDER"
            )
        )

        return newOrder
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String) {
        val existing = dao.getOrderById(orderId)
        if (existing != null) {
            val updated = existing.copy(status = newStatus)
            dao.updateOrder(updated)

            dao.insertNotification(
                AppNotification(
                    id = UUID.randomUUID().toString(),
                    title = "Order Status Updated: $newStatus",
                    message = "Order #${existing.orderId} status has updated to: $newStatus.",
                    type = "ORDER"
                )
            )
        }
    }

    // Notifications actions
    suspend fun markNotificationAsRead(id: String) {
        dao.markNotificationAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() {
        dao.markAllNotificationsAsRead()
    }

    suspend fun sendSampleNotification(title: String, message: String, type: String = "PROMO") {
        dao.insertNotification(
            AppNotification(
                id = UUID.randomUUID().toString(),
                title = title,
                message = message,
                type = type
            )
        )
    }

    fun seedInitialDataIfEmpty(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            // Seed a welcome notification if notifications table is empty
            dao.insertNotification(
                AppNotification(
                    id = "notif_welcome",
                    title = "Welcome to You B Tech Shopping!",
                    message = "Explore the newest next-gen tech drops, premium gadgets, and exclusive discounts.",
                    type = "DROP",
                    timestamp = System.currentTimeMillis() - 3600000
                )
            )
            dao.insertNotification(
                AppNotification(
                    id = "notif_flash_deal",
                    title = "Flash Sale Alert: 20% OFF Audio",
                    message = "Use promo code TECHPRO20 for an instant 20% discount on AeroPro Max headphones today!",
                    type = "PROMO",
                    timestamp = System.currentTimeMillis() - 7200000
                )
            )
        }
    }

    companion object {
        @Volatile
        private var instance: ShopRepository? = null

        fun getInstance(context: Context): ShopRepository {
            return instance ?: synchronized(this) {
                val db = ShopDatabase.getInstance(context)
                val repo = ShopRepository(db.shopDao())
                instance = repo
                repo
            }
        }
    }
}

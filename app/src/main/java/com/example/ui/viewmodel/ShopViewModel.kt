package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppNotification
import com.example.data.model.CartItemWithProduct
import com.example.data.model.OrderEntity
import com.example.data.model.Product
import com.example.data.repository.ShopRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOption(val title: String) {
    FEATURED("Featured"),
    PRICE_LOW_HIGH("Price: Low to High"),
    PRICE_HIGH_LOW("Price: High to Low"),
    TOP_RATED("Highest Rated"),
    NEWEST("New Arrivals")
}

data class FilterUiState(
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val selectedBrand: String = "All",
    val maxPrice: Float = 2500f,
    val minRating: Float = 0f,
    val onlyInStock: Boolean = false,
    val sortOption: SortOption = SortOption.FEATURED
)

class ShopViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ShopRepository.getInstance(application)

    // Repository flows
    val allProducts = repository.products
    val categories = repository.categories
    val brands = repository.brands

    val cartItems: StateFlow<List<CartItemWithProduct>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlistIds: StateFlow<Set<String>> = repository.wishlistIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val orders: StateFlow<List<OrderEntity>> = repository.orders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<AppNotification>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter and Search state
    private val _filterState = MutableStateFlow(FilterUiState())
    val filterState: StateFlow<FilterUiState> = _filterState.asStateFlow()

    // Filtered Products
    val filteredProducts: StateFlow<List<Product>> = combine(
        MutableStateFlow(repository.products),
        _filterState
    ) { products, filters ->
        var list = products

        // Search query
        if (filters.searchQuery.isNotBlank()) {
            val query = filters.searchQuery.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(query) ||
                it.category.lowercase().contains(query) ||
                it.brand.lowercase().contains(query) ||
                it.description.lowercase().contains(query)
            }
        }

        // Category
        if (filters.selectedCategory != "All") {
            list = list.filter { it.category.equals(filters.selectedCategory, ignoreCase = true) }
        }

        // Brand
        if (filters.selectedBrand != "All") {
            list = list.filter { it.brand.equals(filters.selectedBrand, ignoreCase = true) }
        }

        // Price
        list = list.filter { it.price <= filters.maxPrice }

        // Rating
        if (filters.minRating > 0f) {
            list = list.filter { it.rating >= filters.minRating }
        }

        // Stock
        if (filters.onlyInStock) {
            list = list.filter { it.inStock }
        }

        // Sorting
        when (filters.sortOption) {
            SortOption.FEATURED -> list.sortedByDescending { it.isFeatured }
            SortOption.PRICE_LOW_HIGH -> list.sortedBy { it.price }
            SortOption.PRICE_HIGH_LOW -> list.sortedByDescending { it.price }
            SortOption.TOP_RATED -> list.sortedByDescending { it.rating }
            SortOption.NEWEST -> list.sortedByDescending { it.id }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.products)

    // Promo code
    private val _appliedPromoCode = MutableStateFlow<String?>(null)
    val appliedPromoCode: StateFlow<String?> = _appliedPromoCode.asStateFlow()

    private val _promoDiscountPercent = MutableStateFlow(0)
    val promoDiscountPercent: StateFlow<Int> = _promoDiscountPercent.asStateFlow()

    // UI Feedback Event
    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    // Notification settings
    val notifyNewDrops = MutableStateFlow(true)
    val notifyStockAlerts = MutableStateFlow(true)
    val notifyOrderUpdates = MutableStateFlow(true)
    val notifyExclusiveDiscounts = MutableStateFlow(true)

    init {
        repository.seedInitialDataIfEmpty(viewModelScope)
    }

    // Filter controls
    fun updateSearchQuery(query: String) {
        _filterState.value = _filterState.value.copy(searchQuery = query)
    }

    fun selectCategory(category: String) {
        _filterState.value = _filterState.value.copy(selectedCategory = category)
    }

    fun selectBrand(brand: String) {
        _filterState.value = _filterState.value.copy(selectedBrand = brand)
    }

    fun updateMaxPrice(price: Float) {
        _filterState.value = _filterState.value.copy(maxPrice = price)
    }

    fun updateMinRating(rating: Float) {
        _filterState.value = _filterState.value.copy(minRating = rating)
    }

    fun toggleInStockOnly(inStock: Boolean) {
        _filterState.value = _filterState.value.copy(onlyInStock = inStock)
    }

    fun setSortOption(sort: SortOption) {
        _filterState.value = _filterState.value.copy(sortOption = sort)
    }

    fun resetFilters() {
        _filterState.value = FilterUiState()
    }

    // Cart operations
    fun addToCart(productId: String, quantity: Int = 1, color: String = "Space Gray") {
        viewModelScope.launch {
            repository.addToCart(productId, quantity, color)
            _snackbarEvent.emit("Added to cart!")
        }
    }

    fun updateCartQuantity(cartItemId: String, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(cartItemId, quantity)
        }
    }

    fun removeFromCart(cartItemId: String) {
        viewModelScope.launch {
            repository.removeCartItem(cartItemId)
            _snackbarEvent.emit("Item removed from cart")
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
            _snackbarEvent.emit("Cart cleared")
        }
    }

    // Promo code logic
    fun applyPromoCode(code: String): Boolean {
        val trimmed = code.trim().uppercase()
        return when (trimmed) {
            "TECHPRO20" -> {
                _appliedPromoCode.value = trimmed
                _promoDiscountPercent.value = 20
                viewModelScope.launch { _snackbarEvent.emit("Promo TECHPRO20 applied: 20% OFF!") }
                true
            }
            "YOUBTECH" -> {
                _appliedPromoCode.value = trimmed
                _promoDiscountPercent.value = 15
                viewModelScope.launch { _snackbarEvent.emit("Promo YOUBTECH applied: 15% OFF!") }
                true
            }
            "WELCOME10" -> {
                _appliedPromoCode.value = trimmed
                _promoDiscountPercent.value = 10
                viewModelScope.launch { _snackbarEvent.emit("Promo WELCOME10 applied: 10% OFF!") }
                true
            }
            else -> {
                viewModelScope.launch { _snackbarEvent.emit("Invalid promo code. Try TECHPRO20 or YOUBTECH") }
                false
            }
        }
    }

    fun removePromoCode() {
        _appliedPromoCode.value = null
        _promoDiscountPercent.value = 0
    }

    // Wishlist operations
    fun toggleWishlist(productId: String) {
        val isWishlisted = wishlistIds.value.contains(productId)
        viewModelScope.launch {
            repository.toggleWishlist(productId, isWishlisted)
            val msg = if (isWishlisted) "Removed from wishlist" else "Added to wishlist ❤️"
            _snackbarEvent.emit(msg)
        }
    }

    fun moveWishlistToCart(productId: String) {
        viewModelScope.launch {
            repository.addToCart(productId, 1)
            repository.toggleWishlist(productId, true)
            _snackbarEvent.emit("Moved item to Cart!")
        }
    }

    // Checkout & Orders
    fun placeOrder(
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
        itemsSummary: String,
        onSuccess: (OrderEntity) -> Unit
    ) {
        viewModelScope.launch {
            val order = repository.createOrder(
                customerName = customerName,
                email = email,
                phone = phone,
                shippingAddress = shippingAddress,
                billingAddress = billingAddress,
                paymentMethod = paymentMethod,
                subtotal = subtotal,
                discount = discount,
                tax = tax,
                total = total,
                itemsSummary = itemsSummary
            )
            removePromoCode()
            onSuccess(order)
        }
    }

    fun advanceOrderStatus(orderId: String, currentStatus: String) {
        val nextStatus = when (currentStatus) {
            "PLACED" -> "CONFIRMED"
            "CONFIRMED" -> "SHIPPED"
            "SHIPPED" -> "OUT_FOR_DELIVERY"
            "OUT_FOR_DELIVERY" -> "DELIVERED"
            else -> "DELIVERED"
        }
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, nextStatus)
            _snackbarEvent.emit("Order #$orderId is now $nextStatus")
        }
    }

    // Notification operations
    fun markNotificationAsRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
            _snackbarEvent.emit("All notifications marked as read")
        }
    }

    fun triggerTestNotification(type: String) {
        viewModelScope.launch {
            when (type) {
                "DROP" -> repository.sendSampleNotification(
                    title = "🔥 New Drop: Quantum XR Glass Headset",
                    message = "Exclusive pre-order is now live for You B Tech VIPs!",
                    type = "DROP"
                )
                "STOCK" -> repository.sendSampleNotification(
                    title = "📦 Back in Stock: Zenith X1 Pro Laptop",
                    message = "The 32GB RAM edition is back in stock with express delivery.",
                    type = "STOCK"
                )
                "PROMO" -> repository.sendSampleNotification(
                    title = "⚡ Weekend Cyber Sale: Up to 35% OFF",
                    message = "Massive savings on mechanical keyboards and gaming peripherals.",
                    type = "PROMO"
                )
            }
            _snackbarEvent.emit("Simulated push notification received!")
        }
    }

    fun getProductById(id: String): Product? = repository.getProductById(id)
}

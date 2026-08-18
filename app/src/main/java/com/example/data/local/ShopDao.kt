package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AppNotification
import com.example.data.model.CartItemEntity
import com.example.data.model.OrderEntity
import com.example.data.model.WishlistItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {
    // Cart
    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCartItem(item: CartItemEntity)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE cartItemId = :cartItemId")
    suspend fun updateCartQuantity(cartItemId: String, quantity: Int)

    @Query("DELETE FROM cart_items WHERE cartItemId = :cartItemId")
    suspend fun deleteCartItem(cartItemId: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    // Wishlist
    @Query("SELECT * FROM wishlist_items ORDER BY addedAt DESC")
    fun getAllWishlistItems(): Flow<List<WishlistItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWishlist(item: WishlistItemEntity)

    @Query("DELETE FROM wishlist_items WHERE productId = :productId")
    suspend fun removeFromWishlist(productId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist_items WHERE productId = :productId)")
    fun isInWishlist(productId: String): Flow<Boolean>

    // Orders
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE orderId = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: String): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<AppNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotification)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    @Query("DELETE FROM notifications")
    suspend fun clearNotifications()
}

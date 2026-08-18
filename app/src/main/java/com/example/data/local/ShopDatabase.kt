package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AppNotification
import com.example.data.model.CartItemEntity
import com.example.data.model.OrderEntity
import com.example.data.model.WishlistItemEntity

@Database(
    entities = [
        CartItemEntity::class,
        WishlistItemEntity::class,
        OrderEntity::class,
        AppNotification::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ShopDatabase : RoomDatabase() {
    abstract fun shopDao(): ShopDao

    companion object {
        @Volatile
        private var INSTANCE: ShopDatabase? = null

        fun getInstance(context: Context): ShopDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ShopDatabase::class.java,
                    "youbtech_shop.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

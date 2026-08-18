package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Product
import com.example.ui.components.ProductCard
import com.example.ui.components.TechSearchBar
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandCardBorder
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandSurface
import com.example.ui.theme.BrandSurfaceVariant
import com.example.ui.theme.BrandText
import com.example.ui.theme.BrandTextSecondary
import com.example.ui.viewmodel.ShopViewModel

@Composable
fun HomeScreen(
    viewModel: ShopViewModel,
    onNavigateToBrowse: () -> Unit,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToNotifications: () -> Unit,
    onOpenFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filterState by viewModel.filterState.collectAsState()
    val wishlistIds by viewModel.wishlistIds.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val unreadNotifsCount = notifications.count { !it.isRead }
    
    val allProducts = viewModel.allProducts
    val featuredProducts = allProducts.filter { it.isFeatured }
    val trendingProducts = allProducts.filter { it.isTrending }
    val categories = viewModel.categories

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(com.example.ui.theme.BrandBackground),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // App Top Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(BrandPrimary, BrandAccent)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "You B Tech",
                            color = BrandText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "Next-Gen Mobile Shopping",
                            color = BrandAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Notification Bell
                IconButton(
                    onClick = onNavigateToNotifications,
                    modifier = Modifier
                        .testTag("home_notification_btn")
                        .background(BrandSurface, CircleShape)
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadNotifsCount > 0) {
                                Badge(
                                    containerColor = BrandAccent,
                                    contentColor = Color.Black
                                ) {
                                    Text(
                                        text = "$unreadNotifsCount",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = BrandText
                        )
                    }
                }
            }
        }

        // Search Bar
        item {
            TechSearchBar(
                query = filterState.searchQuery,
                onQueryChange = {
                    viewModel.updateSearchQuery(it)
                    if (it.isNotEmpty()) onNavigateToBrowse()
                },
                onFilterClick = onOpenFilter
            )
        }

        // Hero Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable {
                        viewModel.selectCategory("Audio")
                        onNavigateToBrowse()
                    },
                colors = CardDefaults.cardColors(containerColor = BrandSurface)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF1E3A8A),
                                    Color(0xFF0F172A),
                                    BrandSurface
                                )
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1.2f)) {
                            Surface(
                                color = BrandAccent.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalOffer,
                                        contentDescription = null,
                                        tint = BrandAccent,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "USE CODE: TECHPRO20",
                                        color = BrandAccent,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Cyber Tech Drop",
                                color = BrandText,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Up to 25% off on Pro Audio, Laptops & Smart Tech",
                                color = BrandTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(0.8f)
                                .height(130.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&q=80",
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }

        // Category Pills
        item {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Text(
                    text = "Categories",
                    color = BrandText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = filterState.selectedCategory == category
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.selectCategory(category)
                                    onNavigateToBrowse()
                                },
                            color = if (isSelected) BrandPrimary else BrandSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) BrandPrimary else BrandCardBorder
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = category,
                                color = if (isSelected) Color.White else BrandTextSecondary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Trending Products Row
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = BrandAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Trending Now",
                            color = BrandText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "See All",
                        color = BrandAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onNavigateToBrowse() }
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(trendingProducts) { product ->
                        ProductCard(
                            product = product,
                            isWishlisted = wishlistIds.contains(product.id),
                            onProductClick = { onNavigateToProduct(product.id) },
                            onWishlistToggle = { viewModel.toggleWishlist(product.id) },
                            onAddToCart = { viewModel.addToCart(product.id) },
                            modifier = Modifier.width(180.dp)
                        )
                    }
                }
            }
        }

        // Featured Products Section
        item {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Featured Electronics",
                            color = BrandText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Grid layout inside LazyColumn via chunked row items
                val chunked = featuredProducts.chunked(2)
                chunked.forEach { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { product ->
                            ProductCard(
                                product = product,
                                isWishlisted = wishlistIds.contains(product.id),
                                onProductClick = { onNavigateToProduct(product.id) },
                                onWishlistToggle = { viewModel.toggleWishlist(product.id) },
                                onAddToCart = { viewModel.addToCart(product.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

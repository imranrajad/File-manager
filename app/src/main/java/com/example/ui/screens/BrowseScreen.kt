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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ProductCard
import com.example.ui.components.TechSearchBar
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandCardBorder
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandSurface
import com.example.ui.theme.BrandSurfaceVariant
import com.example.ui.theme.BrandText
import com.example.ui.theme.BrandTextSecondary
import com.example.ui.viewmodel.ShopViewModel

@Composable
fun BrowseScreen(
    viewModel: ShopViewModel,
    onNavigateToProduct: (String) -> Unit,
    onOpenFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filterState by viewModel.filterState.collectAsState()
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val wishlistIds by viewModel.wishlistIds.collectAsState()
    val categories = viewModel.categories

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBackground)
    ) {
        // Search Bar & Filter trigger
        TechSearchBar(
            query = filterState.searchQuery,
            onQueryChange = { viewModel.updateSearchQuery(it) },
            onFilterClick = onOpenFilter
        )

        // Categories quick scroller
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = filterState.selectedCategory == cat
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { viewModel.selectCategory(cat) }
                        .testTag("browse_category_$cat"),
                    color = if (isSelected) BrandPrimary else BrandSurface,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) BrandPrimary else BrandCardBorder
                    )
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) Color.White else BrandTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Active filters indicators & count row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredProducts.size} Products found",
                color = BrandTextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onOpenFilter() }
                    .background(BrandSurfaceVariant, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = BrandAccent,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = filterState.sortOption.title,
                    color = BrandText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Products Grid or Empty State
        if (filteredProducts.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.SearchOff,
                title = "No Products Found",
                subtitle = "We couldn't find any tech items matching your filters or search query.",
                actionText = "Reset Filters",
                onActionClick = { viewModel.resetFilters() }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp, top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val chunked = filteredProducts.chunked(2)
                items(chunked) { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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

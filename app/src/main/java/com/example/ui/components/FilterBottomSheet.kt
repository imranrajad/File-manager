package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandCardBorder
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandSurface
import com.example.ui.theme.BrandSurfaceVariant
import com.example.ui.theme.BrandText
import com.example.ui.theme.BrandTextSecondary
import com.example.ui.viewmodel.FilterUiState
import com.example.ui.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    filterState: FilterUiState,
    categories: List<String>,
    brands: List<String>,
    onApply: (FilterUiState) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    var tempCategory by remember { mutableStateOf(filterState.selectedCategory) }
    var tempBrand by remember { mutableStateOf(filterState.selectedBrand) }
    var tempMaxPrice by remember { mutableFloatStateOf(filterState.maxPrice) }
    var tempMinRating by remember { mutableFloatStateOf(filterState.minRating) }
    var tempInStock by remember { mutableStateOf(filterState.onlyInStock) }
    var tempSort by remember { mutableStateOf(filterState.sortOption) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BrandSurface,
        scrimColor = Color.Black.copy(alpha = 0.65f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter Products",
                    color = BrandText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = BrandTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sort Options
            Text(
                text = "Sort By",
                color = BrandText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SortOption.values().forEach { option ->
                    FilterChip(
                        selected = tempSort == option,
                        onClick = { tempSort = option },
                        label = { Text(text = option.title, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = BrandSurfaceVariant,
                            labelColor = BrandTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = BrandCardBorder,
                            selectedBorderColor = BrandPrimary,
                            enabled = true,
                            selected = tempSort == option
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Price Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Max Price",
                    color = BrandText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$${tempMaxPrice.toInt()}",
                    color = BrandAccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Slider(
                value = tempMaxPrice,
                onValueChange = { tempMaxPrice = it },
                valueRange = 50f..2500f,
                colors = SliderDefaults.colors(
                    thumbColor = BrandAccent,
                    activeTrackColor = BrandPrimary,
                    inactiveTrackColor = BrandSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category Chips
            Text(
                text = "Category",
                color = BrandText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = tempCategory == cat,
                        onClick = { tempCategory = cat },
                        label = { Text(text = cat, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = BrandSurfaceVariant,
                            labelColor = BrandTextSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Brand Chips
            Text(
                text = "Brand",
                color = BrandText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                brands.forEach { brand ->
                    FilterChip(
                        selected = tempBrand == brand,
                        onClick = { tempBrand = brand },
                        label = { Text(text = brand, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandAccent,
                            selectedLabelColor = Color.Black,
                            containerColor = BrandSurfaceVariant,
                            labelColor = BrandTextSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rating Filter
            Text(
                text = "Minimum Rating",
                color = BrandText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(0f to "Any", 4.0f to "4.0★+", 4.5f to "4.5★+", 4.8f to "4.8★+").forEach { (rate, label) ->
                    FilterChip(
                        selected = tempMinRating == rate,
                        onClick = { tempMinRating = rate },
                        label = { Text(text = label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = BrandSurfaceVariant,
                            labelColor = BrandTextSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // In Stock Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Show In-Stock Only",
                    color = BrandText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Switch(
                    checked = tempInStock,
                    onCheckedChange = { tempInStock = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = BrandPrimary,
                        uncheckedTrackColor = BrandSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        tempCategory = "All"
                        tempBrand = "All"
                        tempMaxPrice = 2500f
                        tempMinRating = 0f
                        tempInStock = false
                        tempSort = SortOption.FEATURED
                        onReset()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandTextSecondary)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset")
                }

                Button(
                    onClick = {
                        onApply(
                            filterState.copy(
                                selectedCategory = tempCategory,
                                selectedBrand = tempBrand,
                                maxPrice = tempMaxPrice,
                                minRating = tempMinRating,
                                onlyInStock = tempInStock,
                                sortOption = tempSort
                            )
                        )
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .testTag("apply_filters_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("Apply Filters", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

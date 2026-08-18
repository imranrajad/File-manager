package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.components.FilterBottomSheet
import com.example.ui.screens.BrowseScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotificationScreen
import com.example.ui.screens.OrderTrackingScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.WishlistScreen
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandBackground
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSurface
import com.example.ui.theme.BrandText
import com.example.ui.theme.BrandTextSecondary
import com.example.ui.theme.YouBTechTheme
import com.example.ui.viewmodel.ShopViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Browse : Screen("browse", "Browse", Icons.Default.Search)
    object Cart : Screen("cart", "Cart", Icons.Default.ShoppingCart)
    object Wishlist : Screen("wishlist", "Wishlist", Icons.Default.Favorite)
    object Tracking : Screen("tracking", "Orders", Icons.Default.LocalShipping)
    object Notifications : Screen("notifications", "Alerts", Icons.Default.Notifications)
    object ProductDetail : Screen("product_detail/{productId}", "Detail", Icons.Default.Home) {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object Checkout : Screen("checkout", "Checkout", Icons.Default.ShoppingCart)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YouBTechTheme {
                YouBTechApp()
            }
        }
    }
}

@Composable
fun YouBTechApp(viewModel: ShopViewModel = viewModel()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val snackbarHostState = remember { SnackbarHostState() }
    var showFilterSheet by remember { mutableStateOf(false) }

    val filterState by viewModel.filterState.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val wishlistIds by viewModel.wishlistIds.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    val totalCartCount = cartItems.sumOf { it.cartItem.quantity }
    val wishlistCount = wishlistIds.size
    val unreadNotifsCount = notifications.count { !it.isRead }

    // Listen for snackbar events
    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Browse,
        Screen.Cart,
        Screen.Wishlist,
        Screen.Tracking
    )

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Browse.route,
        Screen.Cart.route,
        Screen.Wishlist.route,
        Screen.Tracking.route,
        Screen.Notifications.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = BrandSurface,
                    contentColor = BrandText,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = {
                                when (screen) {
                                    Screen.Cart -> {
                                        BadgedBox(
                                            badge = {
                                                if (totalCartCount > 0) {
                                                    Badge(containerColor = BrandPrimary, contentColor = Color.White) {
                                                        Text("$totalCartCount", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(screen.icon, contentDescription = screen.title)
                                        }
                                    }
                                    Screen.Wishlist -> {
                                        BadgedBox(
                                            badge = {
                                                if (wishlistCount > 0) {
                                                    Badge(containerColor = BrandAccent, contentColor = Color.Black) {
                                                        Text("$wishlistCount", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        ) {
                                            Icon(screen.icon, contentDescription = screen.title)
                                        }
                                    }
                                    else -> {
                                        Icon(screen.icon, contentDescription = screen.title)
                                    }
                                }
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BrandAccent,
                                selectedTextColor = BrandAccent,
                                indicatorColor = BrandPrimary.copy(alpha = 0.2f),
                                unselectedIconColor = BrandTextSecondary,
                                unselectedTextColor = BrandTextSecondary
                            ),
                            modifier = Modifier.testTag("nav_tab_${screen.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BrandBackground)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToBrowse = {
                            navController.navigate(Screen.Browse.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                            }
                        },
                        onNavigateToProduct = { id ->
                            navController.navigate(Screen.ProductDetail.createRoute(id))
                        },
                        onNavigateToNotifications = {
                            navController.navigate(Screen.Notifications.route)
                        },
                        onOpenFilter = { showFilterSheet = true }
                    )
                }

                composable(Screen.Browse.route) {
                    BrowseScreen(
                        viewModel = viewModel,
                        onNavigateToProduct = { id ->
                            navController.navigate(Screen.ProductDetail.createRoute(id))
                        },
                        onOpenFilter = { showFilterSheet = true }
                    )
                }

                composable(Screen.Cart.route) {
                    CartScreen(
                        viewModel = viewModel,
                        onNavigateToCheckout = {
                            navController.navigate(Screen.Checkout.route)
                        },
                        onNavigateToBrowse = {
                            navController.navigate(Screen.Browse.route)
                        }
                    )
                }

                composable(Screen.Wishlist.route) {
                    WishlistScreen(
                        viewModel = viewModel,
                        onNavigateToProduct = { id ->
                            navController.navigate(Screen.ProductDetail.createRoute(id))
                        },
                        onNavigateToBrowse = {
                            navController.navigate(Screen.Browse.route)
                        }
                    )
                }

                composable(
                    route = Screen.Tracking.route + "?orderId={orderId}",
                    arguments = listOf(navArgument("orderId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    })
                ) { backStackEntry ->
                    val orderId = backStackEntry.arguments?.getString("orderId")
                    OrderTrackingScreen(
                        viewModel = viewModel,
                        highlightOrderId = orderId,
                        onNavigateToBrowse = {
                            navController.navigate(Screen.Browse.route)
                        }
                    )
                }

                composable(Screen.Tracking.route) {
                    OrderTrackingScreen(
                        viewModel = viewModel,
                        highlightOrderId = null,
                        onNavigateToBrowse = {
                            navController.navigate(Screen.Browse.route)
                        }
                    )
                }

                composable(Screen.Notifications.route) {
                    NotificationScreen(viewModel = viewModel)
                }

                composable(
                    route = Screen.ProductDetail.route,
                    arguments = listOf(navArgument("productId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val productId = backStackEntry.arguments?.getString("productId") ?: ""
                    ProductDetailScreen(
                        productId = productId,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onNavigateToCart = {
                            navController.navigate(Screen.Cart.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            }
                        }
                    )
                }

                composable(Screen.Checkout.route) {
                    CheckoutScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onOrderPlaced = { placedOrderId ->
                            navController.navigate("${Screen.Tracking.route}?orderId=$placedOrderId") {
                                popUpTo(Screen.Home.route)
                            }
                        }
                    )
                }
            }

            // Filter bottom sheet
            if (showFilterSheet) {
                FilterBottomSheet(
                    filterState = filterState,
                    categories = viewModel.categories,
                    brands = viewModel.brands,
                    onApply = { newFilter ->
                        viewModel.selectCategory(newFilter.selectedCategory)
                        viewModel.selectBrand(newFilter.selectedBrand)
                        viewModel.updateMaxPrice(newFilter.maxPrice)
                        viewModel.updateMinRating(newFilter.minRating)
                        viewModel.toggleInStockOnly(newFilter.onlyInStock)
                        viewModel.setSortOption(newFilter.sortOption)
                    },
                    onReset = {
                        viewModel.resetFilters()
                    },
                    onDismiss = { showFilterSheet = false }
                )
            }
        }
    }
}

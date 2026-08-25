package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.model.NavTab
import com.example.util.AppStrings

data class NavItem(
    val tab: NavTab,
    val labelKey: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val NAV_ITEMS = listOf(
    NavItem(NavTab.CALCULATOR, "calculator", Icons.Filled.Calculate, Icons.Outlined.Calculate),
    NavItem(NavTab.CONVERTER, "converter", Icons.Filled.SwapHoriz, Icons.Outlined.SwapHoriz),
    NavItem(NavTab.FINANCE, "finance", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
    NavItem(NavTab.MATH, "math", Icons.Filled.SquareFoot, Icons.Outlined.SquareFoot),
    NavItem(NavTab.HISTORY, "history", Icons.Filled.History, Icons.Outlined.History),
    NavItem(NavTab.SETTINGS, "settings", Icons.Filled.Settings, Icons.Outlined.Settings)
)

@Composable
fun AppBottomNavigationBar(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    language: Language,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("bottom_nav_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        NAV_ITEMS.forEach { item ->
            val isSelected = currentTab == item.tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(item.tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = AppStrings.get(item.labelKey, language),
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = AppStrings.get(item.labelKey, language),
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("nav_item_${item.tab.name.lowercase()}")
            )
        }
    }
}

@Composable
fun AppNavigationRail(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    language: Language,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier
            .fillMaxHeight()
            .windowInsetsPadding(WindowInsets.statusBars)
            .testTag("nav_rail"),
        containerColor = MaterialTheme.colorScheme.surface,
        header = {
            Surface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(40.dp)
            ) {
                Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text(
                        text = "∑",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 20.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
            }
        }
    ) {
        NAV_ITEMS.forEach { item ->
            val isSelected = currentTab == item.tab
            NavigationRailItem(
                selected = isSelected,
                onClick = { onTabSelected(item.tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = AppStrings.get(item.labelKey, language),
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = AppStrings.get(item.labelKey, language),
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("rail_item_${item.tab.name.lowercase()}")
            )
        }
    }
}

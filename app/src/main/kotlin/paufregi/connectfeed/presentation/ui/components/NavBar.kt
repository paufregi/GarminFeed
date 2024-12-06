package paufregi.connectfeed.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import paufregi.connectfeed.presentation.Routes

data class NavItem(val route: Routes, val label: String, val icons: ImageVector)

fun NavBackStackEntry.isCurrent(route: Routes): Boolean =
    this.destination.hierarchy.any{ it.route.orEmpty().contains(route::class.qualifiedName.orEmpty()) }

@Preview
@Composable
fun NavBar(
    @PreviewParameter(NavBarPreview ::class) navItems: List<NavItem>,
    nav: NavController = rememberNavController()
) {
    val navBackStackEntry by nav.currentBackStackEntryAsState()

    NavigationBar {
        navItems.forEach { item ->
            NavigationBarItem(
                modifier = Modifier.testTag("nav_${item.label.lowercase()}"),
                selected = navBackStackEntry?.isCurrent(item.route) == true,
                onClick = { nav.navigate(item.route) },
                label = { Text(item.label) },
                alwaysShowLabel = true,
                icon = { Icon(item.icons, null) }
            )
        }
    }
}

private class NavBarPreview : PreviewParameterProvider<List<NavItem>> {
    override val values = sequenceOf(
        listOf(
            NavItem(Routes.Home, "Home", Icons.Default.Home),
            NavItem(Routes.Profiles, "Profiles", Icons.Default.Tune),
            NavItem(Routes.Settings, "Settings", Icons.Default.Settings),
        )
    )
}
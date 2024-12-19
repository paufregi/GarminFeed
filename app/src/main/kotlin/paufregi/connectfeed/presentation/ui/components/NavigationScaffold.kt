package paufregi.connectfeed.presentation.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import paufregi.connectfeed.presentation.Route

data class NavigationItem (
    val label: String,
    val icon: ImageVector,
    val route: Route,
)

@Composable
@ExperimentalMaterial3Api
fun NavigationScaffold(
    items: List<NavigationItem> = emptyList<NavigationItem>(),
    selectIndex: Int = 0,
    nav: NavController = rememberNavController(),
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    content: @Composable (PaddingValues) -> Unit = {}
) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                items.fastForEachIndexed { index, item ->
                    NavigationDrawerItem(
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        label = { Text(item.label) },
                        selected = index == selectIndex,
                        onClick = {
                            scope.launch { drawerState.close() }
                            nav.navigate(item.route)
                        },
                        icon = { Icon(item.icon, item.label) },
                    )
                }
            }
        }
    ) {
        Scaffold(
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = floatingActionButtonPosition,
            topBar = {
                TopAppBar(
                    title = { Text("Connect Feed") },
                    actions = {
                        Button(
                            icon = Icons.Filled.Menu,
                            onClick = { scope.launch { drawerState.open() } }
                        )
                    }
                )
            },
        ) { pv -> content(pv) }
    }
}
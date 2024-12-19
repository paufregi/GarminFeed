package paufregi.connectfeed.presentation.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import paufregi.connectfeed.presentation.Navigation
import paufregi.connectfeed.presentation.ui.components.Button
import paufregi.connectfeed.presentation.ui.components.ConfirmationDialog
import paufregi.connectfeed.presentation.ui.components.Loading
import paufregi.connectfeed.presentation.ui.components.NavigationScaffold
import paufregi.connectfeed.presentation.ui.components.SimpleScaffold
import paufregi.connectfeed.presentation.ui.components.StatusInfo
import paufregi.connectfeed.presentation.ui.components.StatusInfoType
import paufregi.connectfeed.presentation.ui.models.ProcessState

@Composable
@ExperimentalMaterial3Api
internal fun AccountScreen(nav: NavController = rememberNavController()) {
    val viewModel = hiltViewModel<AccountViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    AccountContent(state, viewModel::onEvent, nav)
}

@Preview
@Composable
@ExperimentalMaterial3Api
internal fun AccountContent(
    @PreviewParameter(AccountStatePreview::class) state: AccountState = AccountState(),
    onEvent: (AccountEvent) -> Unit = {},
    nav: NavController = rememberNavController()
) {
    when (state.process) {
        is ProcessState.Processing -> SimpleScaffold { Loading(it) }
        is ProcessState.Success -> SimpleScaffold {
            StatusInfo(
                type = StatusInfoType.Success,
                text = state.process.message,
                actionButton = { Button(text = "Ok", onClick = { onEvent(AccountEvent.Reset) }) },
                paddingValues = it
            )
        }
        is ProcessState.Failure -> SimpleScaffold {
            StatusInfo(
                type = StatusInfoType.Failure,
                text = state.process.reason,
                actionButton = { Button(text = "Ok", onClick = { onEvent(AccountEvent.Reset) }) },
                paddingValues = it
            )
        }
        is ProcessState.Idle -> NavigationScaffold(
            items = Navigation.items,
            selectIndex = Navigation.HOME,
            nav = nav
        ) { AccountForm(onEvent, it) }
    }
}

@Preview
@Composable
@ExperimentalMaterial3Api
internal fun AccountForm(
    onEvent: (AccountEvent) -> Unit = {},
    paddingValues: PaddingValues = PaddingValues(),
) {
    var signOutDialog by remember { mutableStateOf(false) }

    if (signOutDialog == true) {
        ConfirmationDialog(
            title = "Sign out",
            message = "Are you sure you want to sign out?",
            onConfirm = { onEvent(AccountEvent.SignOut) },
            onDismiss = { signOutDialog = false }
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Image(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile picture",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.size(200.dp)
            )
            Text(text = "Paul", fontSize = 24.sp)
        }

        Button(
            text = "Change password",
            onClick = {  }
        )
        Button(
            text = "Refresh tokens",
            onClick = { onEvent(AccountEvent.RefreshTokens) }
        )
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            text = "Sign out",
            onClick = { signOutDialog = true }
        )
    }
}

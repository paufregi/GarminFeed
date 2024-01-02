package paufregi.garminfeed.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.MutableStateFlow
import paufregi.garminfeed.models.Status
import paufregi.garminfeed.models.Weight
import paufregi.garminfeed.ui.components.FailedDialog
import paufregi.garminfeed.ui.components.LoadingDialog
import paufregi.garminfeed.ui.components.SuccessDialog
import paufregi.garminfeed.ui.preview.WeightsPreview
import paufregi.garminfeed.utils.Formatter
import java.util.Locale

@Preview
@Composable
@ExperimentalMaterial3Api
fun ImportScreen(
    @PreviewParameter(WeightsPreview::class) weights: List<Weight>,
    onSave: (List<Weight>) -> Unit = {},
    onComplete: () -> Unit = {},
    status: MutableStateFlow<Status?> = MutableStateFlow(null),
) {
    val locale = Locale.getDefault()
    val statusState = status.collectAsState()

    when (statusState.value) {
        is Status.Uploading -> LoadingDialog()
        is Status.Success -> SuccessDialog(onClick = onComplete)
        is Status.Fails -> FailedDialog(onClick = onComplete)
        else -> {}
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onSave(weights) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = "Save")
            }
        }
    ) {
        LazyColumn(
            contentPadding = it,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(weights) { w ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                )
                {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = Formatter.dayOfWeek(locale).format(w.timestamp),
                            fontSize = 20.sp,
                        )
                        Text(
                            text = Formatter.date(locale).format(w.timestamp),
                            fontSize = 12.sp
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonitorWeight,
                            contentDescription = "Weight"
                        )
                        Text(
                            text = "${Formatter.decimal.format(w.weight)} kg",
                            fontSize = 21.sp,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Icon(imageVector = Icons.Default.Fastfood, contentDescription = "Fat %")
                        Text(
                            text = "${Formatter.decimal.format(w.fat)} %",
                            fontSize = 21.sp,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }

    }
}

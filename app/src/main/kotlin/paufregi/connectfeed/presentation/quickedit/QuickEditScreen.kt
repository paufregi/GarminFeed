package paufregi.connectfeed.presentation.quickedit

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.MoodBad
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.presentation.ui.components.Button
import paufregi.connectfeed.presentation.ui.components.CustomSlider
import paufregi.connectfeed.presentation.ui.components.Dropdown
import paufregi.connectfeed.presentation.ui.components.IconRadioGroup
import paufregi.connectfeed.presentation.ui.components.IconRadioItem
import paufregi.connectfeed.presentation.ui.components.Loading
import paufregi.connectfeed.presentation.ui.components.StatusInfo
import paufregi.connectfeed.presentation.ui.components.StatusInfoType
import paufregi.connectfeed.presentation.ui.components.TextEffort
import paufregi.connectfeed.presentation.ui.components.TextFeel
import paufregi.connectfeed.presentation.ui.components.toDropdownItem
import paufregi.connectfeed.presentation.utils.ProcessState

@Composable
@ExperimentalMaterial3Api
internal fun QuickEditScreen(
    paddingValues: PaddingValues = PaddingValues(),
) {
    val viewModel = hiltViewModel<QuickEditViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    QuickEditContent(state, viewModel::onEvent, paddingValues)
}

@Preview
@Composable
@ExperimentalMaterial3Api
internal fun QuickEditContent(
    @PreviewParameter(QuickEditStatePreview ::class) state: QuickEditState,
    onEvent: (QuickEditEvent) -> Unit = {},
    paddingValues: PaddingValues = PaddingValues(),
) {
    when (state.loading) {
        ProcessState.Processing -> Loading(paddingValues)
        ProcessState.Failure -> StatusInfo(
            type = StatusInfoType.Failure,
            text = "Couldn't load activities",
            actionButton = { Button(text = "Retry", onClick = { onEvent(QuickEditEvent.Restart) }) } ,
            contentPadding = paddingValues)
        else -> {
            when (state.updating) {
                ProcessState.Processing -> Loading(paddingValues)
                ProcessState.Failure -> StatusInfo(
                        type = StatusInfoType.Failure,
                        text = "Couldn't update the activity",
                        actionButton = { Button(text = "Ok", onClick = { onEvent(QuickEditEvent.Restart) }) } ,
                        contentPadding = paddingValues)
                ProcessState.Success -> StatusInfo(
                    type = StatusInfoType.Success,
                    text = "Activity updated",
                    actionButton = { Button(text = "Ok", onClick = { onEvent(QuickEditEvent.Restart) }) } ,
                    contentPadding = paddingValues)
                else -> QuickEditForm(state, onEvent, paddingValues)
            }
        }
    }
}

@Preview
@Composable
@ExperimentalMaterial3Api
internal fun QuickEditForm(
    @PreviewParameter(QuickEditStatePreview ::class) state: QuickEditState,
    onEvent: (QuickEditEvent) -> Unit = {},
    paddingValues: PaddingValues = PaddingValues(),
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.width(IntrinsicSize.Min)
        ) {
            val interactionSource = remember { MutableInteractionSource() }

            Dropdown(
                label = { Text("Activity") },
                selected = state.selectedActivity?.toDropdownItem { },
                items = state.activities.map {
                    it.toDropdownItem {
                        onEvent(
                            QuickEditEvent.SelectActivity(
                                it
                            )
                        )
                    }
                }
            )

            Dropdown(
                label = { Text("Profile") },
                selected = state.selectedProfile?.toDropdownItem { },
                items = state.availableProfiles.map {
                    it.toDropdownItem {
                        onEvent(
                            QuickEditEvent.SelectProfile(
                                it
                            )
                        )
                    }
                }
            )

            if (state.selectedActivity?.type == ActivityType.Cycling) {
                Column {
                    IconRadioGroup(
                        options = listOf(
                            IconRadioItem(0f, Icons.Filled.MoodBad),
                            IconRadioItem(25f, Icons.Filled.SentimentVeryDissatisfied),
                            IconRadioItem(50f, Icons.Filled.SentimentNeutral),
                            IconRadioItem(75f, Icons.Filled.SentimentSatisfiedAlt),
                            IconRadioItem(100f, Icons.Filled.Mood),
                        ),
                        selected = state.selectedFeel,
                        onClick = { onEvent(QuickEditEvent.SelectFeel(it)) }
                    )
                    TextFeel(
                        state.selectedFeel,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                            .padding(vertical = 10.dp)
                    )
                }
                Column {
                    Slider(
                        value = state.selectedEffort,
                        onValueChange = {
                            onEvent(
                                QuickEditEvent.SelectEffort(
                                    it.toInt().toFloat()
                                )
                            )
                        },
                        valueRange = 0f..100f,
                        steps = 9,
                        interactionSource = interactionSource,
                        track = CustomSlider.track,
                        thumb = CustomSlider.thumb(interactionSource)
                    )
                    TextEffort(
                        state.selectedEffort,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    text = "Save",
                    enabled = state.selectedActivity != null && state.selectedProfile != null,
                    onClick = { onEvent(QuickEditEvent.Save) }
                )
            }
        }
    }
}


package paufregi.connectfeed.presentation.home

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class HomeStatePreview : PreviewParameterProvider<HomeState> {
    override val values = sequenceOf(
        HomeState(true),
        HomeState(false),
    )
}
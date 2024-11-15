package paufregi.connectfeed.presentation.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

object CustomSlider {

    @ExperimentalMaterial3Api
    val track: @Composable (SliderState) -> Unit = { sliderState -> SliderDefaults.Track(
            sliderState = sliderState,
            modifier = Modifier.height(10.dp),
            thumbTrackGapSize = 0.dp,
        )
    }

    @ExperimentalMaterial3Api
    fun thumb(interactionSource: MutableInteractionSource): @Composable (SliderState) -> Unit = {
        SliderDefaults.Thumb(
            interactionSource = interactionSource,
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape),
        )
    }
}
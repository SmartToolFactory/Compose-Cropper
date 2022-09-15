package com.smarttoolfactory.composecropper.properties

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.slider.ColorfulSlider
import com.smarttoolfactory.slider.MaterialSliderColors
import com.smarttoolfactory.slider.MaterialSliderDefaults
import com.smarttoolfactory.slider.SliderBrushColor

@Composable
internal fun SliderSelection(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    colors: MaterialSliderColors = MaterialSliderDefaults.materialColors(
        activeTrackColor = SliderBrushColor(MaterialTheme.colorScheme.primary)
    )
) {
    ColorfulSlider(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        colors = colors,
        trackHeight = 8.dp,
        thumbRadius = 14.dp
    )
}

@Composable
internal fun Title(
    text: String,
    fontSize: TextUnit = 20.sp
) {
    Text(
        modifier = Modifier.padding(vertical = 1.dp),
        text = text,
        color = MaterialTheme.colorScheme.primary,
        fontSize = fontSize,
        fontWeight = FontWeight.Bold
    )
}


@Composable
internal fun FullRowSwitch(
    label: String,
    state: Boolean,
    onStateChange: (Boolean) -> Unit
) {

    // Checkbox with text on right side
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            role = Role.Switch,
            onClick = {
                onStateChange(!state)
            }
        )
        .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(text = label, modifier = Modifier.weight(1f))

        Switch(
            checked = state,
            onCheckedChange = null
        )
    }
}

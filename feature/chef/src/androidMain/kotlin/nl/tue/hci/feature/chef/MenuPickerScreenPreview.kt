package nl.tue.hci.feature.chef

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import nl.tue.hci.feature.chef.pages.MenuPickerScreen


@Preview
@Composable
fun MenuPickerScreenPreview() {
    MenuPickerScreen(
        modifier = Modifier,
        onClose = { },
    )
}
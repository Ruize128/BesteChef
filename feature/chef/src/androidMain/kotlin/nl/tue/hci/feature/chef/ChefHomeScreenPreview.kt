package nl.tue.hci.feature.chef

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import nl.tue.hci.feature.chef.ui.theme.BesteChefTheme

@Preview(showBackground = true, name = "Chef Home Screen")
@Composable
fun ChefHomeScreenPreview() {
    BesteChefTheme {
        ChefHomeScreen(modifier = Modifier)
    }
}


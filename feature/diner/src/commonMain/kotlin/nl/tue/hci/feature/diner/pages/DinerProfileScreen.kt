package nl.tue.hci.feature.diner.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
// Preview removed for multiplatform
import androidx.compose.ui.unit.dp
import nl.tue.hci.core.ui.BesteChefThemeColors


@Composable
fun DinerProfileScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {}
) {
    val colors = BesteChefThemeColors.current()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Manage your diner profile and preferences.",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Logout button
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.dinerPrimary,
                contentColor = colors.textOnPrimary,
            ),
        ) {
            Text("Logout")
        }
    }
}


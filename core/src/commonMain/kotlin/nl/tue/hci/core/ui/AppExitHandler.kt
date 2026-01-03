package nl.tue.hci.core.ui

import androidx.compose.runtime.Composable

/**
 * A multiplatform activity exit handler that works across Android and Web platforms.
 * 
 * On Android: Finishes the activity stack
 * On Web: Can be extended to handle browser exit
 * 
 * @return A function that exits the app when called
 */
@Composable
expect fun rememberAppExitHandler(): () -> Unit

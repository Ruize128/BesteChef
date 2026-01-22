package nl.tue.hci.feature.chef

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import nl.tue.hci.feature.chef.pages.EditOrderScreen

@Preview
@Composable
fun EditOrderScreenPreview() {
    EditOrderScreen(
        orderId = "order_id",
        modifier = Modifier,
        onBackClick = { },
        onAddDishClick = { },
        itemsToAdd = null,
        onItemsAdded = { },
    )
}
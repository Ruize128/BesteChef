package nl.tue.hci.feature.diner.preview.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import nl.tue.hci.feature.diner.pages.PaymentSuccessfulScreen

@Preview(showBackground = true, name = "Payment Successful Screen")
@Composable
fun PaymentSuccessfulScreenPreview() {
    PaymentSuccessfulScreen(
        modifier = Modifier,
        onDoneClick = {}
    )
}


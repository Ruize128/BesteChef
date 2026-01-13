package nl.tue.hci.feature.diner.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun ImageCarouselWithPager(
    images: List<String>,
    currentIndex: Int,
    onIndexChange: (Int) -> Unit,
    contentDescription: String,
    modifier: Modifier,
    onImageClick: ((String) -> Unit)? = null
)


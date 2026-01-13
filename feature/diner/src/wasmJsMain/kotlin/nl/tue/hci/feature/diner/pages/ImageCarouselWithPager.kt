package nl.tue.hci.feature.diner.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import nl.tue.hci.core.ui.rememberImagePainter

@Composable
actual fun ImageCarouselWithPager(
    images: List<String>,
    currentIndex: Int,
    onIndexChange: (Int) -> Unit,
    contentDescription: String,
    modifier: Modifier,
    onImageClick: ((String) -> Unit)?
) {
    // Web implementation: Simple image display with manual swipe handling
    // For now, just show the current image
    // TODO: Implement proper swipeable carousel for web
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = rememberImagePainter(images[currentIndex]),
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(0.dp))
                .let { modifier ->
                    if (onImageClick != null) {
                        modifier.clickable { onImageClick(images[currentIndex]) }
                    } else {
                        modifier
                    }
                },
            contentScale = ContentScale.Crop
        )
        
        // Carousel indicators with background
        if (images.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
                    .background(
                        Color.Black.copy(alpha = 0.4f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                images.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (index == currentIndex) 
                                    Color.White 
                                else 
                                    Color.White.copy(alpha = 0.5f)
                            )
                    )
                }
            }
        }
    }
}


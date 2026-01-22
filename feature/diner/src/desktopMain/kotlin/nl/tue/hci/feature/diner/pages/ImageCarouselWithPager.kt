package nl.tue.hci.feature.diner.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import nl.tue.hci.core.ui.rememberImagePainter

/**
 * Desktop implementation of image carousel using HorizontalPager
 */
@Composable
actual fun ImageCarouselWithPager(
    images: List<String>,
    currentIndex: Int,
    onIndexChange: (Int) -> Unit,
    contentDescription: String,
    modifier: Modifier,
    onImageClick: ((String) -> Unit)?
) {
    val pagerState = rememberPagerState(pageCount = { images.size })
    
    LaunchedEffect(currentIndex) {
        if (pagerState.currentPage != currentIndex) {
            pagerState.scrollToPage(currentIndex)
        }
    }
    
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != currentIndex) {
            onIndexChange(pagerState.currentPage)
        }
    }
    
    Box(modifier = modifier) {
        if (images.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No images available", color = Color.Gray)
            }
        } else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val imagePainter = rememberImagePainter(images[page])
                Image(
                    painter = imagePainter,
                    contentDescription = contentDescription,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .let { modifier ->
                            if (onImageClick != null) {
                                modifier.clickable { onImageClick(images[page]) }
                            } else {
                                modifier
                            }
                        }
                )
            }
        }
        // Indicators (desktop) — show when more than one image
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
                                if (index == pagerState.currentPage) Color.White else Color.White.copy(alpha = 0.5f)
                            )
                    )
                }
            }
        }
    }
}

package org.example.project

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/**
 * Reusable AppCarousel composable component.
 * Accepts a list of DrawableResource images as parameter and displays an auto-scrolling
 * swipable image carousel with indicator dots.
 *
 * @param images List of DrawableResource items to display.
 * @param modifier Custom Modifier for the carousel container.
 * @param height Height of the carousel container (default 180.dp).
 * @param autoScrollDurationMs Duration between auto-scroll transitions in milliseconds (default 3000ms).
 * @param enableAutoScroll Enable or disable automatic paging (default true).
 * @param onImageClick Callback invoked when a carousel image is clicked, passing its index.
 */
@Composable
fun AppCarousel(
    images: List<DrawableResource>,
    modifier: Modifier = Modifier,
    height: Dp = 180.dp,
    autoScrollDurationMs: Long = 3000L,
    enableAutoScroll: Boolean = true,
    onImageClick: ((Int) -> Unit)? = null
) {
    if (images.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { images.size })

    // Auto-scroll loop effect
    if (enableAutoScroll && images.size > 1) {
        LaunchedEffect(pagerState) {
            while (true) {
                delay(autoScrollDurationMs)
                if (pagerState.pageCount > 0) {
                    val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                    pagerState.animateScrollToPage(nextPage)
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(16.dp))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Image(
                painter = painterResource(images[page]),
                contentDescription = "Carousel Image $page",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (onImageClick != null) {
                            Modifier.clickable { onImageClick(page) }
                        } else Modifier
                    )
            )
        }

        // Carousel Indicator Dots Overlay
        if (images.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.35f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(images.size) { iteration ->
                    val isSelected = pagerState.currentPage == iteration
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Color.White else Color.White.copy(alpha = 0.5f)
                            )
                    )
                }
            }
        }
    }
}

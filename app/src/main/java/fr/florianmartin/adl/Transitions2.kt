import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

@Composable
fun ThreeColumnsWithTransition2() {
    var isVisible by remember { mutableStateOf(true) }

    val spacing = 16.dp
    val fixedBlockHeight = 150.dp

    // Hauteur animée du bloc 1 (identique à ta version)
    val animatedHeight by animateDpAsState(
        targetValue = if (isVisible) 150.dp else 400.dp,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
    )

    // Offsets verticaux animés pour garder 16dp d’écart tout en restant dans une Box
    val secondTopY by animateDpAsState(
        targetValue = animatedHeight + spacing,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
    )
    val thirdTopY by animateDpAsState(
        targetValue = animatedHeight + spacing + fixedBlockHeight + spacing,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
    )

    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Bloc 1 (haut) — clique pour toggle
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedHeight)
                .clip(RoundedCornerShape(12.dp))
                .background(color = Color(0xFF6200EE))
                .clickable { isVisible = !isVisible }
                .align(Alignment.TopStart), // point d’ancrage haut-gauche
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Colonne 1 - Cliquez ici !",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        // Bloc 2 — empilé sous le 1er via offset animé (animations identiques conservées)
        AnimatedVisibility(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = 0,
                        y = with(density) { secondTopY.toPx().roundToInt() }
                    )
                }
                .align(Alignment.TopStart), // même ancrage
            visible = isVisible,
            enter = fadeIn(
                animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
            ) + slideInVertically(
                animationSpec = tween(durationMillis = 1000),
                initialOffsetY = { fullHeight -> fullHeight * 4 }
            ),
            exit = slideOutVertically(
                animationSpec = tween(durationMillis = 1500),
                targetOffsetY = { fullHeight -> fullHeight * 4 }
            ) + fadeOut(
                animationSpec = tween(durationMillis = 2000)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(fixedBlockHeight)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color = Color(0xFF03DAC6)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Colonne 2",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Bloc 3 — empilé sous le 2e via offset animé, avec zIndex pour passer par-dessus si besoin
        AnimatedVisibility(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = 0,
                        y = with(density) { thirdTopY.toPx().roundToInt() }
                    )
                }
                .align(Alignment.TopStart)
                .zIndex(1f), // au-dessus du bloc 2 pendant les transitions
            visible = isVisible,
            enter = fadeIn(
                animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
            ) + slideInVertically(
                animationSpec = tween(durationMillis = 1500),
                initialOffsetY = { fullHeight -> fullHeight * 3 }
            ),
            exit = slideOutVertically(
                animationSpec = tween(durationMillis = 1500, delayMillis = 150),
                targetOffsetY = { fullHeight -> fullHeight * 3 }
            ) + fadeOut(
                animationSpec = tween(durationMillis = 2000)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(fixedBlockHeight)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color = Color(0xFFFF5722)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Colonne 3",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ThreeColumnsWithTransition2Preview() {
    ThreeColumnsWithTransition2()
}
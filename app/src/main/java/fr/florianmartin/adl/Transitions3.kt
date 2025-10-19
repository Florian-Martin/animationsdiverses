@file:OptIn(ExperimentalAnimationApi::class)

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

@Composable
fun DirectionalExpandableListCentered(
    items: List<String> = List(8) { "Item ${it + 1}" },
    collapsedHeight: Int = 150,
    expandedHeight: Int = 400,
    spacingDp: Int = 16,
    selectedWidthFraction: Float = 0.92f, // centrage horizontal de l’item ouvert
) {
    val listState = rememberLazyListState()
    val density = LocalDensity.current

    var expandedIndex by remember { mutableIntStateOf(-1) }             // -1 = aucun ouvert
    var lastExpandedIndex by rememberSaveable { mutableIntStateOf(-1) } // sert au sens des slideIn quand on referme

    // mémorise le dernier ouvert (utile pour l’animation de retour)
    LaunchedEffect(expandedIndex) {
        if (expandedIndex != -1) lastExpandedIndex = expandedIndex
    }

    // Centrer VERTICALEMENT l’item sélectionné quand il s’ouvre
    LaunchedEffect(expandedIndex) {
        if (expandedIndex >= 0) {
            // laisse démarrer les animations d’exit et de croissance pour avoir de meilleures positions perçues
            delay(120)
            val viewportPx = listState.layoutInfo.viewportSize.height
            val contentPaddingTopPx = with(density) { spacingDp.dp.toPx() }
            val expandedHeightPx = with(density) { expandedHeight.dp.toPx() }

            // position souhaitée du haut de l’item pour avoir son centre au milieu de l’écran
            val desiredTopPx = (viewportPx - expandedHeightPx) / 2f
            // l’offset d’animateScrollToItem est relatif AU-DESSUS du contenu (après padding),
            // donc on retire le padding top.
            val offsetPx = (desiredTopPx - contentPaddingTopPx).toInt()

            // scroll animé vers l’index cliqué avec offset pour centrer
            listState.animateScrollToItem(expandedIndex, offsetPx)
        }
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(all = spacingDp.dp),
        verticalArrangement = Arrangement.spacedBy(spacingDp.dp)
    ) {
        itemsIndexed(items, key = { idx, _ -> idx }) { index, label ->
            val isSelected = expandedIndex == index
            val isAnyExpanded = expandedIndex != -1

            val heightDp by animateDpAsState(
                targetValue = if (isSelected) expandedHeight.dp else collapsedHeight.dp,
                animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
                label = "cardHeight"
            )

            val enter = enterTransitionFor(index, isAnyExpanded, lastExpandedIndex)
            val exit = exitTransitionFor(index, expandedIndex)

            AnimatedVisibility(
                visible = !isAnyExpanded || isSelected,
                enter = enter,
                exit = exit
            ) {
                // Wrapper pour centrage HORIZONTAL de la carte ouverte
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .zIndex(if (isSelected) 1f else 0f) // l’item ouvert passe au-dessus
                            .fillMaxWidth(if (isSelected) selectedWidthFraction else 1f)
                            .height(heightDp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colorForIndex(index))
                            .clickable {
                                expandedIndex = if (isSelected) -1 else index
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isSelected) "$label (ouvert)" else label,
                            color = if (index % 2 == 0) Color.White else Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

/** Réapparition directionnelle quand on referme. */
private fun enterTransitionFor(
    index: Int,
    isAnyCurrentlyExpanded: Boolean,
    lastExpandedIndex: Int
): EnterTransition {
    if (isAnyCurrentlyExpanded) {
        // Pendant qu’un item est ouvert, les autres sont invisibles ; l’enter ne s’applique qu’au retour.
        return fadeIn(animationSpec = tween(150, easing = LinearEasing))
    }
    return if (lastExpandedIndex != -1) {
        if (index < lastExpandedIndex) {
            slideInVertically(
                animationSpec = tween(350, easing = FastOutSlowInEasing),
                initialOffsetY = { -it }
            ) + fadeIn(animationSpec = tween(250))
        } else {
            slideInVertically(
                animationSpec = tween(350, easing = FastOutSlowInEasing),
                initialOffsetY = { it }
            ) + fadeIn(animationSpec = tween(250))
        }
    } else {
        fadeIn(animationSpec = tween(200))
    }
}

/** Disparition directionnelle quand on ouvre. */
private fun exitTransitionFor(
    index: Int,
    expandedIndex: Int
): ExitTransition {
    if (expandedIndex == -1) return fadeOut(animationSpec = tween(150))
    val delay = 120
    return if (index < expandedIndex) {
        slideOutVertically(
            animationSpec = tween(durationMillis = 380, delayMillis = delay, easing = FastOutSlowInEasing),
            targetOffsetY = { -it }
        ) + fadeOut(animationSpec = tween(300, delayMillis = delay))
    } else if (index > expandedIndex) {
        slideOutVertically(
            animationSpec = tween(durationMillis = 380, delayMillis = delay, easing = FastOutSlowInEasing),
            targetOffsetY = { it }
        ) + fadeOut(animationSpec = tween(300, delayMillis = delay))
    } else {
        // l’item sélectionné ne sort pas
        fadeOut(animationSpec = tween(1))
    }
}

private fun colorForIndex(idx: Int): Color = when (idx % 4) {
    0 -> Color(0xFF6200EE)
    1 -> Color(0xFF03DAC6)
    2 -> Color(0xFFFF5722)
    else -> Color(0xFFFFBF8F)
}
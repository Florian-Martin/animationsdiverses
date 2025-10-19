@file:OptIn(ExperimentalAnimationApi::class)

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch

@Composable
fun DirectionalExpandableListCentered2(
    items: List<String> = List(8) { "Item ${it + 1}" },
    collapsedHeight: Int = 150,
    expandedHeight: Int = 400,
    spacingDp: Int = 16,
    selectedWidthFraction: Float = 0.92f,
) {
    val density = LocalDensity.current
    val collapsedH = collapsedHeight.dp
    val expandedH = expandedHeight.dp
    val sidePad = spacingDp.dp

    // --- État liste + sélection
    val listState = rememberLazyListState()
    var expandedIndex by remember { mutableIntStateOf(-1) }             // -1 = aucun
    var lastExpandedIndex by rememberSaveable { mutableIntStateOf(-1) } // pour la direction des slides
    LaunchedEffect(expandedIndex) { if (expandedIndex != -1) lastExpandedIndex = expandedIndex }

    // --- Positions Y (en root) des items visibles pour “décoller” en overlay
    val itemTopInRoot = remember { mutableStateMapOf<Int, Float>() }

    // --- Overlay animé : progress 0→1 (open) / 1→0 (close)
    var overlayActive by remember { mutableStateOf(false) }
    val progress = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    // --- Vue racine pour connaître la hauteur du viewport (centre cible)
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val rootHeight = this.maxHeight
        val rootHeightPx = with(density) { rootHeight.toPx() }
        val collapsedHPx = with(density) { collapsedH.toPx() }
        val expandedHPx = with(density) { expandedH.toPx() }

        // Cible top (aligner l’overlay au centre de l'écran)
        val targetTopPx = ((rootHeightPx - expandedHPx) / 2f).coerceAtLeast(0f)

        // --- COLONNE + OVERLAY superposés
        Box(Modifier.fillMaxSize()) {

            // ===== LISTE =====
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(
                    start = sidePad, end = sidePad,
                    top = sidePad, bottom = sidePad
                ),
                verticalArrangement = Arrangement.spacedBy(spacingDp.dp)
            ) {
                itemsIndexed(items, key = { i, _ -> i }) { index, label ->
                    val isSelected = expandedIndex == index
                    val isAnyExpanded = expandedIndex != -1

                    // Pendant l’animation overlay, on garde un "placeholder" à la place de l’item sélectionné
                    // pour éviter un reflow brutal (hauteur fixée à collapsed).
                    val showPlaceholder = overlayActive && isSelected

                    val enter = enterTransitionFor(index, isAnyExpanded, lastExpandedIndex)
                    val exit = exitTransitionFor(index, expandedIndex)

                    AnimatedVisibility(
                        visible = !isAnyExpanded || isSelected, // autres disparaissent (slide/fade)
                        enter = enter,
                        exit = exit
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(
                                    if (showPlaceholder) Modifier.height(collapsedH)
                                    else Modifier
                                )
                                .onGloballyPositioned { coords ->
                                    // Mémorise la position de l’item dans le root
                                    val pos: Offset = coords.positionInRoot()
                                    itemTopInRoot[index] = pos.y
                                }
                                .let {
                                    if (!showPlaceholder) {
                                        it
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(colorForIndex(index))
                                            .clickable {
                                                // Ouvre : armement overlay
                                                expandedIndex = if (isSelected) -1 else index
                                                if (!isSelected) {
                                                    // lance overlay open
                                                    val startTop = itemTopInRoot[index] ?: 0f
                                                    overlayActive = true
                                                    scope.launch {
                                                        progress.stop()
                                                        progress.snapTo(0f)
                                                        progress.animateTo(
                                                            1f,
                                                            tween(450, easing = FastOutSlowInEasing)
                                                        )
                                                    }
                                                }
                                            }
                                    } else it
                                }
                                .then(
                                    if (!showPlaceholder) Modifier
                                        .height(collapsedH) // dans la liste, on garde les items en hauteur "fermée"
                                    else Modifier
                                )
                                .zIndex(if (isSelected) 1f else 0f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!showPlaceholder) {
                                Text(
                                    text = if (isSelected && overlayActive) "$label (ouverture…)" else label,
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

            // ===== OVERLAY (seulement quand actif) =====
            if (overlayActive && expandedIndex >= 0) {
                val startTopPx = itemTopInRoot[expandedIndex] ?: 0f

                // Courbes : Y, hauteur, largeur liées à la même progression
                val topPx = lerpPx(startTopPx, targetTopPx, progress.value)
                val heightDp: Dp = lerp(collapsedH, expandedH, progress.value)
                val widthFraction = androidx.compose.ui.util.lerp(1f, selectedWidthFraction, progress.value)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(10f), // au-dessus de la liste
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = with(density) { topPx.toDp() })
                            .fillMaxWidth(widthFraction)
                            .height(heightDp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colorForIndex(expandedIndex))
                            .clickable {
                                // Fermeture : on ramène l’overlay à sa position d’origine, puis on coupe
                                scope.launch {
                                    progress.stop()
                                    progress.animateTo(
                                        0f,
                                        tween(380, easing = FastOutSlowInEasing)
                                    )
                                    overlayActive = false
                                    expandedIndex = -1
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${items[expandedIndex]} (ouvert)",
                            color = if (expandedIndex % 2 == 0) Color.White else Color.Black,
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

/* ---------- Utils ---------- */

private fun lerpPx(start: Float, end: Float, t: Float): Float =
    start + (end - start) * t

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
        // l’item sélectionné ne sort pas (on affiche l’overlay à la place)
        fadeOut(animationSpec = tween(1))
    }
}

private fun colorForIndex(idx: Int): Color = when (idx % 4) {
    0 -> Color(0xFF6200EE)
    1 -> Color(0xFF03DAC6)
    2 -> Color(0xFFFF5722)
    else -> Color(0xFFFFBF8F)
}


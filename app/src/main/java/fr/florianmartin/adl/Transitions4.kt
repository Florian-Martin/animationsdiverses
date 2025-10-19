@file:OptIn(ExperimentalAnimationApi::class, ExperimentalSharedTransitionApi::class)

package com.example.directionalexpand // <-- adapte si besoin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.rememberSharedContentState
import androidx.compose.animation.sharedElement
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

private const val HEIGHT_ANIM_MS = 450
private const val EXIT_MS = 380
private const val EXIT_DELAY_MS = 120
private const val FADE_MS = 300

@Composable
fun DirectionalListWithSharedCenter(
    items: List<String> = List(8) { "Item ${it + 1}" },
    collapsedHeight: Int = 150,
    expandedHeight: Int = 400,
    spacingDp: Int = 16,
    selectedWidthFraction: Float = 0.92f,
    maxSelectedWidthDp: Int = 720,
) {
    val listState = rememberLazyListState()
    val spacing = spacingDp.dp

    var expandedIndex by remember { mutableIntStateOf(-1) }             // -1 = fermé
    var lastExpandedIndex by rememberSaveable { mutableIntStateOf(-1) } // pour les enter directionnels

    SharedTransitionLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        // --- Liste (flux normal) ---
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(all = spacing),
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            itemsIndexed(items, key = { idx, _ -> idx }) { index, label ->
                val isSelected = expandedIndex == index
                if (expandedIndex != -1) lastExpandedIndex = expandedIndex

                // État partagé par index (même clé source/destination)
                val shared = rememberSharedContentState(key = "card-$index")

                // 1) Source dans la liste (visible sauf quand sélectionné, pour laisser la place à la destination)
                AnimatedVisibility(
                    visible = expandedIndex == -1 || !isSelected,
                    enter = enterTransitionFor(index, isAnyCurrentlyExpanded = expandedIndex != -1, lastExpandedIndex),
                    exit = if (isSelected) {
                        // On évite de forcer un slide ici, la sharedElement gère la “sortie visuelle”
                        fadeOut(tween(120))
                    } else {
                        exitTransitionFor(index, expandedIndex)
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(collapsedHeight.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colorForIndex(index))
                            // Le contenu source est marké comme sharedElement
                            .sharedElement(
                                state = shared,
                                boundsTransform = { _, _ ->
                                    // Animation de position/tailles fluide
                                    spring(stiffness = 600f, dampingRatio = 0.9f)
                                }
                            )
                            .clickable {
                                // Ouvrir l’item -> création de la destination centrée
                                if (expandedIndex == -1) expandedIndex = index
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (index % 2 == 0) Color.White else Color.Black,
                            fontSize = 18.sp
                        )
                    }
                }

                // 2) Destination centrée pour l'item sélectionné
                if (isSelected) {
                    val sharedDest = rememberSharedContentState(key = "card-$index")
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(2f), // au-dessus, mais tjs dans le même écran (pas d'overlay séparé)
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(selectedWidthFraction)
                                .widthIn(max = maxSelectedWidthDp.dp)
                                .height(expandedHeight.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(colorForIndex(index))
                                // Le contenu destination est marké avec la même clé
                                .sharedElement(
                                    state = sharedDest,
                                    boundsTransform = { _, _ ->
                                        spring(stiffness = 600f, dampingRatio = 0.9f)
                                    }
                                )
                                .clickable {
                                    // Fermer -> retour vers la source dans la liste
                                    expandedIndex = -1
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${items[index]} (ouvert)",
                                color = if (index % 2 == 0) Color.White else Color.Black,
                                fontSize = 20.sp
                            )
                        }
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
        // Pendant qu’un item est ouvert, l’enter ne joue qu’au retour.
        return fadeIn(animationSpec = tween(150, easing = LinearEasing))
    }
    return if (lastExpandedIndex != -1) {
        if (index < lastExpandedIndex) {
            expandIn(
                animationSpec = tween(300, easing = FastOutSlowInEasing),
                expandFrom = Alignment.TopCenter
            ) + slideInVertically(
                animationSpec = tween(350, easing = FastOutSlowInEasing),
                initialOffsetY = { -it }
            ) + fadeIn(animationSpec = tween(250))
        } else {
            expandIn(
                animationSpec = tween(300, easing = FastOutSlowInEasing),
                expandFrom = Alignment.BottomCenter
            ) + slideInVertically(
                animationSpec = tween(350, easing = FastOutSlowInEasing),
                initialOffsetY = { it }
            ) + fadeIn(animationSpec = tween(250))
        }
    } else {
        fadeIn(animationSpec = tween(200))
    }
}

/** Disparition directionnelle pour les items ≠ sélectionné. */
private fun exitTransitionFor(
    index: Int,
    expandedIndex: Int
): ExitTransition {
    if (expandedIndex == -1) return fadeOut(animationSpec = tween(150))
    return if (index < expandedIndex) {
        slideOutVertically(
            animationSpec = tween(durationMillis = EXIT_MS, delayMillis = EXIT_DELAY_MS, easing = FastOutSlowInEasing),
            targetOffsetY = { -it }
        ) + shrinkOut(
            animationSpec = tween(durationMillis = EXIT_MS, delayMillis = EXIT_DELAY_MS, easing = FastOutSlowInEasing),
            shrinkTowards = Alignment.TopCenter
        ) + fadeOut(animationSpec = tween(FADE_MS, delayMillis = EXIT_DELAY_MS))
    } else if (index > expandedIndex) {
        slideOutVertically(
            animationSpec = tween(durationMillis = EXIT_MS, delayMillis = EXIT_DELAY_MS, easing = FastOutSlowInEasing),
            targetOffsetY = { it }
        ) + shrinkOut(
            animationSpec = tween(durationMillis = EXIT_MS, delayMillis = EXIT_DELAY_MS, easing = FastOutSlowInEasing),
            shrinkTowards = Alignment.BottomCenter
        ) + fadeOut(animationSpec = tween(FADE_MS, delayMillis = EXIT_DELAY_MS))
    } else {
        // l’item sélectionné n’utilise pas cet exit (sa sortie est gérée par sharedElement)
        fadeOut(animationSpec = tween(1))
    }
}

private fun colorForIndex(idx: Int): Color = when (idx % 4) {
    0 -> Color(0xFF6200EE)
    1 -> Color(0xFF03DAC6)
    2 -> Color(0xFFFF5722)
    else -> Color(0xFFFFBF8F)
}

@Preview(showBackground = true)
@Composable
fun DirectionalListWithSharedCenterPreview() {
    DirectionalListWithSharedCenter()
}

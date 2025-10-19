package fr.florianmartin.adl

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlin.math.pow

// Custom easing functions
fun easeOutQuart(x: Float): Float = 1f - (1f - x).pow(4f)
fun easeInOutCubic(x: Float): Float =
    if (x < 0.5f) 4f * x * x * x else 1f - (-2f * x + 2f).pow(3f) / 2f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingListScreen(
    title: String = "Parkings Disponibles",
    parkingList: List<ParkingData> = sampleParkingData,
    onKnowMoreClicked: (ParkingData) -> Unit = {},
    onReserveClicked: (ParkingData) -> Unit = {}
) {
    val listState = rememberLazyListState()
    val density = LocalDensity.current

    val firstVisibleItemIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex }
    }
    val firstVisibleItemScrollOffset by remember {
        derivedStateOf { listState.firstVisibleItemScrollOffset }
    }

    // Custom smooth collapse progress with better curve
    val rawCollapseProgress by remember {
        derivedStateOf {
            when {
                firstVisibleItemIndex == 0 -> {
                    (firstVisibleItemScrollOffset / 180f).coerceIn(0f, 1f)
                }
                firstVisibleItemIndex > 0 -> 1f
                else -> 0f
            }
        }
    }

    // Smooth animated collapse progress with custom spring
    val collapseProgress by animateFloatAsState(
        targetValue = rawCollapseProgress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = 0.001f
        ),
        label = "collapseProgress"
    )

    // Custom eased values for different properties
    val titleTransitionProgress = easeInOutCubic(collapseProgress)
    val scaleTransitionProgress = easeOutQuart(collapseProgress)
    val alphaTransitionProgress = easeInOutCubic(collapseProgress)

    // Animated properties with custom curves
    val expandedTitleAlpha by animateFloatAsState(
        targetValue = (1f - alphaTransitionProgress).coerceIn(0f, 1f),
        animationSpec = tween(
            durationMillis = 400,
            easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)
        ),
        label = "expandedTitleAlpha"
    )

    val collapsedTitleAlpha by animateFloatAsState(
        targetValue = alphaTransitionProgress.coerceIn(0f, 1f),
        animationSpec = tween(
            durationMillis = 400,
            easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)
        ),
        label = "collapsedTitleAlpha"
    )

    val titleScale by animateFloatAsState(
        targetValue = 1f - (scaleTransitionProgress * 0.15f), // Subtle scale animation
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "titleScale"
    )

    val titleFontSize by animateFloatAsState(
        targetValue = 32f - (titleTransitionProgress * 12f), // 32sp -> 20sp
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "titleFontSize"
    )

    val cardCornerRadius by animateFloatAsState(
        targetValue = 16f - (titleTransitionProgress * 4f), // 16dp -> 12dp
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cardCornerRadius"
    )

    val cardElevation by animateFloatAsState(
        targetValue = 8f + (titleTransitionProgress * 8f), // 8dp -> 16dp
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cardElevation"
    )

    // Blur effect with custom timing
    val blurRadius by animateFloatAsState(
        targetValue = if (collapseProgress > 0.05f) 12f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
        ),
        label = "blurRadius"
    )

    // Background opacity with delayed start
    val backgroundOpacity by animateFloatAsState(
        targetValue = if (collapseProgress > 0.2f) 0.95f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearEasing
        ),
        label = "backgroundOpacity"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Main scrollable content - edge to edge
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 120.dp,
                bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 16.dp,
                start = 0.dp,
                end = 0.dp
            ),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            items(parkingList) { parking ->
                ParkingInfoCard(
                    parkingName = parking.name,
                    originalPrice = parking.originalPrice,
                    discountedPrice = parking.discountedPrice,
                    discountPercentage = parking.discountPercentage,
                    description = parking.description,
                    rating = parking.rating,
                    isOnlineExclusive = parking.isOnlineExclusive,
                    onKnowMoreClicked = { onKnowMoreClicked(parking) },
                    onReserveClicked = { onReserveClicked(parking) }
                )
            }
        }

        // Enhanced blur background - starts from top edge
        if (blurRadius > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 140.dp
                    )
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.92f),
                                Color.White.copy(alpha = 0.85f),
                                Color.White.copy(alpha = 0.4f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = 400f
                        )
                    )
                    .blur(radius = with(density) { blurRadius.dp })
                    .zIndex(0.5f)
            )
        }

        // System bar gradient background - edge to edge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 60.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = backgroundOpacity),
                            Color.White.copy(alpha = backgroundOpacity * 0.8f),
                            Color.White.copy(alpha = if (collapseProgress > 0.1f) backgroundOpacity * 0.3f else 0f),
                            Color.Transparent
                        )
                    )
                )
                .zIndex(1f)
        )

        // Single unified title with morphing animation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    if (collapseProgress < 0.5f) 120.dp + WindowInsets.systemBars.asPaddingValues()
                        .calculateTopPadding()
                    else 64.dp + WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
                )
                .zIndex(2f),
            contentAlignment = if (collapseProgress < 0.5f) Alignment.Center else Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(
                        horizontal = (16 + titleTransitionProgress * 0).dp,
                        vertical = (16 - titleTransitionProgress * 8).dp
                    )
                    .scale(titleScale)
                    .graphicsLayer {
                        // Smooth position interpolation
                        translationY = -titleTransitionProgress * 20f
                    },
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(
                        alpha = 0.95f + (titleTransitionProgress * 0.05f)
                    )
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = cardElevation.dp
                ),
                shape = RoundedCornerShape(cardCornerRadius.dp)
            ) {
                Text(
                    text = title,
                    fontSize = titleFontSize.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(
                            horizontal = (24 - titleTransitionProgress * 4).dp,
                            vertical = (16 - titleTransitionProgress * 4).dp
                        )
                        .graphicsLayer {
                            // Subtle text scaling for smooth readability transition
                            scaleX = 1f - (titleTransitionProgress * 0.05f)
                            scaleY = 1f - (titleTransitionProgress * 0.05f)
                        }
                )
            }
        }
    }
}

// Data class for parking information
data class ParkingData(
    val id: String,
    val name: String,
    val originalPrice: String,
    val discountedPrice: String,
    val discountPercentage: String,
    val description: String,
    val rating: Float,
    val isOnlineExclusive: Boolean
)

// Sample data
val sampleParkingData = listOf(
    ParkingData(
        id = "p2",
        name = "Parking P2",
        originalPrice = "8,00 €",
        discountedPrice = "7,60 €",
        discountPercentage = "-6%",
        description = "Parking extérieur, situé en face du terminal 2",
        rating = 4.5f,
        isOnlineExclusive = true
    ),
    ParkingData(
        id = "p5",
        name = "Parking P5",
        originalPrice = "10,00 €",
        discountedPrice = "9,50 €",
        discountPercentage = "-5%",
        description = "Parking couvert, navette gratuite toutes les 15 minutes",
        rating = 4.2f,
        isOnlineExclusive = false
    ),
    ParkingData(
        id = "eco",
        name = "Parking Eco",
        originalPrice = "6,00 €",
        discountedPrice = "5,00 €",
        discountPercentage = "-16%",
        description = "Parking économique, à 10 minutes à pied du terminal 1",
        rating = 3.8f,
        isOnlineExclusive = true
    ),
    ParkingData(
        id = "premium",
        name = "Parking Premium Alpha",
        originalPrice = "15,00 €",
        discountedPrice = "14,00 €",
        discountPercentage = "-7%",
        description = "Parking premium avec services additionnels tels que lavage de voiture, voiturier et accès direct au hall des départs.",
        rating = 4.9f,
        isOnlineExclusive = true
    ),
    ParkingData(
        id = "express",
        name = "Parking Express",
        originalPrice = "12,00 €",
        discountedPrice = "10,80 €",
        discountPercentage = "-10%",
        description = "Parking rapide avec accès direct aux terminaux",
        rating = 4.3f,
        isOnlineExclusive = false
    ),
    ParkingData(
        id = "longterm",
        name = "Parking Longue Durée",
        originalPrice = "5,50 €",
        discountedPrice = "4,95 €",
        discountPercentage = "-10%",
        description = "Idéal pour les séjours prolongés, navette 24h/24",
        rating = 4.1f,
        isOnlineExclusive = true
    ),
    ParkingData(
        id = "vip",
        name = "Parking VIP",
        originalPrice = "20,00 €",
        discountedPrice = "18,00 €",
        discountPercentage = "-10%",
        description = "Service premium avec voiturier et nettoyage de véhicule",
        rating = 4.8f,
        isOnlineExclusive = true
    ),
    ParkingData(
        id = "shuttle",
        name = "Parking Navette",
        originalPrice = "7,50 €",
        discountedPrice = "6,75 €",
        discountPercentage = "-10%",
        description = "Parking avec navette gratuite toutes les 10 minutes",
        rating = 4.0f,
        isOnlineExclusive = false
    )
)

@Preview(showBackground = true, name = "Parking List Screen")
@Composable
fun ParkingListScreenPreview() {
    ParkingListScreen(
        title = "Parkings Aéroport CDG",
        parkingList = sampleParkingData.take(4)
    )
}
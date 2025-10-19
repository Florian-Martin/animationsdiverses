package fr.florianmartin.adl

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ParkingInfoCard(
    parkingName: String,
    originalPrice: String,
    discountedPrice: String,
    discountPercentage: String,
    description: String,
    rating: Float,
    isOnlineExclusive: Boolean,
    onKnowMoreClicked: () -> Unit,
    onReserveClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(Color(0xFFE3F2FD)),
            width = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
        ) {
            // Header section with icon and badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Modern Parking Icon (Replaced ImageVector Icon with 'P' Text)
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF1565C0),
                                    Color(0xFF0D47A1)
                                )
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "P",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                // Badges section
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isOnlineExclusive) {
                        OnlineExclusiveChip()
                    }
                    RatingChip(rating = rating)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Title
            Text(
                text = parkingName,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Price section
            PriceText(originalPrice, discountedPrice, discountPercentage)

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            Text(
                text = description,
                fontSize = 15.sp,
                color = Color(0xFF6B7280),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Know more button
                OutlinedButton(
                    onClick = onKnowMoreClicked,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, Color(0xFF1565C0)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF1565C0)
                    )
                ) {
                    Text(
                        "En savoir plus",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Reserve button
                Button(
                    onClick = onReserveClicked,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1565C0)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Text(
                        "Réserver",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun OnlineExclusiveChip() {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFE8F5E8),
        border = BorderStroke(1.dp, Color(0xFF4CAF50))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(
                        Color(0xFF4CAF50),
                        shape = RoundedCornerShape(3.dp)
                    )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Exclusivité web",
                color = Color(0xFF2E7D32),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun RatingChip(rating: Float) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFFFF8E1),
        border = BorderStroke(1.dp, Color(0xFFFFB300))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Rating Star",
                tint = Color(0xFFFF8F00),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = String.format("%.1f", rating),
                color = Color(0xFFE65100),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun PriceText(originalPrice: String, discountedPrice: String, discountPercentage: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = originalPrice,
                color = Color(0xFF9CA3AF),
                textDecoration = TextDecoration.LineThrough,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = discountedPrice,
                color = Color(0xFF1A1A1A),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                letterSpacing = (-0.5).sp
            )
        }

        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFFE8F5E8)
        ) {
            Text(
                text = "Offre web $discountPercentage",
                color = Color(0xFF2E7D32),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "Default Parking Card")
@Composable
fun DefaultParkingInfoCardPreview() {
    ParkingInfoCard(
        parkingName = "Parking P2",
        originalPrice = "8,00 €",
        discountedPrice = "7,60 €",
        discountPercentage = "-6%",
        description = "Parking extérieur, situé en face du terminal 2",
        rating = 4.5f,
        isOnlineExclusive = true,
        onKnowMoreClicked = {},
        onReserveClicked = {}
    )
}

@Preview(showBackground = true, name = "Parking Card - No Exclusive")
@Composable
fun NoExclusiveParkingInfoCardPreview() {
    ParkingInfoCard(
        parkingName = "Parking P5",
        originalPrice = "10,00 €",
        discountedPrice = "9,50 €",
        discountPercentage = "-5%",
        description = "Parking couvert, navette gratuite toutes les 15 minutes",
        rating = 4.2f,
        isOnlineExclusive = false,
        onKnowMoreClicked = {},
        onReserveClicked = {}
    )
}

@Preview(showBackground = true, name = "Parking Card - Different Rating")
@Composable
fun DifferentRatingParkingInfoCardPreview() {
    ParkingInfoCard(
        parkingName = "Parking Eco",
        originalPrice = "6,00 €",
        discountedPrice = "5,00 €",
        discountPercentage = "-16%",
        description = "Parking économique, à 10 minutes à pied du terminal 1",
        rating = 3.8f,
        isOnlineExclusive = true,
        onKnowMoreClicked = {},
        onReserveClicked = {}
    )
}

@Preview(showBackground = true, name = "Parking Card - Long Description")
@Composable
fun LongDescriptionParkingInfoCardPreview() {
    ParkingInfoCard(
        parkingName = "Parking Premium Alpha",
        originalPrice = "15,00 €",
        discountedPrice = "14,00 €",
        discountPercentage = "-7%",
        description = "Parking premium avec services additionnels tels que lavage de voiture, voiturier et accès direct au hall des départs.",
        rating = 4.9f,
        isOnlineExclusive = true,
        onKnowMoreClicked = {},
        onReserveClicked = {}
    )
}
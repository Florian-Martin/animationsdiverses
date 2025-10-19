package fr.florianmartin.adl

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex // Important for shadow rendering

// Data class to represent the info banner properties
data class InfoBannerData(
    val text: String,
    val backgroundColor: Color,
    val textColor: Color
)

@Composable
fun ParkingInfoCard(
    parkingName: String,
    originalPrice: String,
    discountedPrice: String,
    discountPercentage: String,
    description: String,
    rating: Float,
    isOnlineExclusive: Boolean, // Changed to reflect "Nouveau" or similar
    chipText: String? = null, // Text for the top-right chip (e.g., "Nouveau")
    onKnowMoreClicked: () -> Unit,
    onReserveClicked: () -> Unit,
    infoBannerData: InfoBannerData? = null // Optional banner data
) {
    Column( // Use Column to stack Card and Banner
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f), // Ensure Card is drawn above the banner for shadow effect
            shape = if (infoBannerData != null) {
                // Apply rounded corners only to top if banner is present
                RoundedCornerShape(
                    topStart = 12.dp,
                    topEnd = 12.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                )
            } else {
                RoundedCornerShape(12.dp) // Original rounded corners if no banner
            },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Shadow comes from here
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // Parking Icon (P7)
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0xFF1D263D), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "P7", // Updated to P7 as per new screenshot
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Top Right Chips
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (chipText != null) {
                                // Generalized chip for "Nouveau" or "Exclusivité internet"
                                CustomInfoChip(
                                    text = chipText,
                                    borderColor = if (chipText == "Nouveau") Color.Gray else Color(
                                        0xFF4CAF50
                                    ),
                                    textColor = if (chipText == "Nouveau") Color.DarkGray else Color(
                                        0xFF4CAF50
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            RatingChip2(rating = rating)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = parkingName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                PriceText2(originalPrice, discountedPrice, discountPercentage)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = description,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    lineHeight = 22.sp // Added for better readability of multi-line description
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onKnowMoreClicked,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE8EAF6), // Light gray
                            contentColor = Color(0xFF3F51B5) // Indigo
                        )
                    ) {
                        Text("En savoir plus", fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = onReserveClicked,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3F51B5), // Indigo
                            contentColor = Color.White
                        )
                    ) {
                        Text("Réserver", fontSize = 16.sp)
                    }
                }
            }
        }

        // Info Banner below the card
        infoBannerData?.let { banner ->
            InfoBanner(
                text = banner.text,
                backgroundColor = banner.backgroundColor,
                textColor = banner.textColor
            )
        }
    }
}

@Composable
fun CustomInfoChip(text: String, borderColor: Color, textColor: Color) {
    Surface(
        modifier = Modifier.clip(RoundedCornerShape(50)),
        shape = RoundedCornerShape(50),
        color = Color.Transparent,
        border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(borderColor))
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}


@Composable
fun InfoBanner(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomStart = 12.dp,
                    bottomEnd = 12.dp
                )
            ), // Round bottom corners
        color = backgroundColor,
        // No elevation here, the shadow comes from the Card above
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}


// --- Previews ---

@Preview(showBackground = true, name = "Parking Card with Info Banner")
@Composable
fun ParkingCardWithBannerPreview() {
    ParkingInfoCard(
        parkingName = "Parking P7",
        originalPrice = "8,00 €",
        discountedPrice = "7,60 €",
        discountPercentage = "-6%",
        description = "Parking goudronné situé près de la tour de contrôle. L'accès aux terminaux se fait par navette gratuite.",
        rating = 4.5f,
        isOnlineExclusive = false, // Not used directly, chipText handles this
        chipText = "Nouveau",
        onKnowMoreClicked = {},
        onReserveClicked = {},
        infoBannerData = InfoBannerData(
            text = "En travaux",
            backgroundColor = Color(0xFFFFF3E0), // Light Orange
            textColor = Color(0xFFE65100)      // Dark Orange
        )
    )
}

@Preview(showBackground = true, name = "Parking Card - No Banner")
@Composable
fun NoBannerParkingInfoCardPreview() {
    ParkingInfoCard(
        parkingName = "Parking P2",
        originalPrice = "8,00 €",
        discountedPrice = "7,60 €",
        discountPercentage = "-6%",
        description = "Parking extérieur, situé en face du terminal 2",
        rating = 4.5f,
        isOnlineExclusive = true,
        chipText = "Exclusivité internet",
        onKnowMoreClicked = {},
        onReserveClicked = {}
        // No infoBannerData provided
    )
}

@Preview(showBackground = true, name = "Parking Card with Different Banner")
@Composable
fun ParkingCardWithDifferentBannerPreview() {
    ParkingInfoCard(
        parkingName = "Parking P5",
        originalPrice = "10,00 €",
        discountedPrice = "9,50 €",
        discountPercentage = "-5%",
        description = "Parking couvert, navette gratuite toutes les 15 minutes",
        rating = 4.2f,
        isOnlineExclusive = false,
        chipText = null, // No top chip
        onKnowMoreClicked = {},
        onReserveClicked = {},
        infoBannerData = InfoBannerData(
            text = "Promotion spéciale !",
            backgroundColor = Color(0xFFE8F5E9), // Light Green
            textColor = Color(0xFF1B5E20)      // Dark Green
        )
    )
}

// Re-adding other composables for completeness if they were removed or modified
@Composable
fun RatingChip2(rating: Float) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(50)), // For a pill shape
        shape = RoundedCornerShape(50),
        color = Color.Transparent,
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = SolidColor(Color(0xFFFFC107)) // Amber border
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Rating Star",
                tint = Color(0xFFFFC107), // Amber star
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = String.format("%.1f", rating),
                color = Color.Black, // Text color for rating could be DarkGray or Black
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun PriceText2(originalPrice: String, discountedPrice: String, discountPercentage: String) {
    Text(
        buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = Color.Gray,
                    textDecoration = TextDecoration.LineThrough,
                    fontSize = 16.sp
                )
            ) {
                append(originalPrice)
            }
            append("  ") // Space between original and discounted price
            withStyle(
                style = SpanStyle(
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            ) {
                append(discountedPrice)
            }
            append("  ") // Space between discounted price and discount percentage
            withStyle(
                style = SpanStyle(
                    color = Color(0xFF4CAF50), // Green color for discount
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            ) {
                append("Offre web $discountPercentage")
            }
        }
    )
}
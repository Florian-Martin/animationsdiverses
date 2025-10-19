package fr.florianmartin.adl

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ThreeColumnsWithTransition() {
    var isVisible by remember { mutableStateOf(true) }
    val animatedHeight by animateDpAsState(
        targetValue = if (isVisible) 150.dp else 400.dp,
        animationSpec = tween(durationMillis = 500,
            easing = FastOutSlowInEasing)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Première colonne (celle du haut) - cliquable
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedHeight)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    color = Color(0xFF6200EE) // Violet/Bleu
                )
                .clickable {
                    isVisible = !isVisible
                },
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

        // Deuxième colonne avec animation (maintenant en position 3 pour glisser sous la colonne 3)
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(
                animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
            ) + slideInVertically(
                animationSpec = tween(durationMillis =  1000),
                initialOffsetY = { fullHeight -> fullHeight * 3 }
            ),
            exit = slideOutVertically(
                animationSpec = tween(durationMillis = 800), // Plus rapide pour glisser sous la 3ème
                targetOffsetY = { fullHeight -> fullHeight * 3}
            ) + fadeOut(
                animationSpec = tween(durationMillis = 800)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        color = Color(0xFF03DAC6) // Vert/Turquoise
                    ),
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

        // Troisième colonne avec animation (maintenant en position 2 pour passer par-dessus)
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(
                animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
            ) + slideInVertically(
                animationSpec = tween(durationMillis =1500),
                initialOffsetY = { fullHeight -> fullHeight * 3 }
            ),
            exit = slideOutVertically(
                animationSpec = tween(durationMillis = 400),
                targetOffsetY = { fullHeight -> fullHeight* 3 }
            ) + fadeOut(
                animationSpec = tween(durationMillis = 800)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        color = Color(0xFFFF5722) // Orange/Rouge
                    ),
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
fun ThreeColumnsWithTransitionPreview2() {
    ThreeColumnsWithTransition()
}

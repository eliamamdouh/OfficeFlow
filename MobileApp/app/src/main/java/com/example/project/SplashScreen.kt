package com.example.project

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.request.ImageRequest
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    var startAnimation by remember { mutableStateOf(false) }
    val gifOpacity by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 1f,
        animationSpec = tween(durationMillis = 1000), label = ""
    )

    LaunchedEffect(Unit) {
        delay(1000) // Short delay before starting the animation
        startAnimation = true
        delay(1000) // Delay before navigating to login screen
        navController.navigate("page0")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GifImage(drawableResId = R.drawable.loading, opacity = gifOpacity)

            Spacer(modifier = Modifier.height(16.dp))
            AnimatedLogo(startAnimation)
        }
    }
}

@Composable
fun GifImage(drawableResId: Int, opacity: Float) {
    val context = LocalContext.current
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(drawableResId)
            .decoderFactory(GifDecoder.Factory())
            .build()
    )

    Image(
        painter = painter,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(200.dp)
            .border(4.dp, Color.Black)
            .graphicsLayer(alpha = opacity) // Apply opacity to the GIF
    )
}

@Composable
fun AnimatedLogo(startAnimation: Boolean) {
    val offsetY by animateDpAsState(
        targetValue = if (startAnimation) (-307).dp else -95.dp,
        animationSpec = tween(durationMillis = 1000),
        label = "LogoAnimation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.offset(y = offsetY)
    ) {
        Logo(modifier = Modifier.size(250.dp))
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun Logo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.logo4),
        contentDescription = "",
        modifier = modifier
    )
}

@Composable
fun BackgroundImage() {
    Image(
        painter = painterResource(id = R.drawable.splash1),
        contentDescription = "",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}

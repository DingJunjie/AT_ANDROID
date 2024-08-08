package com.bitat.ui.component

import android.annotation.SuppressLint
import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun LottieBox(
    @RawRes lottieRes: Int,
    isRepeat: Boolean = false,
    isPlaying: Boolean = true,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(lottieRes))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = if (isRepeat) LottieConstants.IterateForever else 1,
        isPlaying = isPlaying
    )

    LottieAnimation(composition, progress = {
        progress
    }, modifier = modifier)
}
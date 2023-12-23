package com.shino72.shooting_ball

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.shino72.shooting_ball.ui.theme.ShootingballTheme
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShootingballTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Ground()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Ground(modifier: Modifier = Modifier){
    var componentSize by remember {
        mutableStateOf(IntSize.Zero)
    }
    val density = LocalDensity.current

    var state = remember {
        AnchoredDraggableState(
            initialValue = Position.Start,
            positionalThreshold = {totalDistance : Float -> totalDistance},
            velocityThreshold = {Float.MIN_VALUE},
            animationSpec = tween()

        )
    }


    LaunchedEffect(componentSize){
        if(componentSize.width > 0) {
            val endPosition = with(density){(componentSize.height - 40.dp.toPx()) - 60.dp.toPx()}
            state.updateAnchors(
                DraggableAnchors {
                    Position.Start at -0f
                    Position.End at -endPosition
                }
            )
            if(state.offset.isNaN()) {
            }
        }
    }

    LaunchedEffect(state.currentValue){
        if(state.currentValue == Position.End){
            delay(1000)
            state.snapTo(Position.Start)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.LightGray)
            .onGloballyPositioned {
                componentSize = it.size
            }
    ){
        Image(
            painter = painterResource(id = R.drawable.posu),
            contentDescription = "",
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.TopCenter)
        )

        Text(
            text = if(state.currentValue == Position.Start) "던지세요" else "스트라이크",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.Center)
        )

        val safeOffset = if(state.offset.isNaN()) 0f else state.offset
        Log.d("state : ", safeOffset.toString())
        Log.d("component height : ", componentSize.height.toString())
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset {
                    IntOffset(0, (safeOffset.roundToInt()))
                }
                .scale((componentSize.height - state.offset) / componentSize.height)
        ){
            Image(
                painter = painterResource(id = R.drawable.baseball),
                contentDescription = "",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .anchoredDraggable(
                        state = state,
                        orientation = Orientation.Vertical
                    )
                    .graphicsLayer {
                        rotationZ = state.offset % 360
                    }
            )
        }

    }
}

enum class Position{
    Start, End
}

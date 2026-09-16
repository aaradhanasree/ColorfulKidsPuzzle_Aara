package com.puzzle.kidsmatch

import android.media.MediaPlayer
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PuzzleApp() {
    val context = LocalContext.current
    var pieceCount by remember { mutableIntStateOf(6) } 
    val cols = 2
    val rows = if (pieceCount == 6) 3 else 4

    var tiles by remember { mutableStateOf<List<PuzzleTile>>(emptyList()) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var moves by remember { mutableIntStateOf(0) }
    var isSolved by remember { mutableStateOf(false) }

    // --- MUSIC STATE ---
    var isMusicEnabled by remember { mutableStateOf(false) } // Default to false until user clicks it or adds file

    val mediaPlayer = remember {
        try {
            MediaPlayer.create(context, R.raw.bg_music)?.apply {
                isLooping = true
            }
        } catch (e: Exception) {
            null // Fails gracefully if the user hasn't added a real mp3 yet
        }
    }

    DisposableEffect(isMusicEnabled) {
        if (isMusicEnabled) {
            mediaPlayer?.start()
        } else {
            mediaPlayer?.pause()
        }
        onDispose { }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }
    // -------------------

    fun loadAndShuffle() {
        val bitmap = PuzzleUtils.loadBitmap(context)
        val originalTiles = PuzzleUtils.sliceBitmap(bitmap, rows, cols)
        var shuffled = originalTiles.shuffled()
        while (shuffled.indices.all { shuffled[it].originalIndex == it } && shuffled.size > 1) {
            shuffled = originalTiles.shuffled()
        }
        tiles = shuffled
        selectedIndex = null
        moves = 0
        isSolved = false
    }

    LaunchedEffect(pieceCount) {
        loadAndShuffle()
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            PuzzleUtils.saveCustomImage(context, it)
            loadAndShuffle()
        }
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFF9C4), 
            Color(0xFFFFE0B2)  
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        // Floating Music Button at the top right
        Surface(
            shape = CircleShape,
            color = if (isMusicEnabled) MaterialTheme.colorScheme.primary else Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 16.dp)
                .size(56.dp)
                .clickable { isMusicEnabled = !isMusicEnabled }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = if (isMusicEnabled) "🔊" else "🔇",
                    fontSize = 24.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🧩 AradhanaShri's Puzzle ✨",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 12.dp).padding(end = 60.dp) // Leave room for music button
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PlayfulToggleCard(
                    text = "🧸 6 Pieces",
                    isSelected = pieceCount == 6,
                    selectedColor = MaterialTheme.colorScheme.tertiary,
                    onClick = { pieceCount = 6 }
                )
                PlayfulToggleCard(
                    text = "🦄 8 Pieces",
                    isSelected = pieceCount == 8,
                    selectedColor = MaterialTheme.colorScheme.secondary,
                    onClick = { pieceCount = 8 }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(24.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text("📸 Photo", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Button(
                    onClick = { loadAndShuffle() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    shape = RoundedCornerShape(24.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text("🔄 Shuffle", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = isSolved,
                enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(32.dp),
                    shadowElevation = 8.dp,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "🎉 YAY! You did it! 🥳",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.wrapContentSize(),
                    shadowElevation = 4.dp
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(cols),
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .aspectRatio(cols.toFloat() / rows.toFloat())
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        userScrollEnabled = false
                    ) {
                        itemsIndexed(tiles) { index, tile ->
                            val isSelected = selectedIndex == index
                            
                            val scale by animateFloatAsState(
                                targetValue = if (isSelected) 0.9f else 1f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                label = "scale"
                            )

                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .scale(scale)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        width = if (isSelected) 6.dp else 2.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .shadow(if (isSelected) 8.dp else 2.dp, RoundedCornerShape(16.dp))
                                    .clickable {
                                        if (!isSolved) {
                                            if (selectedIndex == null) {
                                                selectedIndex = index
                                            } else {
                                                val first = selectedIndex!!
                                                if (first != index) {
                                                    val updated = tiles.toMutableList()
                                                    val temp = updated[first]
                                                    updated[first] = updated[index]
                                                    updated[index] = temp
                                                    tiles = updated
                                                    moves++
                                                    isSolved = tiles.indices.all { tiles[it].originalIndex == it }
                                                }
                                                selectedIndex = null
                                            }
                                        }
                                    }
                            ) {
                                Image(
                                    bitmap = tile.image,
                                    contentDescription = "Piece ${tile.originalIndex}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
            
            Text(
                text = "Moves: $moves",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF757575),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun PlayfulToggleCard(
    text: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) selectedColor else Color.White
    val contentColor = if (isSelected) Color.White else Color.DarkGray

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        modifier = Modifier
            .clickable { onClick() }
            .shadow(if (isSelected) 8.dp else 2.dp, RoundedCornerShape(20.dp)),
        border = if (!isSelected) null else androidx.compose.foundation.BorderStroke(3.dp, Color.White)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )
    }
}

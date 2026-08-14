package com.example

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.ui.theme.*

@Composable
fun GlassBottomNavItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    val iconColor by animateColorAsState(if (selected) Color.White else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), tween(300), label = "iconColor")
    val textColor by animateColorAsState(if (selected) Color.White else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), tween(300), label = "textColor")
    
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.15f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Box(modifier = modifier
        .fillMaxHeight()
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        ), 
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
        ) {
            Icon(icon, contentDescription = label, tint = iconColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(label, color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: BichouViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    val petAvatarUri by viewModel.petAvatarUri.collectAsState()
    val healthCondition by viewModel.healthCondition.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    
    var showChatSheet by remember { mutableStateOf(false) }
    var chatInput by remember { mutableStateOf("") }
    
    val context = androidx.compose.ui.platform.LocalContext.current
    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val imagePickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        selectedImageUri = uri
    }
    
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .fillMaxWidth()
                    .height(72.dp)
            ) {
                // 1. Dark Contrast Shadow
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            shadowElevation = 20.dp.toPx()
                            shape = RoundedCornerShape(40.dp)
                            clip = false
                        }
                )

                // 2. Crystal Translucent Background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(40.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFFFFFFF).copy(alpha = 0.22f), 
                                    Color(0xFFE2E8F0).copy(alpha = 0.12f)
                                )
                            )
                        )
                )

                // 3. Liquid Gloss Border
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            1.8.dp, 
                            Color.White.copy(alpha = 0.75f), 
                            RoundedCornerShape(40.dp)
                        )
                )

                // Foreground Content
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val tabWidth = maxWidth / 4
                    val indicatorWidth = 72.dp

                    val transition = updateTransition(targetState = selectedTab, label = "tab")
                    val indicatorLeft by transition.animateDp(
                        transitionSpec = {
                            if (targetState > initialState) {
                                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = 50f)
                            } else {
                                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
                            }
                        },
                        label = "indicatorLeft"
                    ) { tab ->
                        (tabWidth * tab) + (tabWidth - indicatorWidth) / 2
                    }

                    val indicatorRight by transition.animateDp(
                        transitionSpec = {
                            if (targetState > initialState) {
                                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
                            } else {
                                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = 50f)
                            }
                        },
                        label = "indicatorRight"
                    ) { tab ->
                        (tabWidth * tab) + (tabWidth - indicatorWidth) / 2 + indicatorWidth
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(vertical = 12.dp)
                            .offset(x = indicatorLeft)
                            .width(indicatorRight - indicatorLeft)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(40.dp)
                            )
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlassBottomNavItem(selected = selectedTab == 0, onClick = { selectedTab = 0 }, icon = Icons.Default.Home, label = "Home", modifier = Modifier.weight(1f))
                        GlassBottomNavItem(selected = selectedTab == 1, onClick = { selectedTab = 1 }, icon = Icons.Default.LocalHospital, label = "Vet", modifier = Modifier.weight(1f))
                        GlassBottomNavItem(selected = selectedTab == 2, onClick = { selectedTab = 2 }, icon = Icons.Default.Restaurant, label = "Pet food", modifier = Modifier.weight(1f))
                        GlassBottomNavItem(selected = selectedTab == 3, onClick = { selectedTab = 3 }, icon = Icons.Default.Pets, label = "Your pet's", modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    ) { paddingVals ->
        AnimatedContent(
            targetState = selectedTab,
            label = "tab_transition",
            transitionSpec = {
                fadeIn(tween(300)) togetherWith fadeOut(tween(300))
            }
        ) { tab ->
            when (tab) {
                0 -> HomeContentView(paddingVals, navController, petAvatarUri, healthCondition, { showChatSheet = true })
                2 -> PetFoodView(paddingVals, viewModel)
                else -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Coming soon", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
    
    if (showChatSheet) {
        ModalBottomSheet(onDismissRequest = { showChatSheet = false }, containerColor = MaterialTheme.colorScheme.background, modifier = Modifier.imePadding()) {
            Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f).padding(16.dp)) {
                Text("Bichou AI Chat", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), reverseLayout = true) {
                    items(chatMessages.reversed()) { msg ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (msg.isUser) MaterialTheme.colorScheme.primary else Color.White,
                                modifier = Modifier.padding(vertical = 4.dp).widthIn(max = 300.dp)
                            ) {
                                Text(msg.text, color = if (msg.isUser) Color.White else MaterialTheme.colorScheme.primary, modifier = Modifier.padding(12.dp))
                            }
                        }
                    }
                }
                
                /*
                if (selectedImageUri != null) {
                    Box(modifier = Modifier.padding(bottom = 8.dp)) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Selected Image",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                        IconButton(
                            onClick = { selectedImageUri = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 8.dp, y = (-8).dp)
                                .size(24.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Remove Image", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                */
                
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = chatInput,
                        onValueChange = { chatInput = it },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        placeholder = { Text("Ask Bichou AI...") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = {
                        if (chatInput.isNotBlank()) {
                            viewModel.sendMessageToAI(chatInput, null)
                            chatInput = ""
                        }
                    }, modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape)) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun HomeContentView(
    paddingVals: PaddingValues, 
    navController: NavController,
    petAvatarUri: android.net.Uri?,
    healthCondition: String,
    onShowChatSheet: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(paddingVals).padding(horizontal = 20.dp, vertical = 16.dp), verticalArrangement = Arrangement.SpaceBetween) {
        // Top Header Area
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Hyper Pet", fontSize = 24.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                Text("Your smart pet assistant", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
            }
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White) }
            }
        }

        // Top Row Cards
        Row(modifier = Modifier.fillMaxWidth().weight(1.2f, fill = false), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Surface(
                shape = RoundedCornerShape(40.dp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f).aspectRatio(1f)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .border(4.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (petAvatarUri != null) {
                            AsyncImage(model = petAvatarUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        } else {
                            Icon(Icons.Default.Pets, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Bichou", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            
            Surface(
                shape = RoundedCornerShape(40.dp),
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.weight(1f).aspectRatio(1f)
            ) {
                Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                    Text("HEALTH MONITOR", color = MaterialTheme.colorScheme.primary, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        val conditionColor = when (healthCondition) {
                            "Excellent" -> GreenExcellent
                            "Very Critical" -> RedCritical
                            else -> OrangeOrdinary
                        }
                        Text(healthCondition, color = conditionColor, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(conditionColor.copy(alpha = 0.2f))) {
                            val progress = when (healthCondition) {
                                "Excellent" -> 0.92f
                                "Very Critical" -> 0.25f
                                else -> 0.5f
                            }
                            Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(progress).clip(RoundedCornerShape(3.dp)).background(conditionColor))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(if (healthCondition == "Excellent") "Score: 92/100" else "Score: --/100", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // AI Chat input Custom Card
        Surface(
            shape = RoundedCornerShape(40.dp),
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth().weight(1f, fill = false).clickable { onShowChatSheet() }
        ) {
            Column(modifier = Modifier.padding(20.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(12.dp), color = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(36.dp)) {
                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.ChatBubble, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp)) }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Ask Bichou AI", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                
                Surface(shape = RoundedCornerShape(25.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("How is my pet feeling today?", color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp)) {
                            Box(contentAlignment = Alignment.Center) { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(16.dp)) }
                        }
                    }
                }
                
                Text("Describe your pet's behavior or symptoms for a quick AI health analysis.", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, lineHeight = 16.sp, modifier = Modifier.padding(horizontal = 4.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Health Record Scan Card
        val strokeColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        Surface(
            shape = RoundedCornerShape(40.dp),
            color = Color(0xFFE0E0E0),
            modifier = Modifier.fillMaxWidth().weight(0.8f, fill = false).clickable { navController.navigate(Screen.CameraScan.name) }
        ) {
            Box(modifier = Modifier.fillMaxSize().drawBehind {
                drawRoundRect(
                    color = strokeColor,
                    style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(40.dp.toPx(), 40.dp.toPx())
                )
            }) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(24.dp).fillMaxSize()) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                        Text("Scan Record", color = MaterialTheme.colorScheme.primary, fontSize = 18.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Update health booklet", color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Surface(shape = RoundedCornerShape(20.dp), color = Color.White, shadowElevation = 2.dp, modifier = Modifier.size(56.dp)) {
                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp)) }
                    }
                }
            }
        }
    }
}

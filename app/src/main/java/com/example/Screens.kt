package com.example

import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors

enum class Screen {
    Welcome, CreateAccount, PetInfo, CustomizeProfile, Home, CameraScan
}

@Composable
fun AppNavigation(viewModel: BichouViewModel) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.name,
        enterTransition = { fadeIn(tween(500)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(500)) },
        exitTransition = { fadeOut(tween(500)) },
        popEnterTransition = { fadeIn(tween(500)) },
        popExitTransition = { fadeOut(tween(500)) + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(500)) }
    ) {
        composable(Screen.Welcome.name) { WelcomeScreen(navController) }
        composable(Screen.CreateAccount.name) { CreateAccountScreen(navController, viewModel) }
        composable(Screen.PetInfo.name) { PetInfoScreen(navController, viewModel) }
        composable(Screen.CustomizeProfile.name) { CustomizeProfileScreen(navController, viewModel) }
        composable(Screen.Home.name) { HomeScreen(navController, viewModel) }
        composable(Screen.CameraScan.name) { CameraScanScreen(navController, viewModel) }
    }
}

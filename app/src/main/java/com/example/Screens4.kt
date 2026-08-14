package com.example

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Base64
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors

@Composable
fun CameraScanScreen(navController: NavController, viewModel: BichouViewModel) {
    var showCamera by remember { mutableStateOf(false) }
    var isAnalyzing by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    
    if (showCamera) {
        Box(modifier = Modifier.fillMaxSize()) {
            val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
            val imageCapture = remember { ImageCapture.Builder().build() }
            
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageCapture
                            )
                        } catch (e: Exception) {
                            // handle error
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                }
            )
            
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally) {
                if (isAnalyzing) {
                    CircularProgressIndicator(color = Color.White)
                    Text("Bichou AI is analyzing...", color = Color.White, modifier = Modifier.padding(16.dp))
                } else {
                    Button(
                        onClick = {
                            isAnalyzing = true
                            imageCapture.takePicture(
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageCapturedCallback() {
                                    override fun onCaptureSuccess(image: ImageProxy) {
                                        val bitmap = image.toBitmap()
                                        val matrix = Matrix()
                                        matrix.postRotate(image.imageInfo.rotationDegrees.toFloat())
                                        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                                        
                                        val outputStream = ByteArrayOutputStream()
                                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                                        val base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
                                        
                                        viewModel.analyzeHealthRecord(base64String) { resultString ->
                                            if (resultString == "Very Critical") {
                                                coroutineScope.launch {
                                                    for (i in 0..4) {
                                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        kotlinx.coroutines.delay(200)
                                                    }
                                                }
                                            }
                                            navController.popBackStack()
                                        }
                                        image.close()
                                    }
                                    
                                    override fun onError(exception: ImageCaptureException) {
                                        isAnalyzing = false
                                        navController.popBackStack()
                                    }
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.padding(32.dp).fillMaxWidth().height(60.dp),
                        shape = RoundedCornerShape(30.dp)
                    ) {
                        Text("Take Picture", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Bichou AI Scan", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                "Scan the last entry the veterinarian made in your pet's health record to determine its health status.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = { showCamera = true },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Agree", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            }
        }
    }
}

package com.example

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.ui.theme.PetGender

@Composable
fun WelcomeScreen(navController: NavController) {
    val bgColor = Color(0xFFCACACA) // Light gray background
    val darkBlue = Color(0xFF143048)
    val buttonGray = Color(0xFF6B6B6B)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(vertical = 32.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "Welcome",
            fontSize = 64.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Pets,
                contentDescription = null,
                tint = darkBlue,
                modifier = Modifier
                    .size(80.dp)
                    .rotate(-20f)
            )
            
            Spacer(modifier = Modifier.width(32.dp))
            
            Text(
                text = "to",
                fontSize = 56.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.width(32.dp))
            
            Icon(
                imageVector = Icons.Default.Pets,
                contentDescription = null,
                tint = darkBlue,
                modifier = Modifier
                    .size(80.dp)
                    .rotate(20f)
            )
        }
        
        Text(
            text = "Hyper Pet",
            fontSize = 64.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black,
            style = TextStyle(
                shadow = Shadow(
                    color = Color(0x66000000),
                    offset = Offset(6f, 6f),
                    blurRadius = 8f
                )
            )
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Pets,
                contentDescription = null,
                tint = darkBlue,
                modifier = Modifier
                    .size(80.dp)
                    .rotate(-20f)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Image(
                painter = painterResource(id = R.drawable.hyper_pet_icon_1781450040649), 
                contentDescription = "Pet avatar",
                modifier = Modifier
                    .size(160.dp),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Icon(
                imageVector = Icons.Default.Pets,
                contentDescription = null,
                tint = darkBlue,
                modifier = Modifier
                    .size(80.dp)
                    .rotate(20f)
            )
        }
        
        Button(
            onClick = { navController.navigate(Screen.CreateAccount.name) },
            shape = RoundedCornerShape(40.dp),
            modifier = Modifier
                .width(280.dp)
                .height(80.dp),
            colors = ButtonDefaults.buttonColors(containerColor = buttonGray)
        ) {
            Text("Next", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun CreateAccountScreen(navController: NavController, viewModel: BichouViewModel) {
    val petName by viewModel.petName.collectAsState()
    
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Create Your Animal Account", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        
        Surface(
            shape = RoundedCornerShape(40.dp),
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Button(onClick = {}, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(30.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)) {
                    Text("Sign in with Google")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {}, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(30.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)) {
                    Text("Sign in with Apple")
                }
                Spacer(modifier = Modifier.height(24.dp))
                
                OutlinedTextField(
                    value = petName,
                    onValueChange = { viewModel.setPetName(it) },
                    label = { Text("Enter your Animal Name:", color = Color.White) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { navController.navigate(Screen.PetInfo.name) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                ) {
                    Text("Next", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

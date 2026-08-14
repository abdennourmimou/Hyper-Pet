package com.example

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.ui.theme.PetGender

@Composable
fun PetInfoScreen(navController: NavController, viewModel: BichouViewModel) {
    val petGender by viewModel.petGender.collectAsState()
    
    var species by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var length by remember { mutableStateOf("") }
    var sterilized by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }

    val validSpecies = listOf("cat", "dog", "bird")
    val speciesError = species.isNotBlank() && !validSpecies.contains(species.trim().lowercase())
    
    val allFilled = species.isNotBlank() && breed.isNotBlank() && dob.isNotBlank() && 
                    weight.isNotBlank() && length.isNotBlank() && sterilized.isNotBlank() && 
                    allergies.isNotBlank()
                    
    val isFormValid = allFilled && !speciesError && petGender != PetGender.NONE
    
    val buttonColor by animateColorAsState(
        targetValue = if (isFormValid) Color.White else Color.DarkGray,
        animationSpec = tween(300)
    )
    val buttonContentColor by animateColorAsState(
        targetValue = if (isFormValid) Color.Black else Color.Gray,
        animationSpec = tween(300)
    )
    
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text("Enter Your Pet's Information", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        item {
            Surface(
                shape = RoundedCornerShape(40.dp),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Gender:", color = Color.White, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(
                            onClick = { viewModel.setPetGender(PetGender.MALE) },
                            colors = ButtonDefaults.buttonColors(containerColor = if (petGender == PetGender.MALE) Color.White else Color.DarkGray, contentColor = if (petGender == PetGender.MALE) Color.Black else Color.White),
                            shape = RoundedCornerShape(20.dp)
                        ) { Text("Male") }
                        
                        Button(
                            onClick = { viewModel.setPetGender(PetGender.FEMALE) },
                            colors = ButtonDefaults.buttonColors(containerColor = if (petGender == PetGender.FEMALE) Color.White else Color.DarkGray, contentColor = if (petGender == PetGender.FEMALE) Color.Black else Color.White),
                            shape = RoundedCornerShape(20.dp)
                        ) { Text("Female") }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = species,
                        onValueChange = { species = it },
                        label = { Text("Species", color = Color.LightGray) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (speciesError) Color.Red else Color.White,
                            unfocusedBorderColor = if (speciesError) Color.Red else Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        isError = speciesError
                    )
                    
                    AnimatedVisibility(
                        visible = speciesError,
                        enter = expandVertically(animationSpec = tween(300)),
                        exit = shrinkVertically(animationSpec = tween(300))
                    ) {
                        Text(
                            text = "Invalid species. Please enter 'cat', 'dog', or 'bird'.",
                            color = Color(0xFFFF6B6B),
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, bottom = 8.dp)
                        )
                    }
                    
                    val textFieldColors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                    
                    OutlinedTextField(value = breed, onValueChange = { breed = it }, label = { Text("Breed", color = Color.LightGray) }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), shape = RoundedCornerShape(20.dp), colors = textFieldColors)
                    OutlinedTextField(value = dob, onValueChange = { dob = it }, label = { Text("Date of Birth", color = Color.LightGray) }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), shape = RoundedCornerShape(20.dp), colors = textFieldColors)
                    OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight (kg)", color = Color.LightGray) }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), shape = RoundedCornerShape(20.dp), colors = textFieldColors)
                    OutlinedTextField(value = length, onValueChange = { length = it }, label = { Text("Length / Height (cm)", color = Color.LightGray) }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), shape = RoundedCornerShape(20.dp), colors = textFieldColors)
                    OutlinedTextField(value = sterilized, onValueChange = { sterilized = it }, label = { Text("Sterilized / Neutered", color = Color.LightGray) }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), shape = RoundedCornerShape(20.dp), colors = textFieldColors)
                    OutlinedTextField(value = allergies, onValueChange = { allergies = it }, label = { Text("Allergies", color = Color.LightGray) }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), shape = RoundedCornerShape(20.dp), colors = textFieldColors)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            if (isFormValid) {
                                navController.navigate(Screen.CustomizeProfile.name)
                            }
                        },
                        enabled = isFormValid,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor,
                            contentColor = buttonContentColor,
                            disabledContainerColor = buttonColor,
                            disabledContentColor = buttonContentColor
                        )
                    ) {
                        Text("Next", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomizeProfileScreen(navController: NavController, viewModel: BichouViewModel) {
    val petAvatarUri by viewModel.petAvatarUri.collectAsState()
    
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.setPetAvatarUri(uri) }
    )
    
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Customize pet's profile", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        
        Surface(
            shape = RoundedCornerShape(40.dp),
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth().aspectRatio(1f)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .background(Color.DarkGray)
                        .clickable {
                            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (petAvatarUri != null) {
                        AsyncImage(
                            model = petAvatarUri,
                            contentDescription = "Pet Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.PhotoCamera, contentDescription = "Pick photo", tint = Color.White, modifier = Modifier.size(64.dp))
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = { navController.navigate(Screen.Home.name) { popUpTo(Screen.Welcome.name) { inclusive = true } } },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Finish", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

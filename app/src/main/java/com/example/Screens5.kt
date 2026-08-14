package com.example

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class CountryShop(
    val name: String,
    val url: String,
    val currency: String
)

val countryShops = listOf(
    CountryShop("Global", "https://www.amazon.com/", "$"),
    CountryShop("Algeria", "https://chicanimaldz.com/", "DZD"),
    CountryShop("Egypt", "https://aminpetshop.com/", "EGP"),
    CountryShop("Saudi Arabia", "https://aleef.com/", "SAR"),
    CountryShop("China", "https://www.boqii.com/", "CNY"),
    CountryShop("Russia", "https://www.petshop.ru/", "RUB"),
    CountryShop("France", "https://www.zooplus.fr/", "€"),
    CountryShop("USA", "https://www.chewy.com/", "$")
)

data class ProductInfo(
    val name: String,
    val price: Double,
    val benefits: List<String>
)

val catProducts = listOf(
    ProductInfo("Premium Salmon Feast", 25.99, listOf("High Protein for muscle growth", "Enriched with Omega-3 for shiny fur")),
    ProductInfo("Sterilized Cat Formula", 22.50, listOf("Optimized for sterilized/neutered health", "Weight management control")),
    ProductInfo("Digestive Care Kibbles", 18.75, listOf("Easy to digest", "Supports gut flora")),
    ProductInfo("Kitten Growth Recipe", 28.00, listOf("Essential vitamins for kittens", "Supports bone development")),
    ProductInfo("Hairball Control Chicken", 24.99, listOf("Reduces hairballs", "High fiber content"))
)

val allSuggestions = listOf("Cat Food", "Calcium Treats", "Canned Tuna", "Kitten Growth", "Premium Salmon", "Digestive Care", "Hairball Control", "Sterilized", "Chicken")

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PetFoodView(paddingVals: PaddingValues, viewModel: BichouViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf(countryShops.first()) }
    
    val context = LocalContext.current
    
    val filteredProducts = remember(searchQuery) {
        catProducts.filter { product ->
            product.name.contains(searchQuery, ignoreCase = true) ||
            product.benefits.any { it.contains(searchQuery, ignoreCase = true) }
        }
    }
    
    val activeSuggestions = remember(searchQuery) {
        if (searchQuery.isBlank()) emptyList()
        else allSuggestions.filter { it.contains(searchQuery, ignoreCase = true) && !it.equals(searchQuery, ignoreCase = true) }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingVals)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // 1. Search Bar
            Surface(
                shape = RoundedCornerShape(40.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Row(modifier = Modifier.padding(start = 20.dp, end = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search for food, toys or accessories...", color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), fontSize = 14.sp) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.primary,
                            unfocusedTextColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search", tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                        }
                    }
                }
            }
            
            // 2. Country Selector Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                countryShops.forEach { shop ->
                    val isSelected = shop == selectedCountry
                    Surface(
                        shape = RoundedCornerShape(30.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
                        shadowElevation = if (isSelected) 4.dp else 1.dp,
                        modifier = Modifier.clickable { selectedCountry = shop }
                    ) {
                        Text(
                            text = shop.name,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                }
            }
            
            // 3. Dynamic Shop Link Header & 4. Product List with Transition
            AnimatedContent(
                targetState = selectedCountry,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "country_transition"
            ) { shop ->
                Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()) {
                    Surface(
                        shape = RoundedCornerShape(40.dp),
                        color = MaterialTheme.colorScheme.primary,
                        shadowElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth().clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(shop.url))
                            context.startActivity(intent)
                        }
                    ) {
                        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.Center) {
                            Text("Shop in ${shop.name}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "Open Link", tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(shop.url, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    
                    AnimatedContent(
                        targetState = filteredProducts,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "products_transition"
                    ) { products ->
                        if (products.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFF5F5F5),
                                    modifier = Modifier.size(80.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("😿", fontSize = 40.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No products found for '$searchQuery'.\nTry searching for food or toys!",
                                    color = Color.Gray,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(products, key = { it.name }) { product ->
                                    val localPrice = when(shop.currency) {
                                        "DZD" -> product.price * 135
                                        "EGP" -> product.price * 50
                                        "SAR" -> product.price * 3.75
                                        "CNY" -> product.price * 7.2
                                        "RUB" -> product.price * 90
                                        "€" -> product.price * 0.92
                                        else -> product.price
                                    }
                                    
                                    val formattedPrice = if (shop.currency == "€" || shop.currency == "$") {
                                        "${shop.currency}${String.format("%.2f", localPrice)}"
                                    } else {
                                        "${String.format("%.0f", localPrice)} ${shop.currency}"
                                    }
                                    
                                    Surface(
                                        shape = RoundedCornerShape(30.dp),
                                        color = Color.White,
                                        shadowElevation = 2.dp,
                                        modifier = Modifier.fillMaxWidth().animateItem()
                                    ) {
                                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                shape = RoundedCornerShape(20.dp),
                                                color = Color(0xFFE0E0E0),
                                                modifier = Modifier.size(80.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text("🐱", fontSize = 32.sp) // placeholder image
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                                    Text(product.name, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
                                                    Text(formattedPrice, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp, fontWeight = FontWeight.Black)
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                product.benefits.forEach { benefit ->
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Box(modifier = Modifier.size(4.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), CircleShape))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(benefit, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Overlay Dropdown for Search Suggestions
        Box(modifier = Modifier.padding(top = 64.dp).align(Alignment.TopCenter).padding(horizontal = 8.dp)) {
            androidx.compose.animation.AnimatedVisibility(
                visible = activeSuggestions.isNotEmpty(),
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.98f),
                    shadowElevation = 12.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        activeSuggestions.take(4).forEach { suggestion ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { searchQuery = suggestion }
                                    .padding(horizontal = 20.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(suggestion, color = MaterialTheme.colorScheme.primary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}

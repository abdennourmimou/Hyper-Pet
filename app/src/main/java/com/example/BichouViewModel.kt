package com.example

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ui.theme.PetGender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatMessage(val text: String, val isUser: Boolean)

class BichouViewModel : ViewModel() {

    // --- Pet Profile States ---
    private val _petName = MutableStateFlow("")
    val petName: StateFlow<String> = _petName.asStateFlow()

    private val _petGender = MutableStateFlow(PetGender.NONE)
    val petGender: StateFlow<PetGender> = _petGender.asStateFlow()

    private val _petAvatarUri = MutableStateFlow<Uri?>(null)
    val petAvatarUri: StateFlow<Uri?> = _petAvatarUri.asStateFlow()

    private val _healthCondition = MutableStateFlow("Excellent")
    val healthCondition: StateFlow<String> = _healthCondition.asStateFlow()

    // --- Chat State ---
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // --- Setters ---
    fun setPetName(name: String) { _petName.value = name }
    fun setPetGender(gender: PetGender) { _petGender.value = gender }
    fun setPetAvatarUri(uri: Uri?) { _petAvatarUri.value = uri }
    fun setHealthCondition(condition: String) { _healthCondition.value = condition }

    fun sendMessageToAI(query: String, imageBase64: String? = null) {
        _chatMessages.update { it + ChatMessage(query, true) }
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val apiKey = "AIzaSyDCr3a8b8RF4MTqfLsK5Pk8vlE_hnHdpXM"

                val promptText = """
                    You are Bichou AI, a professional veterinary medical assistant developed by Abdennour Mimou.
                    Answer the following pet health and veterinary question in Arabic clearly, accurately, and professionally.
                    If the user query is completely unrelated to veterinary medicine or pet health, politely decline.
                    
                    User query: $query
                """.trimIndent()

                val parts = mutableListOf<Part>()
                parts.add(Part(text = promptText))

                if (imageBase64 != null) {
                    parts.add(Part(inlineData = InlineData(mimeType = "image/jpeg", data = imageBase64)))
                }

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = parts))
                )

                Log.d("BichouAI", "Sending request to Gemini API...")
                val response = RetrofitClient.service.generateContent(apiKey, request)
                Log.d("BichouAI", "Response received: ${response.candidates.size} candidates")

                val aiResponseText = response.candidates.firstOrNull()
                    ?.content?.parts?.firstOrNull()?.text
                    ?: "عذراً، لم أتمكن من معالجة الرد حالياً."

                _chatMessages.update { it + ChatMessage(aiResponseText, false) }

            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("BichouAI_Error", "HTTP ${e.code()} - $errorBody", e)
                val msg = when (e.code()) {
                    400 -> "خطأ في الطلب (400). تأكد من صحة البيانات المرسلة."
                    401, 403 -> "خطأ في الـ API Key (${e.code()}). تحقق من صلاحية المفتاح."
                    429 -> "تجاوزت حد الطلبات. انتظر قليلاً وحاول مجدداً."
                    500 -> "خطأ في سيرفر Gemini. حاول لاحقاً."
                    else -> "خطأ HTTP ${e.code()}: $errorBody"
                }
                _chatMessages.update { it + ChatMessage(msg, false) }

            } catch (e: java.net.UnknownHostException) {
                Log.e("BichouAI_Error", "No internet connection", e)
                _chatMessages.update { it + ChatMessage("لا يوجد اتصال بالإنترنت. تحقق من الشبكة.", false) }

            } catch (e: java.net.SocketTimeoutException) {
                Log.e("BichouAI_Error", "Connection timeout", e)
                _chatMessages.update { it + ChatMessage("انتهت مدة الاتصال. تحقق من سرعة الإنترنت وحاول مجدداً.", false) }

            } catch (e: Exception) {
                Log.e("BichouAI_Error", "Unexpected error: ${e.javaClass.simpleName}", e)
                _chatMessages.update { it + ChatMessage("خطأ غير متوقع: ${e.javaClass.simpleName} - ${e.message}", false) }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun analyzeHealthRecord(bitmapBase64: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val apiKey = "AIzaSyDCr3a8b8RF4MTqfLsK5Pk8vlE_hnHdpXM"
                val request = GenerateContentRequest(
                    contents = listOf(
                        Content(
                            parts = listOf(
                                Part(text = "Analyze this pet health record and determine the condition strictly using one of these three exact phrases: 'Excellent', 'Ordinary', or 'Very Critical'. Do not include any other text."),
                                Part(inlineData = InlineData(mimeType = "image/jpeg", data = bitmapBase64))
                            )
                        )
                    )
                )
                val response = RetrofitClient.service.generateContent(apiKey, request)
                val rawResult = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim() ?: "Ordinary"

                val filteredResult = when {
                    rawResult.contains("Excellent", ignoreCase = true) -> "Excellent"
                    rawResult.contains("Very Critical", ignoreCase = true) -> "Very Critical"
                    else -> "Ordinary"
                }

                _healthCondition.value = filteredResult
                onResult(filteredResult)

            } catch (e: Exception) {
                Log.e("BichouAI_Error", "Failed to analyze health record", e)
                onResult("Ordinary")
            }
        }
    }
}

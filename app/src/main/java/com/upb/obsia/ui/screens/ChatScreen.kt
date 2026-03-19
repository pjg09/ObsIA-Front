// Ruta: app/src/main/java/com/upb/obsia/ui/screens/chat_screen.kt

package com.upb.obsia.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.upb.obsia.ui.theme.AzulAuxiliarChat
import com.upb.obsia.ui.theme.FondoChat
import com.upb.obsia.ui.theme.FondoPrincipal
import com.upb.obsia.ui.theme.LetrasNegras
import com.upb.obsia.ui.theme.LetrasNegras80
import com.upb.obsia.ui.theme.MensajesUsuario
import com.upb.obsia.ui.viewmodel.ChatInitState
import com.upb.obsia.ui.viewmodel.ChatQueryState
import com.upb.obsia.ui.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
        sessionName: String,
        userId: Int,
        sessionId: Int,
        onNavigateBack: () -> Unit,
        viewModel: ChatViewModel = viewModel()
) {
    val context = LocalContext.current
    val initState by viewModel.initState.collectAsState()
    val queryState by viewModel.queryState.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    var inputText by remember { mutableStateOf("") }

    // Inicializar motor al entrar a la pantalla
    LaunchedEffect(Unit) { viewModel.initialize(context, userId, sessionId) }

    // Auto-scroll al último mensaje
    LaunchedEffect(messages.size, queryState) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(
                    index = messages.size + 1 // +1 por el mensaje de bienvenida
            )
        }
    }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = {
                            Column {
                                Text(
                                        text = "Arr\u00f3",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                )
                                Text(
                                        text = sessionName,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Volver",
                                        tint = Color.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = FondoPrincipal)
                )
            },
            bottomBar = {
                ChatInputBar(
                        inputText = inputText,
                        onTextChange = { inputText = it },
                        isEnabled =
                                initState is ChatInitState.Ready &&
                                        queryState !is ChatQueryState.Loading,
                        onSend = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendMessage(context, inputText)
                                inputText = ""
                                keyboardController?.hide()
                            }
                        }
                )
            },
            containerColor = FondoChat
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (val state = initState) {
                is ChatInitState.CopyingAssets -> {
                    EngineLoadingOverlay(message = "Preparando modelo...")
                }
                is ChatInitState.Initializing -> {
                    EngineLoadingOverlay(message = "Inicializando motor...")
                }
                is ChatInitState.Error -> {
                    EngineErrorOverlay(message = state.message)
                }
                is ChatInitState.Ready -> {
                    LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Espaciado superior
                        item { Spacer(modifier = Modifier.size(12.dp)) }

                        // Mensaje de bienvenida hardcodeado
                        item { AssistantBubble(text = viewModel.welcomeMessage) }

                        // Historial de mensajes persistidos
                        items(messages) { message ->
                            when (message.role) {
                                "user" -> UserBubble(text = message.content)
                                "assistant" -> AssistantBubble(text = message.content)
                            }
                        }

                        // Indicador de escritura
                        item {
                            AnimatedVisibility(
                                    visible = queryState is ChatQueryState.Loading,
                                    enter = fadeIn() + scaleIn(),
                                    exit = fadeOut()
                            ) { TypingIndicator() }
                        }

                        // Error de query
                        if (queryState is ChatQueryState.Error) {
                            item {
                                val errorMsg = (queryState as ChatQueryState.Error).message
                                AssistantBubble(
                                        text = "Ocurrió un error: $errorMsg",
                                        isError = true
                                )
                            }
                        }

                        // Espaciado inferior
                        item { Spacer(modifier = Modifier.size(8.dp)) }
                    }
                }
            }
        }
    }
}

// Burbujas de mensaje

@Composable
private fun UserBubble(text: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Box(
                modifier =
                        Modifier.widthIn(max = 280.dp)
                                .clip(
                                        RoundedCornerShape(
                                                topStart = 18.dp,
                                                topEnd = 18.dp,
                                                bottomStart = 18.dp,
                                                bottomEnd = 4.dp
                                        )
                                )
                                .background(MensajesUsuario)
                                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) { Text(text = text, color = Color.White, fontSize = 15.sp, lineHeight = 22.sp) }
    }
}

@Composable
private fun AssistantBubble(text: String, isError: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Box(
                modifier =
                        Modifier.widthIn(max = 280.dp)
                                .clip(
                                        RoundedCornerShape(
                                                topStart = 4.dp,
                                                topEnd = 18.dp,
                                                bottomStart = 18.dp,
                                                bottomEnd = 18.dp
                                        )
                                )
                                .background(if (isError) Color(0xFFFFE0E0) else AzulAuxiliarChat)
                                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                    text = text,
                    color = if (isError) Color(0xFFB00020) else LetrasNegras,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
            )
        }
    }
}

// Indicador de escritura animado

@Composable
private fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")

    Row(
            modifier =
                    Modifier.clip(
                                    RoundedCornerShape(
                                            topStart = 4.dp,
                                            topEnd = 18.dp,
                                            bottomStart = 18.dp,
                                            bottomEnd = 18.dp
                                    )
                            )
                            .background(AzulAuxiliarChat)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val alpha by
                    infiniteTransition.animateFloat(
                            initialValue = 0.3f,
                            targetValue = 1f,
                            animationSpec =
                                    infiniteRepeatable(
                                            animation =
                                                    tween(
                                                            durationMillis = 500,
                                                            delayMillis = index * 150,
                                                            easing = LinearEasing
                                                    ),
                                            repeatMode = RepeatMode.Reverse
                                    ),
                            label = "dot_$index"
                    )
            Box(
                    modifier =
                            Modifier.size(8.dp)
                                    .clip(CircleShape)
                                    .background(LetrasNegras80.copy(alpha = alpha))
            )
        }
    }
}

// Barra de input inferior

@Composable
private fun ChatInputBar(
        inputText: String,
        onTextChange: (String) -> Unit,
        isEnabled: Boolean,
        onSend: () -> Unit
) {
    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .background(Color.White)
                            .navigationBarsPadding()
                            .imePadding()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
                value = inputText,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                enabled = isEnabled,
                placeholder = {
                    Text(
                            text = "Escribe tu pregunta al chatbot",
                            color = LetrasNegras80,
                            fontSize = 14.sp
                    )
                },
                shape = RoundedCornerShape(24.dp),
                colors =
                        TextFieldDefaults.colors(
                                focusedContainerColor = FondoChat,
                                unfocusedContainerColor = FondoChat,
                                disabledContainerColor = FondoChat,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                focusedTextColor = LetrasNegras,
                                unfocusedTextColor = LetrasNegras
                        ),
                keyboardOptions =
                        KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Send
                        ),
                keyboardActions = KeyboardActions(onSend = { onSend() }),
                singleLine = false,
                maxLines = 4
        )

        Spacer(modifier = Modifier.width(10.dp))

        // Micrófono cuando no hay texto, enviar cuando hay texto
        Box(
                modifier = Modifier.size(50.dp).clip(CircleShape).background(FondoPrincipal),
                contentAlignment = Alignment.Center
        ) {
            if (inputText.isBlank()) {
                IconButton(onClick = { /* TODO: reconocimiento de voz */}, enabled = isEnabled) {
                    Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Micrófono",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                IconButton(onClick = onSend, enabled = isEnabled) {
                    Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

// Estados de carga y error del motor

@Composable
private fun EngineLoadingOverlay(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(color = FondoPrincipal, modifier = Modifier.size(48.dp))
            Text(
                    text = message,
                    color = LetrasNegras80,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun EngineErrorOverlay(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                    text = "Error al inicializar el motor",
                    color = LetrasNegras,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
            )
            Text(text = message, color = LetrasNegras80, fontSize = 14.sp, lineHeight = 20.sp)
        }
    }
}

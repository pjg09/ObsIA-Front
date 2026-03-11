package com.upb.obsia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.upb.obsia.NavRoutes
import com.upb.obsia.ui.theme.*

@Composable
fun RegisterScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var celular by remember { mutableStateOf("") }
    val celularError: String? = when {
        celular.isEmpty() -> null
        !celular.startsWith("3") -> "El celular debe comenzar por 3"
        celular.length != 10 -> "El celular debe tener 10 dígitos"
        else -> null
    }

    Column(modifier = Modifier.fillMaxSize().background(BlancoAuxiliar)) {

        // Header teal
        Box(
                modifier =
                        Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                                .background(FondoPrincipal)
                                .padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 32.dp)
        ) {
            Column {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = FondoBlanco
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                        text = "Regístrate",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = FondoBlanco
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                        text = "Llena con tus datos para registrarte",
                        fontSize = 14.sp,
                        color = FondoBlanco
                )
            }
        }

        // Formulario
        Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    placeholder = { Text("Ingresa tu nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors =
                            OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FondoPrincipal,
                                    unfocusedBorderColor = FondoPrincipal,
                                    focusedLabelColor = FondoPrincipal,
                                    unfocusedLabelColor = FondoPrincipal,
                            )
            )

            OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    placeholder = { Text("Ingresa tu correo electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors =
                            OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FondoPrincipal,
                                    unfocusedBorderColor = FondoPrincipal,
                                    focusedLabelColor = FondoPrincipal,
                                    unfocusedLabelColor = FondoPrincipal,
                            )
            )

            OutlinedTextField(
                    value = celular,
                    onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) celular = it },
                    label = { Text("Celular") },
                    placeholder = { Text("Ingresa tu número de celular") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = celularError != null,
                    supportingText = {
                        if (celularError != null) {
                            Text(text = celularError, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    trailingIcon = {
                        Icon(
                                imageVector = Icons.Default.VisibilityOff,
                                contentDescription = "Ocultar",
                                tint = LetrasNegras
                        )
                    },
                    colors =
                            OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FondoPrincipal,
                                    unfocusedBorderColor = FondoPrincipal,
                                    focusedLabelColor = FondoPrincipal,
                                    unfocusedLabelColor = FondoPrincipal,
                            )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón y link
        Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                    onClick = { if (celularError == null && celular.isNotEmpty()) navController.navigate(NavRoutes.REGISTER_DETAILS) },
                    enabled = celularError == null && celular.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FondoPrincipal)
            ) {
                Text(
                        text = "Registrarse",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = FondoBlanco
                )
            }

            Row {
                Text(text = "¿Ya tienes una cuenta? ", fontSize = 14.sp, color = LetrasNegras)
                TextButton(
                        onClick = { navController.navigate(NavRoutes.LOGIN) },
                        contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                            text = "Iniciar sesión",
                            fontSize = 14.sp,
                            color = FondoPrincipal,
                            fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

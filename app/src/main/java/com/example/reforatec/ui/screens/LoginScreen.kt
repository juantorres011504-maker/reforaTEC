package com.example.reforatec.ui.screens

import com.example.reforatec.ui.LoginUIState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reforatec.R

@Composable
fun LoginScreen(
    onLoginClick: (String, String, detenerCarga: () -> Unit) -> Unit,
    onForgotPasswordClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    var uiState by remember { mutableStateOf(LoginUIState()) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var correoFocused by remember { mutableStateOf(false) }
    var passwordFocused by remember { mutableStateOf(false) }

    var estaCargando by remember { mutableStateOf(false) }

    val correoLabelArriba = correoFocused || uiState.correoInput.isNotEmpty()
    val passwordLabelArriba = passwordFocused || uiState.passwordInput.isNotEmpty()

    val panelColor = MaterialTheme.colorScheme.surfaceContainerLow

    val configuration = LocalConfiguration.current
    val screenHeight = remember { configuration.screenHeightDp.dp }
    val topImageHeight = screenHeight * 0.38f
    val formMinHeight = screenHeight * 0.62f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(panelColor)
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(topImageHeight)
        ) {
            Image(
                painter = painterResource(id = R.drawable.imglogin),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .align(Alignment.BottomCenter)
            ) {
                val w = size.width
                val h = size.height
                val path = Path().apply {
                    moveTo(0f, h)
                    lineTo(0f, h * 0.5f)
                    cubicTo(w * 0.25f, 0f, w * 0.75f, h, w, h * 0.5f)
                    lineTo(w, h)
                    close()
                }
                drawPath(path = path, color = panelColor)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = formMinHeight)
                .background(panelColor)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Bienvenido",
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Inicia Sesión",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Campo Correo
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.height(22.dp)) {
                    if (correoLabelArriba) {
                        Text(
                            text = "Correo Electrónico",
                            fontSize = 12.sp,
                            color = if (correoFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
                OutlinedTextField(
                    value = uiState.correoInput,
                    onValueChange = { uiState = uiState.copy(correoInput = it, correoError = null) },
                    label = if (!correoLabelArriba) { { Text("Correo Electrónico") } } else null,
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_dialog_email),
                            contentDescription = null,
                            tint = if (correoFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    isError = uiState.correoError != null,
                    supportingText = { Text(uiState.correoError ?: " ") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { correoFocused = it.isFocused },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
                )
            }

            Spacer(modifier = Modifier.height(0.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.height(22.dp)) {
                    if (passwordLabelArriba) {
                        Text(
                            text = "Contraseña",
                            fontSize = 12.sp,
                            color = if (passwordFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
                OutlinedTextField(
                    value = uiState.passwordInput,
                    onValueChange = { uiState = uiState.copy(passwordInput = it, passwordError = null) },
                    label = if (!passwordLabelArriba) { { Text("Contraseña") } } else null,
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_lock_idle_lock),
                            contentDescription = null,
                            tint = if (passwordFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    isError = uiState.passwordError != null,
                    supportingText = { Text(uiState.passwordError ?: " ") },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = null,
                                tint = if (passwordFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { passwordFocused = it.isFocused },
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                )
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(
                    onClick = onForgotPasswordClick,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val errorCorreo = if (uiState.correoInput.isEmpty()) "Ingresa tu correo" else null
                    val errorPass = if (uiState.passwordInput.isEmpty()) "Ingresa tu contraseña" else null

                    if (errorCorreo == null && errorPass == null) {
                        estaCargando = true
                        onLoginClick(uiState.correoInput, uiState.passwordInput) {
                            estaCargando = false
                        }
                    } else {
                        uiState = uiState.copy(correoError = errorCorreo, passwordError = errorPass)
                    }
                },
                enabled = !estaCargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(50)
            ) {
                if (estaCargando) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Entrar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            TextButton(onClick = onRegisterClick) {
                Row {
                    Text(
                        text = "¿No tienes cuenta? ",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Regístrate",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(
                modifier = Modifier
                    .navigationBarsPadding()
                    .height(16.dp)
            )
        }
    }
}
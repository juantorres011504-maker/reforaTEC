package com.example.reforatec.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.reforatec.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onRegistroClick: (nombres: String, apPaterno: String, apMaterno: String, telefono: String, sexo: String, correo: String, password: String) -> Unit,
    onBackClick: () -> Unit
) {
    var nombres by remember { mutableStateOf("") }
    var apPaterno by remember { mutableStateOf("") }
    var apMaterno by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val opcionesSexo = listOf("Hombre", "Mujer", "Prefiero no decirlo")
    var sexoSeleccionado by remember { mutableStateOf("") }
    var expandirDropdown by remember { mutableStateOf(false) }

    var errNombres by remember { mutableStateOf<String?>(null) }
    var errApPaterno by remember { mutableStateOf<String?>(null) }
    var errApMaterno by remember { mutableStateOf<String?>(null) }
    var errTelefono by remember { mutableStateOf<String?>(null) }
    var errCorreo by remember { mutableStateOf<String?>(null) }
    var errPassword by remember { mutableStateOf<String?>(null) }

    var isPasswordVisible by remember { mutableStateOf(false) }
    val focusedManager = LocalFocusManager.current

    val view = LocalView.current
    val isDarkTheme = isSystemInDarkTheme()

    DisposableEffect(isDarkTheme) {
        val window = (view.context as Activity).window
        val insetsController = WindowCompat.getInsetsController(window, view)
        val originalStatus = insetsController.isAppearanceLightStatusBars
        insetsController.isAppearanceLightStatusBars = !isDarkTheme
        onDispose {
            insetsController.isAppearanceLightStatusBars = originalStatus
        }
    }

    @Composable
    fun RegistroTextFieldEstilizado(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        icon: ImageVector? = null,
        isError: Boolean,
        errorMsg: String?,
        keyboardOptions: KeyboardOptions,
        keyboardActions: KeyboardActions,
        modifier: Modifier = Modifier,
        visualTransformation: VisualTransformation = VisualTransformation.None,
        trailingIcon: @Composable (() -> Unit)? = null
    ) {
        var isFocused by remember { mutableStateOf(false) }
        val labelArriba = isFocused || value.isNotEmpty()

        Column(modifier = modifier) {
            Box(modifier = Modifier.height(22.dp)) {
                if (labelArriba) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        color = if (isFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = if (!labelArriba) { { Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant) } } else null,
                singleLine = true,
                leadingIcon = icon?.let {
                    { Icon(it, contentDescription = null, tint = if (isFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant) }
                },
                isError = isError,
                supportingText = { Text(errorMsg ?: " ") },
                visualTransformation = visualTransformation,
                trailingIcon = trailingIcon,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused },
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.statusBarsPadding().height(24.dp))

            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Logo ReforaTEC",
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Crea tu cuenta",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Únete a ReforaTEC",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RegistroTextFieldEstilizado(
                    value = nombres,
                    onValueChange = { nombres = it; errNombres = null },
                    label = "Nombres",
                    isError = errNombres != null,
                    errorMsg = errNombres,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusedManager.moveFocus(FocusDirection.Next) }),
                    modifier = Modifier.weight(1f)
                )

                Column(modifier = Modifier.weight(1f)) {
                    val labelArriba = expandirDropdown || sexoSeleccionado.isNotEmpty()

                    Box(modifier = Modifier.height(22.dp)) {
                        if (labelArriba) {
                            Text(
                                text = "Sexo",
                                fontSize = 12.sp,
                                color = if (expandirDropdown) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                    ExposedDropdownMenuBox(
                        expanded = expandirDropdown,
                        onExpandedChange = { expandirDropdown = !expandirDropdown },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = sexoSeleccionado,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = if (!labelArriba) { { Text("Sexo", color = MaterialTheme.colorScheme.onSurfaceVariant) } } else null,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirDropdown) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = MaterialTheme.colorScheme.primary
                            ),
                            supportingText = { Text(" ") }
                        )
                        ExposedDropdownMenu(
                            expanded = expandirDropdown,
                            onDismissRequest = { expandirDropdown = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            opcionesSexo.forEach { opcion ->
                                DropdownMenuItem(
                                    text = { Text(opcion) },
                                    onClick = {
                                        sexoSeleccionado = opcion
                                        expandirDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(0.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                RegistroTextFieldEstilizado(
                    value = apPaterno,
                    onValueChange = { apPaterno = it; errApPaterno = null },
                    label = "Ap. Paterno",
                    isError = errApPaterno != null,
                    errorMsg = errApPaterno,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusedManager.moveFocus(FocusDirection.Next) }),
                    modifier = Modifier.weight(1f)
                )

                RegistroTextFieldEstilizado(
                    value = apMaterno,
                    onValueChange = { apMaterno = it; errApMaterno = null },
                    label = "Ap. Materno",
                    isError = errApMaterno != null,
                    errorMsg = errApMaterno,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusedManager.moveFocus(FocusDirection.Next) }),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(0.dp))

            RegistroTextFieldEstilizado(
                value = telefono,
                onValueChange = { telefono = it; errTelefono = null },
                label = "Número Telefónico",
                icon = Icons.Filled.Phone,
                isError = errTelefono != null,
                errorMsg = errTelefono,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusedManager.moveFocus(FocusDirection.Next) }),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(0.dp))

            RegistroTextFieldEstilizado(
                value = correo,
                onValueChange = { correo = it; errCorreo = null },
                label = "Correo Electrónico",
                icon = Icons.Filled.Email,
                isError = errCorreo != null,
                errorMsg = errCorreo,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusedManager.moveFocus(FocusDirection.Next) }),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(0.dp))

            RegistroTextFieldEstilizado(
                value = password,
                onValueChange = { password = it; errPassword = null },
                label = "Contraseña",
                icon = Icons.Filled.Lock,
                isError = errPassword != null,
                errorMsg = errPassword,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(icon, contentDescription = "Mostrar contraseña")
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusedManager.clearFocus() }),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val e1 = if (nombres.isBlank()) "Requerido" else null
                    val e2 = if (apPaterno.isBlank()) "Requerido" else null
                    val e3 = if (apMaterno.isBlank()) "Requerido" else null
                    val e4 = if (telefono.isBlank()) "Requerido" else null
                    val e5 = if (correo.isBlank()) "Requerido" else null
                    val e6 = if (password.isBlank()) "Requerido" else null

                    errNombres = e1
                    errApPaterno = e2
                    errApMaterno = e3
                    errTelefono = e4
                    errCorreo = e5
                    errPassword = e6

                    if (e1 == null && e2 == null && e3 == null && e4 == null && e5 == null && e6 == null) {
                        val finalSexo = if (sexoSeleccionado.isBlank()) opcionesSexo[0] else sexoSeleccionado
                        onRegistroClick(nombres, apPaterno, apMaterno, telefono, finalSexo, correo, password)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(50)
            ) {
                Text("Registrarse", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onBackClick) {
                Row {
                    Text("¿Ya tienes cuenta? ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Inicia Sesión", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.navigationBarsPadding().height(24.dp))
        }
    }
}
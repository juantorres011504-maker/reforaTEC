package com.example.reforatec.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarArbolScreen(
    onGuardarClick: (nombre: String, especie: String, escuela: String, calle: String, numeroExt: String, colonia: String, onError: () -> Unit) -> Unit,
    onBackClick: () -> Unit
) {
    var nombreValor by remember { mutableStateOf("") }
    var especie by remember { mutableStateOf("") }
    var escuelaCampana by remember { mutableStateOf("") }

    var calle by remember { mutableStateOf("") }
    var numeroExterior by remember { mutableStateOf("") }
    var colonia by remember { mutableStateOf("") }

    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorCalle by remember { mutableStateOf<String?>(null) }
    var errorNumero by remember { mutableStateOf<String?>(null) }
    var errorColonia by remember { mutableStateOf<String?>(null) }

    var estaGuardando by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nuevo Árbol", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Identidad del Árbol",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nombreValor,
                onValueChange = { nombreValor = it; errorNombre = null },
                label = { Text("Nombre / Valor (ej. Honestidad)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                isError = errorNombre != null,
                supportingText = { Text(errorNombre ?: " ") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            OutlinedTextField(
                value = especie,
                onValueChange = { especie = it },
                label = { Text("Especie (ej. Palo de Rosa)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                supportingText = { Text(" ") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            OutlinedTextField(
                value = escuelaCampana,
                onValueChange = { escuelaCampana = it },
                label = { Text("Escuela o Campaña") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                supportingText = { Text(" ") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ubicación",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = calle,
                onValueChange = { calle = it; errorCalle = null },
                label = { Text("Calle o Avenida") },
                leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                isError = errorCalle != null,
                supportingText = { Text(errorCalle ?: " ") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = numeroExterior,
                    onValueChange = { numeroExterior = it; errorNumero = null },
                    label = { Text("Num. Ext") },
                    modifier = Modifier.weight(0.4f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    isError = errorNumero != null,
                    supportingText = { Text(errorNumero ?: " ") }
                )

                OutlinedTextField(
                    value = colonia,
                    onValueChange = { colonia = it; errorColonia = null },
                    label = { Text("Colonia") },
                    modifier = Modifier.weight(0.6f),
                    shape = RoundedCornerShape(12.dp),
                    isError = errorColonia != null,
                    supportingText = { Text(errorColonia ?: " ") },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val e1 = if (nombreValor.isBlank()) "Este campo es obligatorio" else null
                    val e2 = if (calle.isBlank()) "Requerido" else null
                    val e3 = if (numeroExterior.isBlank()) "Req." else null
                    val e4 = if (colonia.isBlank()) "Requerido" else null

                    errorNombre = e1
                    errorCalle = e2
                    errorNumero = e3
                    errorColonia = e4

                    if (e1 == null && e2 == null && e3 == null && e4 == null) {
                        estaGuardando = true
                        onGuardarClick(nombreValor, especie, escuelaCampana, calle, numeroExterior, colonia) {
                            estaGuardando = false
                        }
                    }
                },
                enabled = !estaGuardando,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(50)
            ) {
                if (estaGuardando) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Plantar Árbol", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
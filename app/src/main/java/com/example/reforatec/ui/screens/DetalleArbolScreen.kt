package com.example.reforatec.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.reforatec.data.local.entity.ArbolEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import android.content.Context
import androidx.core.content.FileProvider
import java.io.File
import androidx.compose.material.icons.filled.PhotoLibrary
import android.graphics.Matrix
import android.media.ExifInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import androidx.compose.runtime.saveable.rememberSaveable
import coil.compose.SubcomposeAsyncImage

data class ServicioEntity(
    val id: Int = 0,
    val tipo: String,
    val fecha: String,
    val hora: String,
    val comentarios: String,
    val fotoUri: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleArbolScreen(
    navController: NavController,
    tipoDetalle: String,
    arbol: ArbolEntity,
    historial: List<ServicioEntity>,
    estaCargando: Boolean = false,
    onRefresh: () -> Unit = {},
    onGuardarServicio: (tipo: String, altura: String, diametro: String, comentarios: String, fecha: String, fotoUri: String?, onErrorCallback: () -> Unit) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.shadow(elevation = 8.dp),
                title = { Text(tipoDetalle, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            when (tipoDetalle) {
                "Servicio" -> ServicioContent(
                    nombreArbol = arbol.nombreValor,
                    navController = navController,
                    onGuardarServicio = onGuardarServicio
                )
                "Historial" -> HistorialContent(
                    nombreArbol = arbol.nombreValor,
                    historial = historial,
                    estaCargando = estaCargando,
                    onRefresh = onRefresh
                )
                "Info" -> InfoContent(arbol = arbol)
                else -> Text("Pantalla no encontrada", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicioContent(
    nombreArbol: String,
    navController: NavController,
    onGuardarServicio: (String, String, String, String, String, String?, () -> Unit) -> Unit
) {
    var estaGuardandoServicio by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    var expandirDropdown by remember { mutableStateOf(false) }
    val opcionesServicio = listOf("Regar", "Podar", "Fumigar", "Fertilizar", "Medición")
    var servicioSeleccionado by remember { mutableStateOf(opcionesServicio[0]) }
    var comentarios by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }
    var diametro by remember { mutableStateOf("") }

    var fotoSeleccionadaUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var uriTemporalCamara by remember { mutableStateOf<android.net.Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                val tomarFlags = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, tomarFlags)
                fotoSeleccionadaUri = comprimirImagen(context, uri)
            } catch (e: Exception) {
                e.printStackTrace()
                fotoSeleccionadaUri = uri
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito) {
            uriTemporalCamara?.let { uriOriginal ->
                val uriLigera = comprimirImagen(context, uriOriginal)
                fotoSeleccionadaUri = uriLigera
            }
        }
    }

    var mostrarCalendario by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    var fechaSeleccionadaTexto by remember {
        mutableStateOf(SimpleDateFormat("dd - MM - yyyy", Locale.getDefault()).format(Date()))
    }

    val colorFondoCampos = MaterialTheme.colorScheme.surfaceContainerHigh
    val colorTextoPrincipal = MaterialTheme.colorScheme.onSurface
    val colorLabel = MaterialTheme.colorScheme.onSurfaceVariant

    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    val seleccion = datePickerState.selectedDateMillis
                    if (seleccion != null) {
                        val formato = SimpleDateFormat("dd - MM - yyyy", Locale.getDefault())
                        formato.timeZone = TimeZone.getTimeZone("UTC")
                        fechaSeleccionadaTexto = formato.format(Date(seleccion))
                    }
                    mostrarCalendario = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) { Text("Cancelar") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(contentColor = MaterialTheme.colorScheme.surfaceContainerLow),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = nombreArbol, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Elige el servicio que deseas agregar:", style = MaterialTheme.typography.bodyLarge, color = colorLabel)
                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = expandirDropdown,
                    onExpandedChange = { expandirDropdown = !expandirDropdown }
                ) {
                    TextField(
                        value = servicioSeleccionado, onValueChange = {}, readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirDropdown) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = TextFieldDefaults.colors(focusedContainerColor = colorFondoCampos, unfocusedContainerColor = colorFondoCampos, focusedTextColor = colorTextoPrincipal, unfocusedTextColor = colorTextoPrincipal, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandirDropdown, onDismissRequest = { expandirDropdown = false }, modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        opcionesServicio.forEach { opcion ->
                            DropdownMenuItem(text = { Text(opcion) }, onClick = { servicioSeleccionado = opcion; expandirDropdown = false })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                Text(text = "Formulario", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(visible = servicioSeleccionado == "Medición") {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Altura (m)", style = MaterialTheme.typography.bodyMedium, color = colorLabel)
                                Spacer(modifier = Modifier.height(4.dp))
                                TextField(value = altura, onValueChange = { altura = it }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next), modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = colorFondoCampos, unfocusedContainerColor = colorFondoCampos, focusedTextColor = colorTextoPrincipal, unfocusedTextColor = colorTextoPrincipal, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent), shape = RoundedCornerShape(12.dp))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Diametro (cm)", style = MaterialTheme.typography.bodyMedium, color = colorLabel)
                                Spacer(modifier = Modifier.height(4.dp))
                                TextField(value = diametro, onValueChange = { diametro = it }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done), modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = colorFondoCampos, unfocusedContainerColor = colorFondoCampos, focusedTextColor = colorTextoPrincipal, unfocusedTextColor = colorTextoPrincipal, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent), shape = RoundedCornerShape(12.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                val fotoSubida = fotoSeleccionadaUri != null

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { val nuevoUri = crearUriTemporalParaCamara(context); uriTemporalCamara = nuevoUri; cameraLauncher.launch(nuevoUri) },
                        modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = if (fotoSubida) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    ) {
                        Icon(imageVector = if (fotoSubida) Icons.Filled.CheckCircle else Icons.Filled.CameraAlt, contentDescription = "Cámara")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (fotoSubida) "Lista" else "Cámara")
                    }

                    OutlinedButton(
                        onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = if (fotoSubida) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    ) {
                        Icon(imageVector = if (fotoSubida) Icons.Filled.CheckCircle else Icons.Filled.PhotoLibrary, contentDescription = "Galería")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (fotoSubida) "Lista" else "Galería")
                    }
                }

                if (fotoSubida) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Foto adjuntada exitosamente.", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(28.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Comentarios", style = MaterialTheme.typography.bodyMedium, color = colorLabel)
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(value = comentarios, onValueChange = { comentarios = it }, placeholder = { Text("Escribe comentarios aqui") }, modifier = Modifier.fillMaxWidth().height(120.dp), colors = TextFieldDefaults.colors(focusedContainerColor = colorFondoCampos, unfocusedContainerColor = colorFondoCampos, focusedTextColor = colorTextoPrincipal, unfocusedTextColor = colorTextoPrincipal, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent), shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Fecha", style = MaterialTheme.typography.bodyMedium, color = colorLabel)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        TextField(value = fechaSeleccionadaTexto, onValueChange = {}, readOnly = true, trailingIcon = { Icon(Icons.Filled.CalendarMonth, contentDescription = "Seleccionar Fecha", modifier = Modifier.clickable { mostrarCalendario = true }) }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = colorFondoCampos, unfocusedContainerColor = colorFondoCampos, focusedTextColor = colorTextoPrincipal, unfocusedTextColor = colorTextoPrincipal, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent), shape = RoundedCornerShape(12.dp))
                        Box(modifier = Modifier.matchParentSize().clip(RoundedCornerShape(12.dp)).clickable { mostrarCalendario = true })
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        estaGuardandoServicio = true
                        onGuardarServicio(servicioSeleccionado, altura, diametro, comentarios, fechaSeleccionadaTexto, fotoSeleccionadaUri?.toString()) { estaGuardandoServicio = false }
                    },
                    enabled = !estaGuardandoServicio,
                    modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer), shape = RoundedCornerShape(24.dp)
                ) {
                    if (estaGuardandoServicio) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Registrar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.navigationBarsPadding().height(24.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialContent(
    nombreArbol: String,
    historial: List<ServicioEntity>,
    estaCargando: Boolean,
    onRefresh: () -> Unit
) {
    val opcionesFiltro = listOf("Todos", "Regar", "Podar", "Fumigar", "Fertilizar", "Medición")
    var filtroSeleccionado by remember { mutableStateOf(opcionesFiltro[0]) }

    PullToRefreshBox(
        isRefreshing = estaCargando,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = nombreArbol, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                items(opcionesFiltro) { filtro ->
                    FilterChip(
                        selected = (filtro == filtroSeleccionado),
                        onClick = { filtroSeleccionado = filtro },
                        label = { Text(filtro) },
                        colors = FilterChipDefaults.filterChipColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh, selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer, selectedLabelColor = MaterialTheme.colorScheme.primary),
                        border = null,
                        shape = RoundedCornerShape(50)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (estaCargando && historial.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, strokeWidth = 4.dp, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Cargando historial...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else if (historial.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()), contentAlignment = Alignment.Center) {
                    Text("Aún no hay servicios registrados.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                // Lista de historial
                val itemsMostrados = if (filtroSeleccionado == "Todos") historial else historial.filter { it.tipo == filtroSeleccionado }
                LazyColumn(modifier = Modifier.fillMaxSize().imePadding(), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                    items(itemsMostrados) { registro ->
                        HistorialCardItem(registro)
                    }
                    item { Spacer(modifier = Modifier.navigationBarsPadding().height(24.dp)) }
                }
            }
        }
    }
}

@Composable
fun HistorialCardItem(registro: ServicioEntity) {
    var expandido by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable { expandido = !expandido }.animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (expandido) {
                Text(text = registro.fecha, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.width(4.dp).height(24.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = registro.tipo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        if (expandido) {
                            Text(text = registro.hora, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                Icon(imageVector = if (expandido) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(32.dp))
            }

            if (expandido) {
                Spacer(modifier = Modifier.height(16.dp))

                if (!registro.fotoUri.isNullOrBlank()) {
                    SubcomposeAsyncImage(
                        model = registro.fotoUri,
                        contentDescription = "Evidencia del servicio",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(36.dp),
                                    strokeWidth = 3.dp
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Box(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp)).padding(12.dp)) {
                    Text(text = if (registro.comentarios.isBlank()) "Sin comentarios." else registro.comentarios, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun InfoContent(arbol: ArbolEntity) {
    val colorLabel = MaterialTheme.colorScheme.onSurfaceVariant
    Column(modifier = Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow), shape = RoundedCornerShape(24.dp)) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = arbol.especieNombreComun, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(16.dp))
                Image(painter = painterResource(id = arbol.imagenRes), contentDescription = null, modifier = Modifier.size(160.dp).clip(RoundedCornerShape(16.dp)), contentScale = ContentScale.Crop)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "No disponible por el momento", style = MaterialTheme.typography.titleMedium, fontStyle = FontStyle.Italic, color = colorLabel)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Detalles e Historia", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "No disponible por el momento", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Justify, color = colorLabel)
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Ubicación Geográfica", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = arbol.escuelaCampana, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${arbol.ubicacion.calle} #${arbol.ubicacion.numeroExterior}, Col. ${arbol.ubicacion.colonia}", style = MaterialTheme.typography.bodySmall, color = colorLabel)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Coordenadas: ${arbol.ubicacion.latitud}, ${arbol.ubicacion.longitud}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(modifier = Modifier.navigationBarsPadding().height(24.dp))
    }
}

fun crearUriTemporalParaCamara(context: Context): Uri {
    val archivoTemporal = File.createTempFile("evidencia_${System.currentTimeMillis()}", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivoTemporal)
}

fun comprimirImagen(context: Context, uriOriginal: Uri): Uri {
    val inputStream = context.contentResolver.openInputStream(uriOriginal)
    val bitmapOriginal = BitmapFactory.decodeStream(inputStream)
    inputStream?.close()

    if (bitmapOriginal == null) return uriOriginal

    var rotacion = 0f
    try {
        val exifStream = context.contentResolver.openInputStream(uriOriginal)
        if (exifStream != null) {
            val exif = ExifInterface(exifStream)
            val orientacion = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            rotacion = when (orientacion) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
            exifStream.close()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    val bitmapFinal = if (rotacion > 0f) {
        val matrix = Matrix()
        matrix.postRotate(rotacion)
        Bitmap.createBitmap(bitmapOriginal, 0, 0, bitmapOriginal.width, bitmapOriginal.height, matrix, true)
    } else {
        bitmapOriginal
    }

    val out = ByteArrayOutputStream()
    bitmapFinal.compress(Bitmap.CompressFormat.JPEG, 50, out)

    val archivoComprimido = File(context.cacheDir, "foto_servicio_${System.currentTimeMillis()}.jpg")
    val fileOutputStream = FileOutputStream(archivoComprimido)
    fileOutputStream.write(out.toByteArray())
    fileOutputStream.flush()
    fileOutputStream.close()

    return Uri.fromFile(archivoComprimido)
}
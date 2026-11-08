package com.guali.tutor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guali.tutor.model.Compilador
import com.guali.tutor.model.Leccion
import com.guali.tutor.model.LeccionesDeEjemplo
import com.guali.tutor.model.Objetivo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun horaActual(): String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

data class Intento(val leccion: String, val hora: String, val completada: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal() {
    var leccionActiva by remember { mutableStateOf<Leccion?>(null) }
    var codigo by remember { mutableStateOf("") }
    var compilacionExitosa by remember { mutableStateOf(false) }
    var completada by remember { mutableStateOf(false) }
    var jdk by remember { mutableStateOf("") }
    var dialogo by remember { mutableStateOf("") }
    val consola = remember { mutableStateListOf<String>() }
    val historial = remember { mutableStateListOf<Intento>() }

    fun log(mensaje: String) {
        consola += "[${horaActual()}] $mensaje"
    }

    fun objetivoCumplido() {
        val leccion = leccionActiva ?: return
        if (!completada) {
            completada = true
            if (historial.isNotEmpty()) {
                historial[0] = historial[0].copy(completada = true)
            }
            log("*** ¡Objetivo cumplido! Lección '${leccion.nombre}' completada. ***")
        }
    }

    fun compilar() {
        val resultado = Compilador.compilar(codigo)
        compilacionExitosa = resultado.exitoso
        if (resultado.exitoso) {
            log("[OK] Compilación exitosa, 0 errores.")
        } else {
            log("[ERROR] El compilador reportó errores:")
            resultado.errores.forEach { consola += it }
        }
        val objetivo = leccionActiva?.objetivo
        if ((objetivo == Objetivo.COMPILAR_EXITOSO && resultado.exitoso) ||
            (objetivo == Objetivo.COMPILAR_CON_ERROR && !resultado.exitoso)
        ) {
            objetivoCumplido()
        }
    }

    fun ejecutar() {
        log("Ejecutando...")
        Compilador.ejecutar(codigo).forEach { consola += it }
        log("[OK] El programa terminó sin errores.")
        if (leccionActiva?.objetivo == Objetivo.EJECUTAR_EXITOSO) {
            objetivoCumplido()
        }
    }

    LaunchedEffect(Unit) {
        log("Guali Tutor iniciado.")
        log("[AVISO] El JDK no está configurado. Use Configuración > Configurar JDK.")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Guali Tutor", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { relleno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
                .padding(horizontal = 8.dp)
        ) {
            BarraMenu(
                hayLeccion = leccionActiva != null,
                onAbrir = { dialogo = "abrir" },
                onCerrar = {
                    log("Lección cerrada: ${leccionActiva?.nombre}")
                    leccionActiva = null
                    codigo = ""
                    compilacionExitosa = false
                },
                onConfigurarJdk = { dialogo = "jdk" },
                onHistorial = { dialogo = "historial" },
                onAyuda = { dialogo = "ayuda" }
            )
            TituloLeccion(leccionActiva, completada)
            PanelLeccion(
                texto = leccionActiva?.contenido
                    ?: "Bienvenido a Guali Tutor.\n\nNo hay una lección activa. Use Lección > Abrir Lección.",
                modifier = Modifier.weight(1f)
            )
            EditorCodigo(
                codigo = codigo,
                onCambio = {
                    codigo = it
                    compilacionExitosa = false
                },
                modifier = Modifier.weight(1f)
            )
            BotonesAccion(
                jdkConfigurado = jdk.isNotBlank(),
                puedeEjecutar = jdk.isNotBlank() && compilacionExitosa,
                onCompilar = { compilar() },
                onEjecutar = { ejecutar() }
            )
            PanelConsola(consola)
        }
    }

    when (dialogo) {
        "abrir" -> DialogoAbrirLeccion(
            onElegir = {
                leccionActiva = it
                codigo = ""
                compilacionExitosa = false
                completada = false
                historial.add(0, Intento(it.nombre, horaActual(), false))
                log("Lección abierta: ${it.nombre}")
                log("Objetivo: ${it.objetivo.nombre}")
                dialogo = ""
            },
            onCerrar = { dialogo = "" }
        )
        "jdk" -> DialogoConfigurarJdk(
            actual = jdk,
            onGuardar = {
                jdk = it
                log("JDK configurado: $it")
                dialogo = ""
            },
            onCerrar = { dialogo = "" }
        )
        "historial" -> DialogoHistorial(historial) { dialogo = "" }
        "ayuda" -> DialogoAyuda { dialogo = "" }
    }
}

@Composable
fun BarraMenu(
    hayLeccion: Boolean,
    onAbrir: () -> Unit,
    onCerrar: () -> Unit,
    onConfigurarJdk: () -> Unit,
    onHistorial: () -> Unit,
    onAyuda: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Menu("Lección", listOf("Abrir Lección" to onAbrir, "Cerrar Lección" to onCerrar), hayLeccion)
        Menu("Configuración", listOf("Configurar JDK" to onConfigurarJdk))
        Menu("Historial", listOf("Ver Historial" to onHistorial))
        Menu("Ayuda", listOf("Guía de Uso" to onAyuda))
    }
}

@Composable
fun Menu(titulo: String, opciones: List<Pair<String, () -> Unit>>, permiteCerrar: Boolean = true) {
    var abierto by remember { mutableStateOf(false) }
    Box {
        TextButton(onClick = { abierto = true }) {
            Text(titulo, fontWeight = FontWeight.SemiBold)
        }
        DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            opciones.forEach { (texto, accion) ->
                DropdownMenuItem(
                    text = { Text(texto) },
                    enabled = texto != "Cerrar Lección" || permiteCerrar,
                    onClick = {
                        abierto = false
                        accion()
                    }
                )
            }
        }
    }
}

@Composable
fun TituloLeccion(leccion: Leccion?, completada: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Lección Activa",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier
                .background(Color.Black, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        )
        Text(
            text = "  " + (leccion?.nombre ?: "Ninguna"),
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        if (leccion != null) {
            Text(
                if (completada) "Completada" else "Pendiente",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(
                        if (completada) Color(0xFF2E7D32) else Color(0xFFC62828),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
fun PanelLeccion(texto: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(6.dp)),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = texto,
            fontSize = 14.sp,
            modifier = Modifier
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
fun EditorCodigo(codigo: String, onCambio: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = codigo,
        onValueChange = onCambio,
        label = { Text("Editor de código") },
        textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, fontSize = 13.sp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}

@Composable
fun BotonesAccion(
    jdkConfigurado: Boolean,
    puedeEjecutar: Boolean,
    onCompilar: () -> Unit,
    onEjecutar: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onCompilar, enabled = jdkConfigurado) { Text("Compilar") }
            Button(onClick = onEjecutar, enabled = puedeEjecutar) { Text("Ejecutar") }
        }
        val aviso = when {
            !jdkConfigurado -> "Se requiere configurar el JDK antes de continuar."
            !puedeEjecutar -> "Ejecutar se habilita después de una compilación exitosa."
            else -> ""
        }
        if (aviso.isNotEmpty()) {
            Text(aviso, color = Color(0xFFC62828), fontSize = 12.sp)
        }
    }
}

@Composable
fun PanelConsola(lineas: List<String>) {
    val estado = rememberLazyListState()
    LaunchedEffect(lineas.size) {
        if (lineas.isNotEmpty()) estado.animateScrollToItem(lineas.size - 1)
    }
    LazyColumn(
        state = estado,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(vertical = 6.dp)
            .background(Color(0xFF1E1E1E), RoundedCornerShape(6.dp))
            .padding(8.dp)
    ) {
        items(lineas) {
            Text(it, color = Color(0xFFD4D4D4), fontFamily = FontFamily.Monospace, fontSize = 11.sp)
        }
    }
}

@Composable
fun DialogoAbrirLeccion(onElegir: (Leccion) -> Unit, onCerrar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Abrir Lección") },
        text = {
            Column {
                LeccionesDeEjemplo.todas.forEach { leccion ->
                    TextButton(onClick = { onElegir(leccion) }) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(leccion.nombre, fontWeight = FontWeight.SemiBold)
                            Text(leccion.objetivo.nombre, fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } }
    )
}

@Composable
fun DialogoConfigurarJdk(actual: String, onGuardar: (String) -> Unit, onCerrar: () -> Unit) {
    var ruta by remember { mutableStateOf(actual.ifBlank { "C:\\Program Files\\Java\\jdk-25" }) }
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Configurar JDK") },
        text = {
            OutlinedTextField(
                value = ruta,
                onValueChange = { ruta = it },
                label = { Text("Carpeta raíz del JDK") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onGuardar(ruta.trim()) }, enabled = ruta.isNotBlank()) { Text("Aceptar") }
        },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } }
    )
}

@Composable
fun DialogoHistorial(historial: List<Intento>, onCerrar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Historial de Sesión") },
        text = {
            if (historial.isEmpty()) {
                Text("Todavía no hay intentos registrados.")
            } else {
                Column {
                    historial.forEach {
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Text(it.leccion, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            Text(it.hora, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 6.dp))
                            Text(
                                if (it.completada) "Completada" else "Pendiente",
                                fontSize = 13.sp,
                                color = if (it.completada) Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onCerrar) { Text("Cerrar") } }
    )
}

@Composable
fun DialogoAyuda(onCerrar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Guía de Uso") },
        text = {
            Text(
                "1. Configure el JDK en Configuración > Configurar JDK.\n" +
                    "2. Abra una lección en Lección > Abrir Lección.\n" +
                    "3. Copie el código de la lección en el editor.\n" +
                    "4. Presione Compilar y, si no hay errores, Ejecutar.\n" +
                    "5. Revise la salida en la consola y sus intentos en Historial."
            )
        },
        confirmButton = { TextButton(onClick = onCerrar) { Text("Entendido") } }
    )
}

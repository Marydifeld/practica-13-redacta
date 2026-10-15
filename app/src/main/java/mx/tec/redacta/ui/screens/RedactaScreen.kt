package mx.tec.redacta.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import mx.tec.redacta.R
import mx.tec.redacta.domain.EJEMPLOS
import mx.tec.redacta.domain.EstadoModelo
import mx.tec.redacta.domain.Tarea
import mx.tec.redacta.ui.components.ChipModelo
import mx.tec.redacta.ui.components.ChipSinInternet
import mx.tec.redacta.ui.components.SelectorTarea
import mx.tec.redacta.ui.components.TarjetaRespuesta
import mx.tec.redacta.ui.state.RedactaUiState
import mx.tec.redacta.ui.state.Respuesta
import mx.tec.redacta.ui.theme.RedactaTema
import mx.tec.redacta.ui.theme.RedactaTheme

/** La pantalla principal: tu texto, qué hacer con él, y lo que contesta el modelo. No tiene ViewModel. */
@Composable
fun RedactaScreen(
    estado: RedactaUiState,
    onTexto: (String) -> Unit,
    onTarea: (Tarea) -> Unit,
    onRedactar: () -> Unit,
    onDetener: () -> Unit,
    onUsar: (String) -> Unit
) {
    val e = RedactaTema.espaciado
    IconosClarosEnLaBarraDeEstado()
    Scaffold(
        topBar = { Encabezado(estado.modelo) },
        bottomBar = { BarraRedactar(estado, onRedactar, onDetener) }
    ) { relleno ->
        Column(
            modifier = Modifier
                .padding(relleno)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = e.lg, vertical = e.lg),
            verticalArrangement = Arrangement.spacedBy(e.lg)
        ) {
            Seccion("Tu texto")
            CampoTexto(estado.texto, onTexto, habilitado = !estado.generando)
            Row(horizontalArrangement = Arrangement.spacedBy(e.sm), verticalAlignment = Alignment.CenterVertically) {
                Text("Prueba con", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                EJEMPLOS.forEach { (nombre, texto) ->
                    AssistChip(onClick = { onTexto(texto) }, label = { Text(nombre) }, enabled = !estado.generando)
                }
            }
            Seccion("¿Qué hago con él?")
            SelectorTarea(estado.tarea, onTarea, habilitado = !estado.generando)
            estado.respuesta?.let { TarjetaRespuesta(it, onUsar) }
            Spacer(Modifier.height(e.sm))
        }
    }
}

/** El encabezado es verde oscuro: mientras se ve esta pantalla, la hora y la batería van en blanco. */
@Composable
private fun IconosClarosEnLaBarraDeEstado() {
    val vista = LocalView.current
    if (vista.isInEditMode) return
    DisposableEffect(Unit) {
        val barras = WindowCompat.getInsetsController((vista.context as Activity).window, vista)
        val antes = barras.isAppearanceLightStatusBars
        barras.isAppearanceLightStatusBars = false
        onDispose { barras.isAppearanceLightStatusBars = antes }
    }
}

@Composable
private fun Encabezado(modelo: EstadoModelo) {
    val c = MaterialTheme.colorScheme
    val e = RedactaTema.espaciado
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(listOf(c.primary, c.onPrimaryContainer)),
                RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
            )
            .statusBarsPadding()
            .padding(start = e.xl, end = e.lg, top = e.md, bottom = e.lg)
    ) {
        Text("Redacta", style = MaterialTheme.typography.headlineLarge, color = c.onPrimary)
        Text(
            "Tu texto, mejor escrito. El modelo vive en tu teléfono.",
            style = MaterialTheme.typography.bodyMedium,
            color = c.onPrimary.copy(alpha = 0.85f)
        )
        Spacer(Modifier.height(e.md))
        Row(horizontalArrangement = Arrangement.spacedBy(e.sm)) {
            ChipModelo(modelo)
            ChipSinInternet()
        }
    }
}

@Composable
private fun Seccion(titulo: String) {
    Text(titulo, style = MaterialTheme.typography.titleLarge)
}

@Composable
private fun CampoTexto(texto: String, onTexto: (String) -> Unit, habilitado: Boolean) {
    val c = MaterialTheme.colorScheme
    TextField(
        value = texto,
        onValueChange = onTexto,
        modifier = Modifier.fillMaxWidth(),
        enabled = habilitado,
        placeholder = { Text("Pega o escribe un aviso, un correo, un mensaje…") },
        minLines = 4,
        maxLines = 8,
        shape = MaterialTheme.shapes.extraLarge,
        trailingIcon = if (texto.isNotEmpty() && habilitado) {
            { IconButton(onClick = { onTexto("") }) { Icon(painterResource(R.drawable.ic_borrar), contentDescription = "Borrar el texto") } }
        } else null,
        supportingText = { Text("${texto.length} caracteres") },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = c.surfaceContainerHighest,
            unfocusedContainerColor = c.surfaceContainerHighest,
            disabledContainerColor = c.surfaceContainerHigh
        )
    )
}

/** Abajo, al alcance del pulgar. Mientras genera, el mismo botón detiene. */
@Composable
private fun BarraRedactar(estado: RedactaUiState, onRedactar: () -> Unit, onDetener: () -> Unit) {
    val e = RedactaTema.espaciado
    val c = MaterialTheme.colorScheme
    Surface(color = c.surfaceContainer) {
        Column(
            Modifier
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = e.lg, vertical = e.md)
        ) {
            if (estado.generando) {
                Button(
                    onClick = onDetener,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = c.errorContainer, contentColor = c.onErrorContainer)
                ) {
                    Icon(painterResource(R.drawable.ic_detener), contentDescription = null)
                    Spacer(Modifier.width(e.sm))
                    Text("Detener", style = MaterialTheme.typography.titleMedium)
                }
            } else {
                Button(
                    onClick = onRedactar,
                    enabled = estado.puedeRedactar,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = CircleShape
                ) {
                    Icon(painterResource(R.drawable.ic_chispa), contentDescription = null)
                    Spacer(Modifier.width(e.sm))
                    Text("Redactar", style = MaterialTheme.typography.titleMedium)
                }
            }
            if (estado.modelo == EstadoModelo.Cargando) {
                Text(
                    "Subiendo el modelo a memoria. La primera vez tarda hasta 20 s.",
                    style = MaterialTheme.typography.bodySmall,
                    color = c.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = e.sm)
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 760)
@Composable
private fun RedactaPreview() {
    RedactaTheme {
        RedactaScreen(
            estado = RedactaUiState(
                modelo = EstadoModelo.Listo,
                texto = EJEMPLOS.first().second,
                respuesta = Respuesta(
                    Tarea.Amable,
                    texto = "Mañana no habrá clase. Si les quedó alguna duda, pueden repasar en casa: el lunes tendremos examen.",
                    generando = true,
                    tokens = 24,
                    tokensPorSegundo = 13.5
                )
            ),
            onTexto = {}, onTarea = {}, onRedactar = {}, onDetener = {}, onUsar = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 760)
@Composable
private fun OpcionesPreview() {
    RedactaTheme(oscuro = true) {
        RedactaScreen(
            estado = RedactaUiState(
                modelo = EstadoModelo.Listo,
                texto = EJEMPLOS.last().second,
                tarea = Tarea.Responder,
                respuesta = Respuesta(
                    Tarea.Responder,
                    texto = "1. ¡Sí vamos!\n2. No puedo, lo siento.\n3. Te confirmo el jueves.",
                    generando = false,
                    tokens = 30,
                    tokensPorSegundo = 12.8,
                    opciones = listOf("¡Sí vamos!", "No puedo, lo siento.", "Te confirmo el jueves.")
                )
            ),
            onTexto = {}, onTarea = {}, onRedactar = {}, onDetener = {}, onUsar = {}
        )
    }
}
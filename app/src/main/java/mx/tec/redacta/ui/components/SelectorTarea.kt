package mx.tec.redacta.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import mx.tec.redacta.R
import mx.tec.redacta.domain.Tarea
import mx.tec.redacta.ui.theme.RedactaTema

/** Qué hacer con el texto. Una sola a la vez: la elegida lleva las chispas. */
@Composable
fun SelectorTarea(
    elegida: Tarea,
    onElegir: (Tarea) -> Unit,
    habilitado: Boolean,
    modifier: Modifier = Modifier
) {
    val e = RedactaTema.espaciado
    FlowRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(e.sm)) {
        Tarea.entries.forEach { tarea ->
            val seleccionada = tarea == elegida
            FilterChip(
                selected = seleccionada,
                onClick = { onElegir(tarea) },
                enabled = habilitado,
                label = { Text(tarea.etiqueta) },
                leadingIcon = if (seleccionada) {
                    { Icon(painterResource(R.drawable.ic_chispa), contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                } else null
            )
        }
    }
}
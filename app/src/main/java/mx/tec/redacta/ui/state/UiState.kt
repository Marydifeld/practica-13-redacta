package mx.tec.redacta.ui.state

import mx.tec.redacta.domain.EstadoModelo
import mx.tec.redacta.domain.`Tarea.kt`

/** Todo lo que la pantalla necesita para dibujarse. */
data class RedactaUiState(
    val modelo: EstadoModelo = EstadoModelo.Cargando,
    val texto: String = "",
    val tarea: `Tarea.kt` = `Tarea.kt`.Amable,
    /** La última respuesta. null = todavía no le has pedido nada. */
    val respuesta: Respuesta? = null
) {
    val generando: Boolean get() = respuesta?.generando == true

    val puedeRedactar: Boolean
        get() = modelo == EstadoModelo.Listo && texto.isNotBlank() && !generando
}

/** Una respuesta del modelo, mientras llega y cuando ya llegó. */
data class Respuesta(
    /** La tarea que se pidió: la de los chips puede cambiar mientras tanto. */
    val tarea: `Tarea.kt`,
    val texto: String = "",
    val generando: Boolean = true,
    val tokens: Int = 0,
    val tokensPorSegundo: Double? = null,
    /** Para «3 respuestas»: cada una por separado, cuando ya terminó. */
    val opciones: List<String> = emptyList(),
    val error: String? = null
) {
    /** Ya se pidió, pero el primer token no llega: el modelo está leyendo tu texto. */
    val leyendo: Boolean get() = generando && texto.isEmpty()
}

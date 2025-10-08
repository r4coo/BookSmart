// src/main/kotlin/Main.kt
import GestorPrestamo.calcularMulta
import GestorPrestamo.aplicarDescuento
import GestorPrestamo.validarPrerequesitosPrestamo
import GestorPrestamo.procesarPrestamoAsincronico
import GestorPrestamo.generarReporteSubtotal
import GestorPrestamo.inicializarCatalogo
import kotlinx.coroutines.runBlocking
import java.lang.IllegalArgumentException
import kotlinx.coroutines.*

/**
 * Función principal y punto de entrada.
 * Usa runBlocking para ejecutar el código asíncrono.
 */
fun main() = runBlocking {
    println("=== SISTEMA BOOKSMART ===")
    val catalogo = inicializarCatalogo() // Inicialización del catálogo

    // --- 1. MOSTRAR CATÁLOGO Y SOLICITAR INPUT ---

    println("\nCatálogo disponible:")
    // Mostrar catálogo
    catalogo.forEachIndexed { index, libro ->
        println("${index + 1}. ${libro.descripcion()}")
    }

    // Variables para almacenar la selección
    var librosPrestamo = emptyList<Libro>()
    var tipoUsuario: String

    // --- Bucle para la SELECCIÓN de libros (Valida números separados por coma) ---
    do {
        print("\nSeleccione libros para préstamo (números separados por coma, ej: 1,3): ")
        // Lee la entrada del usuario
        val inputIndices = readLine()

        // Convierte la cadena "1,3" a una lista de Int [1, 3]
        val indicesSeleccionados = inputIndices
            ?.split(",")
            ?.mapNotNull { it.trim().toIntOrNull() } // Elimina espacios y convierte a Int
            ?: emptyList()

        if (indicesSeleccionados.isEmpty() || inputIndices.isNullOrBlank()) {
            println("Entrada inválida. Intente de nuevo con números separados por comas.")
            continue
        }

        // Obtiene los objetos Libro
        librosPrestamo = indicesSeleccionados
            .mapNotNull {
                // Vuelve el índice 1-base a 0-base y asegura que esté dentro del rango
                if (it > 0 && it <= catalogo.size) catalogo[it - 1] else null
            }

        if (librosPrestamo.size != indicesSeleccionados.size) {
            println("Algunos números seleccionados no corresponden a libros válidos. Verifique su entrada.")
        }

    } while (librosPrestamo.isEmpty() || librosPrestamo.size != indicesSeleccionados.size)

    // --- Bucle para el TIPO DE USUARIO (Valida los valores permitidos) ---
    do {
        print("Tipo de usuario (estudiante, docente, externo): ")
        tipoUsuario = readLine()?.trim()?.lowercase() ?: ""
        if (tipoUsuario !in listOf("estudiante", "docente", "externo")) {
            println("Tipo de usuario inválido. Debe ser uno de: estudiante, docente, o externo.")
        }
    } while (tipoUsuario !in listOf("estudiante", "docente", "externo"))

    val diasRetraso = 0 // Variable simple para simular días de retraso

    // --- 2. VALIDACIÓN SINCRÓNICA (Manejo de Errores con try-catch) ---

    try {
        println("\nValidando solicitud...")
        validarPrerequesitosPrestamo(librosPrestamo)
        println("Validación completa.")
    } catch (e: IllegalArgumentException) {
        println("\nERROR CRÍTICO: ${e.message}")
        return@runBlocking // Termina la ejecución si hay un error crítico
    }


    // --- 3. PROCESAMIENTO ASÍNCRONO (Corrutinas) ---

    println("\nProcesando préstamo...")
    val estadoFinal = procesarPrestamoAsincronico(librosPrestamo) // Llamada a la suspend fun

    println("Estado: ${estadoFinal::class.simpleName}")


    // --- 4. GESTIÓN DEL ESTADO FINAL (Sealed Class y Resumen) ---

    when (estadoFinal) {
        is EstadoPrestamo.EnPrestamo -> {
            // Lógica de cálculo
            val subtotal: Double = generarReporteSubtotal(librosPrestamo).toDouble()
            val descuento = aplicarDescuento(subtotal, tipoUsuario)
            val multa = calcularMulta(diasRetraso)
            val total = subtotal - descuento + multa

            // Determinar porcentaje de descuento para la impresión
            val porcentajeDescuento = when (tipoUsuario.lowercase()) {
                "estudiante" -> "10%"
                "docente" -> "15%"
                "externo" -> "0%"
                else -> "0%"
            }

            println("\n=== RESUMEN DEL PRÉSTAMO ===")
            librosPrestamo.forEach { libro ->
                println("- ${libro.titulo} (${if (libro is LibroFisico) "Físico" else "Digital"}) \$${String.format("%,.3f", libro.precioBase)}")
            }

            // Desglose final
            println("\nSubtotal: \$${String.format("%,.3f", subtotal)}")
            println("Descuento ${tipoUsuario.replaceFirstChar { it.uppercase() }} (${porcentajeDescuento}): -\$${String.format("%,.3f", descuento)}")
            println("Multa por retraso: \$${String.format("%,.3f", multa)} (${if (multa > 0) "$diasRetraso días" else "sin retraso"})")
            println("TOTAL: \$${String.format("%,.3f", total)}")

            // Estado final y tiempo
            println("\nEstado final: ${estadoFinal::class.simpleName}")
            println("Tiempo estimado para retiro/activacion digital: 3 s")
        }
        is EstadoPrestamo.Error -> {
            println("\n--- ERROR ASÍNCRONO ---")
            println("Estado: ${estadoFinal::class.simpleName}")
            println("Mensaje: ${estadoFinal.msg}")
        }
        else -> {
            println("Proceso finalizado con estado: ${estadoFinal::class.simpleName}")
        }
    }
}
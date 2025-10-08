import kotlinx.coroutines.*
import kotlin.math.roundToInt

object GestorPrestamo {
    fun inicializarCatalogo(): List<Libro> {
        return listOf(
            LibroFisico("Estructuras de Datos", "Goodrich", 12990.0, 7, false),
            LibroFisico("Diccionario Enciclopédico", "Varios", 15990.0, 0, true),
            LibroDigital("Programación en Kotlin", "JetBrains", 9990.0, 10, true),
            LibroDigital("Algoritmos Básicos", "Cormen", 11990.0, 10, false)
        )
    }
    fun aplicarDescuento(subtotal: Double, tipoUsuario: String):Double {
        val porcentajeDescuento = when (tipoUsuario.lowercase()) {
            "estudiante" -> 0.10 // 10% descuento [cite: 65]
            "docente" -> 0.15    // 15% descuento [cite: 66]
            "externo" -> 0.00    // 0% descuento [cite: 67]
            else -> 0.00
        }
        return subtotal * porcentajeDescuento
    }

    fun calcularMulta(diasRetraso: Int, costoPorDia: Double = 500.0 ): Double{
        return if (diasRetraso > 0) diasRetraso * costoPorDia else 0.0
    }



    fun validarPrerequesitosPrestamo(libros: List<Libro>){
        libros.forEach { libro ->
            if(libro is LibroFisico && libro.esReferencia){
                throw IllegalArgumentException("El libro '${libro.titulo}' es de referencia y no se presta'")
            }
            if(libro.diasPrestamo < 0){
                throw IllegalArgumentException("El libro '${libro.titulo}' tiene un numero de dias de prestamo'")
            }
            if(libro.precioBase < 0){
                throw IllegalArgumentException("El libro '${libro.titulo}' tiene un precio base '")
            }
        }
    }

    suspend fun procesarPrestamoAsincronico(libros: List<Libro>):EstadoPrestamo = withContext(Dispatchers.IO){
        delay(3000L)
        val librosDigitales = libros.filterIsInstance<LibroDigital>()
        if (librosDigitales.isNotEmpty()){
            println("verificacion DRM para libros digitales")
        }
        return@withContext EstadoPrestamo.EnPrestamo
    }

    fun generarReporteSubtotal(librosPrestados: List<Libro>): Int {
        return librosPrestados.sumOf { it.calcularSubtotal().roundToInt() }
    }


}
open class Libro (
    val titulo: String,
    val autor: String,
    val precioBase: Double,
    open val diasPrestamo: Int
){
    open fun descripcion(): String{
        return "$titulo de $autor (Precio base: $precioBase, Días: $diasPrestamo)"
    }
    fun calcularSubtotal(): Double{
        return precioBase
    }
}
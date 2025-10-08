
class LibroFisico (
    titulo: String,
    autor: String,
    precioBase: Double,
    diasPrestamo: Int,
    val esReferencia: Boolean
):Libro(titulo,autor,precioBase,diasPrestamo) {

    override fun descripcion(): String{
        val refInfo = if (esReferencia) " • Referencia: NO SE PRESTA" else ""
        return "Fisico $refInfo"
    }
}
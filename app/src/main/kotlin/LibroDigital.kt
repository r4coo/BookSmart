class LibroDigital (
    titulo: String,
    autor: String,
    precioBase: Double,
    diasPrestamo: Int,
    val drm: Boolean
) : Libro(titulo, autor, precioBase, diasPrestamo){


    override fun descripcion(): String{
        val drmInfo = if (drm) " • DRM es de referencia y no se presta" else ""
        return "Digital $drmInfo"
    }
}
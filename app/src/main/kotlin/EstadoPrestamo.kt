sealed class EstadoPrestamo {
    object Pendiente : EstadoPrestamo()
    object EnPrestamo : EstadoPrestamo()
    object Devuelto : EstadoPrestamo()
    data class Error(val msg: String) : EstadoPrestamo()
}
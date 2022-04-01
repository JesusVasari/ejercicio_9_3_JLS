fun main() {
    val c = ConnectionBuilder()
    println("conectando.....")

     if (c.connection?.isValid(10)!!) {
        println("Conexión válida")

        // Deshabilito el autoCommit. Si no, tengo que quitar los commit()
        c.connection!!.autoCommit = false

        // Uso la conexión. De esta manera cierra la conexión cuando termine el bloque
        c.connection.use {

            // Me creo mi objeto DAO (Data Access Object), el cual sabe acceder a los ç
            // datos de la tabla USER. Necesita la conexión (it) para poder acceder a la
            // base de datos.
            // El objeto DAO va a tener todos los metodos necesarios para trabajar con
            // la tabla USER, y devolverá entidades MyUser.
            // Fuera de este objeto no debería hablarse de nada relacioando con la base
            // de datos.
            // Los objetos MyUser, tambien llamados entidades, se llaman
            // Objetos TO (Transfer Object) porque son objetos que transfieren datos.
            // desde la base de datos hasta las capas de logica de negocio y presentación.
            val h2DAO = it?.let { it1 -> TiendaDAO(it1) }

            // Creamos la tabla o la vaciamos si ya existe
            h2DAO?.prepareTable()

            // Insertamos 4 usuarios
            repeat(4)
            {
                h2DAO?.insert(Tienda(id_tienda = 1, nombre_tienda = "La Nena", direccion_tienda = "Callejon de la Nena"))
                h2DAO?.insert(Tienda(id_tienda = 2, nombre_tienda = "La Virgen", direccion_tienda = "Calle Rosa de Guadalupe"))
                h2DAO?.insert(Tienda(id_tienda = 3, nombre_tienda = "La Piscina", direccion_tienda = "Avenida De los Charcos"))
                h2DAO?.insert(Tienda(id_tienda = 4, nombre_tienda = "El churro", direccion_tienda = "Calle del Pason"))
                h2DAO?.insert(Tienda(id_tienda = 5, nombre_tienda = "Don Pancho", direccion_tienda = "Avenida del Reboso"))


            }  // Buscar un usuario
            val u = h2DAO?.selectById(1)

            // Si ha conseguido el usuario, por tanto no es nulo, entonces
            // actualizar el usuario
            if (u!=null)
            {
                u.nombre_tienda = "Nuevo usuario"
                h2DAO.update(u)
            }
            // Borrar un usuario
            h2DAO?.deleteById(2)

            // Seleccionar todos los usuarios
            println(h2DAO?.selectAll())
        }
    } else
        println("Conexión ERROR")
}

/**
 * Connection builder construye una conexión
 *
 * @constructor Create empty Connection builder
 */





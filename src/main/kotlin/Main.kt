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
            val h2DAO = TiendaDAO(c.connection!!)

            val h2DAA = ArticuloDAO(c.connection!!)

            // Creamos la tabla o la vaciamos si ya existe
            h2DAO.prepareTable()
            h2DAA.prepareTable()

            // Insertamos 4 usuarios
            repeat(1)
            {
                h2DAO.insert(Tienda(id = 1, nombre = "La Nena", direccion = "Callejon de la Nena"))
                h2DAO.insert(Tienda(id = 2, nombre = "La Virgen", direccion = "Calle Rosa de Guadalupe"))
                h2DAO.insert(Tienda(id = 3, nombre = "La Piscina", direccion = "Avenida De los Charcos"))
                h2DAO.insert(Tienda(id = 4, nombre = "El churro", direccion = "Calle del Pason"))
                h2DAO.insert(Tienda(id = 5, nombre = "Don Pancho", direccion = "Avenida del Reboso"))

                h2DAA.insert(Articulos(1,"CD-DVD","900 MB", 35))
                h2DAA.insert(Articulos(2,"USB","32 GB",155))
                h2DAA.insert(Articulos(3,"Laptop SONY","4 GB RAM, 300 HDD",13410))
                h2DAA.insert(Articulos(4,"Mouse óptico","700 DPI",104))
                h2DAA.insert(Articulos(5,"Disco Duro","200 TB",2300))
                h2DAA.insert(Articulos(6,"Proyector HSB","Toshiba",5500))

            }  // Buscar un usuario
            val u = h2DAO.selectById(1)

            // Si ha conseguido el usuario, por tanto no es nulo, entonces
            // actualizar el usuario
            if (u != null) {
                u.nombre = "Nuevo usuario"
                h2DAO.update(u)
            }
            // Borrar un usuario
            h2DAO.deleteById(2)

            // Seleccionar todos los usuarios
            println(h2DAO.selectAll())
        }
    } else
        println("Conexión ERROR")
}

/**
 * Connection builder construye una conexión
 *
 * @constructor Create empty Connection builder
 */





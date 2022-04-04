import java.sql.Connection
import java.sql.SQLException

class TiendaDAO (private val c: Connection){

    // En el companion object creamos todas las constantes.
    // Las constante definirán las plantillas de las sentencias que necesitamos para construir
    // los selects, inserts, deletes, updates.

    // En aquellos casos en donde necesitemos insertar un parametro, pondremos un ?
    // Luego lo sistituiremos llamando a métodos setX, donde X será (Int, String, ...)
    // dependiendo del tiempo de dato que corresponda.
    companion object {
        private const val SCHEMA = "default"
        private const val TABLE = "TIENDA"
        private const val TRUNCATE_TABLE_TIENDA_SQL = "TRUNCATE TABLE TIENDA"
        private const val CREATE_TABLE_TIENDA_SQL =
            "CREATE TABLE TIENDA (id  number(1) NOT NULL , nombre varchar(120) NOT NULL, direccion varchar(220) NOT NULL,PRIMARY KEY (id))"
        private const val INSERT_TIENDA_SQL = "INSERT INTO TIENDA (id,nombre, direccion) VALUES  (?, ?, ?)"
        private const val SELECT_TIENDA_BY_ID = "select id,nombre,direccion from TIENDA where id =?"
        private const val SELECT_ALL_TIENDA = "select * from TIENDA"
        private const val DELETE_TIENDA_SQL = "delete from TIENDA where  id= ?"
        private const val UPDATE_TIENDA_SQL = "update TIENDA set nombre = ? ,direccion= ? where id = ?"
    }


    fun prepareTable() {
        val metaData = c.metaData

        // Consulto en el esquema (Catalogo) la existencia de la TABLE
        val rs = metaData.getTables(null, SCHEMA, TABLE, null)

        // Si en rs hay resultados, borra la tabla con truncate, sino la crea
        if (rs.next())  truncateTable() else createTable()
    }

    private fun truncateTable() {
        println(TRUNCATE_TABLE_TIENDA_SQL)
        // try-with-resource statement will auto close the connection.
        try {
            c.createStatement().use { st ->
                st.execute(TRUNCATE_TABLE_TIENDA_SQL)
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    private fun createTable() {
        println(CREATE_TABLE_TIENDA_SQL)
        // try-with-resource statement will auto close the connection.
        try {
            //Get and instance of statement from the connection and use
            //the execute() method to execute the sql
            c.createStatement().use { st ->
                //SQL statement to create a table
                st.execute(CREATE_TABLE_TIENDA_SQL)
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    /**
     * Insert Inserta un objeto MyUser en la base de datos.
     * El proceso siempre es el mismo:
     *      Haciendo uso de la conexión, prepara una Statement pasandole la sentencia que se va a ejecutar
     *      en este caso, INSERT_USERS_SQL
     *      A la Statement devuelta se le aplica use
     *          Establecemos los valores por cada ? que existan en la plantilla.
     *          En este caso son 3, pq en INSERT_USERS_SQL hay tres ?
     *          Los indices tienen que ir en el mismo orden en el que aparecen
     *
     *          Finalmente, se ejecuta la Statement
     *          Se llama a commit.
     *
     * @param user
     */
    fun insert(user: Tienda) {
        println(INSERT_TIENDA_SQL)
        // try-with-resource statement will auto close the connection.
        try {
            c.prepareStatement(INSERT_TIENDA_SQL).use { st ->
                st.setInt(1, user.id)
                st.setString(2, user.nombre)
                st.setString(3, user.direccion)

                st.executeUpdate()
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    fun selectById(id: Int): Tienda? {
        var user: Tienda? = null
        // Step 1: Preparamos la Statement, asignado los valores a los indices
        //          en función de las ? que existen en la plantilla
        try {
            c.prepareStatement(SELECT_TIENDA_BY_ID).use { st ->
                st.setInt(1, id)
                println(st)
                // Step 3: Ejecuta la Statement
                val rs = st.executeQuery()

                // Step 4: Procesamos el objeto ResultSet (rs), mientras tenga valores.
                //          En este caso, si hay valores, tendrá un unico valor, puesto
                //          que estamos buscando por el ID, que es la clave primaria.
                while (rs.next()) {
                    val id = rs.getInt("id")
                    val name = rs.getString("nombre")
                    val email = rs.getString("direccion")
                    user = Tienda(id, name, email)
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return user
    }

    fun selectAll(): List<Tienda> {

        // using try-with-resources to avoid closing resources (boiler plate code)
        val users: MutableList<Tienda> = ArrayList()
        // Step 1: Establishing a Connection
        try {
            c.prepareStatement(SELECT_ALL_TIENDA).use { st ->
                println(st)
                // Step 3: Execute the query or update query
                val rs = st.executeQuery()

                // Step 4: Process the ResultSet object.
                while (rs.next()) {
                    val id = rs.getInt("id")
                    val name = rs.getString("nombre")
                    val email = rs.getString("direccion")
                    users.add(Tienda(id, name, email ))
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return users
    }

    fun deleteById(id: Int): Boolean {
        var rowDeleted = false

        try {
            c.prepareStatement(DELETE_TIENDA_SQL).use { st ->
                st.setInt(1, id)
                rowDeleted = st.executeUpdate() > 0
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
        return rowDeleted
    }

    fun update(user: Tienda): Boolean {
        var rowUpdated = false

        try {
            c.prepareStatement(UPDATE_TIENDA_SQL).use { st ->
                st.setInt(3, user.id)
                st.setString(1, user.nombre)
                st.setString(2, user.direccion)
                rowUpdated = st.executeUpdate() > 0
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
        return rowUpdated
    }

    private fun printSQLException(ex: SQLException) {
        for (e in ex) {
            if (e is SQLException) {
                e.printStackTrace(System.err)
                System.err.println("SQLState: " + e.sqlState)
                System.err.println("Error Code: " + e.errorCode)
                System.err.println("Message: " + e.message)
                var t = ex.cause
                while (t != null) {
                    println("Cause: $t")
                    t = t.cause
                }
            }
        }
    }

}
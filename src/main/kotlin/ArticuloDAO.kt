import java.sql.Connection
import java.sql.SQLException

class ArticuloDAO (private val c: Connection){
    // En el companion object creamos todas las constantes.
    // Las constante definirán las plantillas de las sentencias que necesitamos para construir
    // los selects, inserts, deletes, updates.

    // En aquellos casos en donde necesitemos insertar un parametro, pondremos un ?
    // Luego lo sistituiremos llamando a métodos setX, donde X será (Int, String, ...)
    // dependiendo del tiempo de dato que corresponda.
    companion object {
        private const val SCHEMA = "default"
        private const val TABLE = "ARTICULOS"
        private const val TRUNCATE_TABLE_ARTICULOS_SQL = "TRUNCATE TABLE ARTICULOS"
        private const val CREATE_TABLE_ARTICULOS_SQL =
            "CREATE TABLE ARTICULOS (id  numeric (1) NOT NULL , nombre varchar(200) NOT NULL, comentarios varchar(200) NOT NULL,precio numeric (10)  CHECK(precio>0),PRIMARY KEY (id) )"
        private const val INSERT_ARTICULOS_SQL = "INSERT INTO ARTICULOS (id,nombre, comentarios,precio) VALUES  (?, ?, ?, ?)"
        private const val SELECT_ARTICULOS_BY_ID = "select id,nombre from ARTICULOS where id =?"

    }


    fun prepareTable() {
        val metaData = c.metaData

        // Consulto en el esquema (Catalogo) la existencia de la TABLE
        val rs = metaData.getTables(null, SCHEMA, TABLE, null)

        // Si en rs hay resultados, borra la tabla con truncate, sino la crea
        if (rs.next())  truncateTable() else createTable()
    }

    private fun truncateTable() {
        println(TRUNCATE_TABLE_ARTICULOS_SQL)
        // try-with-resource statement will auto close the connection.
        try {
            c.createStatement().use { st ->
                st.execute(TRUNCATE_TABLE_ARTICULOS_SQL)
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    private fun createTable() {
        println(CREATE_TABLE_ARTICULOS_SQL)
        // try-with-resource statement will auto close the connection.
        try {
            //Get and instance of statement from the connection and use
            //the execute() method to execute the sql
            c.createStatement().use { st ->
                //SQL statement to create a table
                st.execute(CREATE_TABLE_ARTICULOS_SQL)
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
    fun insert(user: Articulos) {
        println(INSERT_ARTICULOS_SQL)
        // try-with-resource statement will auto close the connection.
        try {
            c.prepareStatement(INSERT_ARTICULOS_SQL).use { st ->
                st.setInt(1, user.id_articulo)
                st.setString(2, user.nombre)
                st.setString(3, user.comentario)
                st.setInt(4, user.precio)

                st.executeUpdate()
            }
            //Commit the change to the database
            c.commit()
        } catch (e: SQLException) {
            printSQLException(e)
        }
    }

    fun selectById(id: Int): Articulos? {
        var user: Articulos? = null
        // Step 1: Preparamos la Statement, asignado los valores a los indices
        //          en función de las ? que existen en la plantilla
        try {
            c.prepareStatement(SELECT_ARTICULOS_BY_ID).use { st ->
                st.setInt(1, id)
                // Step 3: Ejecuta la Statement
                val rs = st.executeQuery()

                // Step 4: Procesamos el objeto ResultSet (rs), mientras tenga valores.
                //          En este caso, si hay valores, tendrá un unico valor, puesto
                //          que estamos buscando por el ID, que es la clave primaria.
                while (rs.next()) {
                    val id = rs.getInt("id")
                    val name = rs.getString("nombre")
                    val email = rs.getString("comentario")
                    val precio = rs.getInt("precio")
                    user = Articulos(id, name, email,precio)
                }
            }

        } catch (e: SQLException) {
            printSQLException(e)
        }
        return user
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
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class ConnectionBuilder {

    // TODO Auto-generated catch block
    var connection: Connection? = null

    // La URL de conexión. Tendremos que cambiarsa según el SGBD que se use.
    private val jdbcURL = "jdbc:oracle:thin:@localhost:1521:XE"
    private val jdbcUsername = "alumno"
    private val jdbcPassword = "alumno"

    init {
        try {
            // Aqui construimos la conexión
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword)
        } catch (e: SQLException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }
} 
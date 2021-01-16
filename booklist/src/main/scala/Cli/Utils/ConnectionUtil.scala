package Cli.Utils

import java.sql.Connection
import java.sql.DriverManager

//setting up our database conncection
object ConnectionUtil {
  var conn: Connection = null;

  def getConnection(): Connection = {

    if (conn == null || conn.isClosed()) {
      classOf[org.postgresql.Driver].newInstance()

      conn = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/yyazdi",
        "yyazdi",
        "password"
      )
    }
    conn
  }
}

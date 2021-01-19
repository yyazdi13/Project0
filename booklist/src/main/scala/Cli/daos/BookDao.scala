package Cli.daos

import Cli.Utils.ConnectionUtil
import scala.io.StdIn

object BookDao {
  def getAllBooks(): Unit = {
    val conn = ConnectionUtil.getConnection()
    try {
      println(Console.BLUE + "List of saved books:" + "\n")
      // use inner join to show our bookList along with their authors from the author table
      var statement = conn.prepareStatement(
        "select title, first_name, last_name from bookList b inner join author a on b.author_id = a.author_id;"
      )
      statement.execute()
      var rs = statement.getResultSet()
      //prints columns from our result set
      while (rs.next()) {
        println("book title: " + rs.getString(1))
        println("Author first name: " + rs.getString(2))
        println("Author last name: " + rs.getString(3) + "\n")
      }
    } catch {
      case e: Exception => {
        println(Console.RED + "SQL error:")
        e.printStackTrace()
      }
    } finally {
      conn.close()
    }

  }

  def deleteBook(firstName: String, lastName: String, title: String): Unit = {
    val conn = ConnectionUtil.getConnection()
    try {
      var statement = conn.prepareStatement(
        "SELECT * FROM author where first_name = ? AND last_name = ?;"
      )
      statement.setString(1, firstName)
      statement.setString(2, lastName)
      statement.execute()
      var rs = statement.getResultSet()
      var authorId: Int = -1;
      while (rs.next()) authorId = rs.getInt("author_id")
      var bookStatement = conn.prepareStatement(
        ("DELETE from bookList where title = ? AND author_id = ?")
      )
      bookStatement.setString(1, title)
      bookStatement.setInt(2, authorId)
      bookStatement.execute()
      if (bookStatement.getUpdateCount() > 0)
        println(Console.GREEN + "successfully deleted book")
      else println(Console.YELLOW + "book not recognized")
    } catch {
      case e: Exception => {
        println(Console.RED + "SQL error:")
        e.printStackTrace()
      }
    } finally {
      conn.close()
    }
  }

  def insertBook(
      authorFirst: String,
      authorLast: String,
      title: String
  ): Unit = {
    val conn = ConnectionUtil.getConnection()
    try {
      var authorId: Int = -1;
      // if we have a that author in our DB already, we can us it's author_id. Otherwise we create a new author
      while (authorId < 0) {
        var statement = conn.prepareStatement(
          "SELECT * FROM author WHERE first_name = ?;"
        )
        statement.setString(1, authorFirst)
        statement.execute()
        val rs = statement.getResultSet()
        while (rs.next()) authorId = rs.getInt("author_id")
        // if no author id is found for the author we've searched for, then we'll create a new one
        if (authorId < 0) {
          var authorStatement = conn.prepareStatement(
            "INSERT INTO author VALUES (DEFAULT, ?, ?);"
          )
          authorStatement.setString(1, authorFirst)
          authorStatement.setString(2, authorLast)
          authorStatement.execute()
        }
      }
      // once we have our author id, we can insert our book to the bookList table
      var bookStatement = conn.prepareStatement(
        "INSERT INTO bookList VALUES (DEFAULT, ?, ?);"
      )
      bookStatement.setString(1, title)
      bookStatement.setInt(2, authorId)
      bookStatement.execute()
      println(Console.GREEN + "successfully added book to booklist!" + "\n")
    } catch {
      case e: Exception => {
        println(Console.RED + "SQL error:")
        e.printStackTrace()
      }
    } finally {
      conn.close()
    }
  }

  def updateBookTitle(newTitle: String, title: String): Unit = {
    val conn = ConnectionUtil.getConnection()
    try {
      var statement =
        conn.prepareStatement("UPDATE bookList SET title = ? WHERE title = ?;")
      statement.setString(1, newTitle)
      statement.setString(2, title)
      statement.execute()
      if (statement.getUpdateCount() > 0)
        println(Console.GREEN + "updated rows: " + statement.getUpdateCount())
      else println(Console.YELLOW + "title not recognized: " + title)
    } catch {
      case e: Exception => {
        println(Console.RED + "SQL error:")
        e.printStackTrace()
      }
    } finally {
      conn.close()
    }

  }

  def updateAuthor(
      firstName: String,
      lastName: String,
      changedFirstName: String,
      changedLastName: String
  ): Unit = {
    val conn = ConnectionUtil.getConnection()
    try {
      var updateStatment = conn.prepareStatement(
        "UPDATE author SET first_name = ?, last_name = ? WHERE first_name = ? AND last_name = ?;"
      )
      updateStatment.setString(3, firstName)
      updateStatment.setString(4, lastName)
      updateStatment.setString(1, changedFirstName)
      updateStatment.setString(2, changedLastName)
      updateStatment.execute()
      if (updateStatment.getUpdateCount() > 0)
        println(Console.GREEN + "rows changed: " + updateStatment.getUpdateCount())
      else println(Console.YELLOW + "Fail: author not recognized" + "\n")
    } catch {
      case e: Exception => {
        println(Console.RED + "SQL error:")
        e.printStackTrace()
      }
    } finally {
      conn.close()
    }
  }
}

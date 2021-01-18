package Cli
import scala.io.StdIn
import java.io.File
import Cli.Utils.ConnectionUtil
import scala.collection.mutable.ArrayBuffer
import java.sql.PreparedStatement

class Start {
  // give our menu loop a condition to start/stop on
  var on: Boolean = true;

  // prints greeting message
  def greeting(): Unit = {
    println("Welcome to Book List!")
    println("Select an option below:")
  }

  // prints user options
  def options(): Unit = {
    println("[1] Browse Books") // reads file with list of books
    println("[2] Save book") // saves a book into the DB
    println("[3] View saved books") // reads the DB to show us our saved books
    println("[4] Delete a book from your list") // deletes a book from the DB
    println("[5] Update a book") // updates book in DB
    println("[6] exits the CLI") // stops the while loop
  }

  def menu(): Unit = {
    greeting();

    //while our menu loop is on, continue
    while (on) {
      var conn = ConnectionUtil.getConnection()
      println("please put in a number corresponding to the options below:")
      options();
      val input = StdIn.readLine()

      input match {
        //uses the getText method from FileUtil print out our arrayBuffer and give each line a number
        case "1" => {
          println("Here's a list of books to choose from: \n")
          var i: Int = 0;
          var j: Int = 1;
          var books = FileUtil.getText("books.json")
          books.foreach((m: String) =>
            (
              if (m.contains("author")) {
                println(s"$i) " + m)
                i += 2;
              } else {
                println(s"$j) " + m + "\n")
                j += 2
              }
            )
          )
        }
        case "2" => {
          var books = FileUtil.getText("books.json")

          try {
            println("please put in author number")
            var authorInput = StdIn.readInt()
            println("title number?")
            var titleInput = StdIn.readInt()

            //make substrings to extract relevant info
            var book = FileUtil.getBookFromList(books, authorInput, titleInput)
            var author =
              book.substring(book.indexOf(" "), book.indexOf(("title"))).trim()
            var authorFirst : String = " ";
            var authorLast : String = " ";
            // in case the author only has one name, e.g 'homer', we'll set it to both their first and last name
            if (author.indexOf(" ") != -1) {
              authorFirst = author.substring(0, author.indexOf(" ")).trim()
              authorLast = author.substring(author.indexOf(" ")).trim()
            }
            else {
              authorFirst = author 
              authorLast = author
            }
            var title = book.substring(book.indexOf("title:") + 6).trim()
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
            println("successfully added book to booklist!" + "\n")
          } catch {
            // this happens when you put in something other than a number for author and title
            case ne: NumberFormatException => {
              println(
                "pleas try again using valid numbers corresponding to the book list" + "\n"
              )
            }
            // this exception occurs if you've switched the author and title numbers, since author nums are even and title nums are odd
            case oob: IndexOutOfBoundsException => {
              println("make sure author and title are in correct order")
            }
          }
        }
        case "3" => {
          println("List of saved books:" + "\n")
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
        }
        case "4" => {
          try {
            println("delete book")
            println("title?")
            var title = StdIn
              .readLine()
              .toLowerCase
              .split(' ')
              .map(x => if (x.length > 3) x.capitalize else x)
              .mkString(" ").capitalize
            println("Author's first name?")
            var firstName = StdIn.readLine().toLowerCase().capitalize.trim()
            println("Author's last name?")
            var lastName = StdIn.readLine().toLowerCase().capitalize.trim()
            var statement = conn.prepareStatement(
              "SELECT * FROM author where first_name = ? AND last_name = ?;"
            )
            statement.setString(1, firstName)
            statement.setString(2, lastName)
            statement.execute()
            var rs = statement.getResultSet()
            var authorId: Int = -1;
            while (rs.next()) authorId = rs.getInt("author_id")
            println(rs.getStatement())
            println(title)
            println(authorId)
            println(firstName)
            println(lastName)
            var bookStatement = conn.prepareStatement(
              ("DELETE from bookList where title = ? AND author_id = ?")
            )
            bookStatement.setString(1, title)
            bookStatement.setInt(2, authorId)
            bookStatement.execute()
            if (bookStatement.getUpdateCount() > 0) println("successfully deleted book")
            else println("book not recognized")
          } catch {
            case e: Exception => {
              println("SQL error:")
              e.printStackTrace()
            }
          }
        }
        case "5" => {
          println("update a book:")
          println("what would you like to update (author or title)?")
          var updateInput = StdIn.readLine().trim().toLowerCase()
          updateInput match {
            case "title" => {
              println("what's the title's name?")
              var title = StdIn.readLine().toLowerCase
              .split(' ')
              .map(x => if (x.length > 3) x.capitalize else x)
              .mkString(" ").capitalize

              println("what would you like to change it to?")
              var newTitle = StdIn.readLine().toLowerCase
              .split(' ')
              .map(x => if (x.length > 3) x.capitalize else x)
              .mkString(" ").capitalize
              var statement = conn.prepareStatement("UPDATE bookList SET title = ? WHERE title = ?;")
              statement.setString(1, newTitle)
              statement.setString(2, title)
              statement.execute()
              if (statement.getUpdateCount() > 0) println("updated rows: " + statement.getUpdateCount())
              else println("title not recognized")
            }
            case "author" => {
              println("what's the author's first name?")
              var firstName = StdIn.readLine().trim().capitalize
              println("last name?")
              var lastName = StdIn.readLine().trim().capitalize
              println("what would you like their new first name to be?")
              var changedFirstName = StdIn.readLine().trim().capitalize
              println("new last name?")
              var changedLastName = StdIn.readLine().trim().capitalize
              var updateStatment = conn.prepareStatement("UPDATE author SET first_name = ?, last_name = ? WHERE first_name = ? AND last_name = ?;")
              updateStatment.setString(3, firstName)
              updateStatment.setString(4, lastName)
              updateStatment.setString(1, changedFirstName)
              updateStatment.setString(2, changedLastName)
              updateStatment.execute()
              if (updateStatment.getUpdateCount() > 0) println("rows changed: " + updateStatment.getUpdateCount())
              else println("Fail: author not recognized" + "\n")
            }
            case _ => {
              println("you must type either author or title" + "\n")
            }
          }
        }
        case "6" => {
          conn.close()
          on = false;
        }
        case _ => {
          println("please pick a valid number from the list:" + "\n")
        }
      }
    }
    println("goodbye!")
  }
}

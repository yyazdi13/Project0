package Cli
import scala.io.StdIn
import java.io.File
import Cli.Utils.FileUtil
import scala.collection.mutable.ArrayBuffer
import java.sql.PreparedStatement
import Cli.daos.BookDao

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
            BookDao.insertBook(authorFirst, authorLast, title)
            
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
          BookDao.getAllBooks()
        }
        case "4" => {
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
          BookDao.deleteBook(firstName, lastName, title)
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
              BookDao.updateBookTitle(newTitle, title)
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
              BookDao.updateAuthor(firstName, lastName, changedFirstName, changedLastName)
            }
            case _ => {
              println("you must type either author or title" + "\n")
            }
          }
        }
        case "6" => {
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

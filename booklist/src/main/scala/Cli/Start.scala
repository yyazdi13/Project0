package Cli
import scala.io.StdIn
import java.io.File

class Start {
  // give our menu loop a condition to start/stop on
  var on : Boolean = true;

  // prints greeting message
  def greeting() : Unit = {
      println("Welcome to Book List!")
      println("Select an option below:")
  }

  // prints user options
  def options() : Unit = {
    println("[1] Browse Books") // reads file with list of books
    println("[2] Save book") // saves a book into the DB
    println("[3] View saved books") // reads the DB to show us our saved books
    println("[4] Delete a book from your list") // deletes a book from the DB
    println("[5] Update a book") // updates book in DB
    println("[6] exits the CLI") // stops the while loop
  }

  def menu() : Unit = {
      greeting();

      while (on){
          println("please put in a number corresponding to the options below:")
          options();
          val input = StdIn.readLine() 

          input match {
            case "1" => {
              println("Here's a list of books to choose from: \n")
              var books = FileUtil.getText("books.json")
              .foreach((m : String) => (println(m.replace("\"", ""))))
            }
            case "2" => {
              println("you chose 2 \n")
            }
            case "6" => {
                on = false;
            }
            case _ => {
              println("please pick a valid number from the list:")
            }
          }
        }
        println("goodbye!")
    }
}

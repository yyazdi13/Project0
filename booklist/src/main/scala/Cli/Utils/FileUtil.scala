package Cli.Utils

import scala.io.BufferedSource
import scala.io.Source
import java.io.File
import java.io.FileNotFoundException
import scala.collection.mutable.ArrayBuffer

object FileUtil {
  // set a bufferdSource as null, this will be our file:
  var openFile: BufferedSource = null

  def getText(filename: String): ArrayBuffer[String] = {
    // this is where our books from our file will be stored:
    var list: ArrayBuffer[String] = new ArrayBuffer;

    try {
      //set openFile equal to the file name in our parameter
      openFile = Source.fromFile(filename);
      //loop through each line and filter for author or title and add it to our arrayBuffer, removing white space and quotations
      for (
        book <- openFile
          .getLines()
          .filter((line: String) =>
            line.contains("author") || line.contains("title")
          )
      ) {
        list.addOne(book.replace("\"", "").trim().replace(",", ""))
      }
      list

    } catch {
      // if we get e file not found exception, we print the error message and our list, even if it's empty.
      case fe: FileNotFoundException =>
        fe.printStackTrace();
        return list
    } finally {
      // we want to make sure to close our file if the user has opened a new one
      if (openFile != null) openFile.close()
    }
  }

  // this gets a book's title and author at a given index
  def getBookFromList(
      books: ArrayBuffer[String],
      authorIndex: Int,
      titleIndex: Int
  ): String = {
    if (openFile != null) openFile.close()
    return books(authorIndex) + " " + books(titleIndex)

  }

}

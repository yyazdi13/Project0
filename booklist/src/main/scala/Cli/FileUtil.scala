package Cli

import scala.io.BufferedSource
import scala.io.Source
import java.io.File
import java.io.FileNotFoundException
import scala.collection.mutable.ArrayBuffer

object FileUtil {
  var openFile : BufferedSource = null
  var list : ArrayBuffer[String] = new ArrayBuffer;

  def getText(filename: String) : ArrayBuffer[String] = {
       
    try {
        openFile = Source.fromFile(filename);
        for (book <- openFile.getLines()
        .filter((line : String) => line.contains("author") || line.contains("title") )
        ) {
            list.addOne(book)
        }
        return list

    } catch {
        case fe : FileNotFoundException => 
            fe.printStackTrace();
            return list
    } finally {
      if (openFile != null) openFile.close()
    } 
  }

}

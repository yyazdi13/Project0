package Cli

import scala.io.BufferedSource
import scala.io.Source
import java.io.File
import java.io.FileNotFoundException

object FileUtil {
  var openFile : BufferedSource = null

  def getText(filename: String) : Unit = {
       
    try {
        openFile = Source.fromFile(filename);
        openFile.getLines().foreach(println)

    } catch {
        case fe : FileNotFoundException => 
            fe.printStackTrace();
    } finally {
      if (openFile != null) openFile.close()
    } 
  }

}

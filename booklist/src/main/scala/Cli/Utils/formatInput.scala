package Cli.Utils

import scala.io.StdIn

//this will provide us with some formating for our user input

object FormatInput {
  //capitalize every word
  def capitalizeName: String = {
    StdIn
      .readLine()
      .toLowerCase
      .trim()
      .split(' ')
      .map(x => x.capitalize)
      .mkString(" ")
  }
  //trying to capitalize every word except prepositions/conjunctions,
  //unless it's the first word
  def titleCase: String = {
    StdIn
      .readLine()
      .toLowerCase
      .split(' ')
      .map(x =>
        if (x.length > 2 && x != "the" && x != "and") x.capitalize else x)
      .mkString(" ")
      .capitalize
  }
}

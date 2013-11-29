
object RenameToLastCol {
    def main(args: Array[String]) {
      if (args.length != 1) {
          println("specify the directory first")
          System.exit(1)
      }
      val cur-dir = new java.io.File(args(0))
      System.setProperty("user.dir", cur
      cur-dir.listFiles.foreach(file => { 
          val name = file.getName
          val splits = name.split(' ')
          val finalname = splits(splits.length - 1)
          file.renameTo(new java.io.File(finalname))
      })
}


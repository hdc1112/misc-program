import java.net.{ServerSocket, Socket}
import java.io.{BufferedReader, InputStreamReader, PrintStream}
import java.io.IOException
import scala.util.control.Breaks._

private class ClientHandler(
  val client: Socket,
  val filepath: String,
  val flowspeed: String = "1k") extends Thread {

  override def run(): Unit = {
    // client socket end
    val outputStream = client.getOutputStream
    val printStream = new PrintStream(outputStream)

    // local cmd end
    val process = sys.runtime.exec("pv -L %s -q %s".format(flowspeed, filepath))
    val reader = new BufferedReader(new InputStreamReader(process.getInputStream))

    // progress bar
    // update the following code in multi-user environment
    // update the following code if speed mismatch
    var lines = 0
    val dot = 10
    val dotsline = 80
    println("Each dot represents %d lines, %d dots per line".format(dot, dotsline))

    breakable {
      while(true) {
        val str = reader.readLine
        Option(str) match {
          case Some(s) => 
          try {
            printStream.println(str)

            lines += 1
            if (lines % dot == 0) {
              print(".")
              // System.out.flush
              if (lines / dot == dotsline) {
                println()
                lines = 0
              }
            }

          } catch {
            case ioe: IOException => {
              println("Client IOException")
              break
            }
            case e: Exception => break
          }
          case None => break
        }
      }
    }

    println("Client %s ends".format(client.getInetAddress.getHostAddress))
  }
}

object StableFlow {
  def main(args: Array[String]) {
    if (args.length < 2) {
      println("Usage: scala StableFlow port /path/to/file [flow-speed=1k]")
      println("Example 1: scala stableFlow 9999 /tmp/1G.file")
      println("Example 2: scala stableFlow 9999 /tmp/1G.file 1k")
      println("Example 3: scala stableFlow 9999 /tmp/1G.file 1m")
      println("Warning: make sure you have \"pv\" installed")
      sys.exit(1)
    }

    val port = args(0).toInt
    val filepath = args(1)
    var flowspeed = "1k"
    if (args.length == 3) {
      flowspeed = args(2)
    }

    val server = new ServerSocket(port)

    println("Server is waiting at port: %d".format(port))

    while(true) {
      val client = server.accept()
      println("Client %s connected".format(client.getInetAddress.getHostAddress))
      new ClientHandler(client, filepath, flowspeed).start()
    }
  }
}

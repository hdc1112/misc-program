import java.net.{ServerSocket, Socket}
import java.io.{BufferedReader, InputStreamReader, PrintStream}
import java.io.IOException
import scala.util.control.Breaks._

/*
 * This program reads from a local text file,
 * and transfer it to a client by a specified
 * speed. It is like a stable data flow
 * 
 *      Dachuan Huang
 *      12/6/2013
 *      hdc1112@gmail.com
 */

private class ClientHandler(
  val client: Socket,
  val filepath: String) extends Thread {

  var flowspeed: String = "1k"
  var dot: Int = 10
  var dotsline: Int = 80

  def setFlowSpeed(f: String): this.type = {
    flowspeed = f
    println("flowspeed set to: %s".format(f))
    this
  }

  def setDot(d: Int): this.type = {
    dot = d
    println("dot set to: %d".format(d))
    this
  }

  def setDotsLine(dl: Int): this.type = {
    dotsline = dl
    println("dotsline set to: %d".format(dl))
    this
  }

  override def run(): Unit = {
    // client socket send buffer size
    // println("client socket send buffer size %d".format(client.getSendBufferSize))
    // println("client socket receive buffer size %d".format(client.getReceiveBufferSize))

    // client socket
    val outputStream = client.getOutputStream
    val printStream = new PrintStream(outputStream)

    // local cmd
    val cmd = "pv -L %s -q %s".format(flowspeed, filepath)
    println("cmd: %s".format(cmd))
    val process = sys.runtime.exec(cmd)
    val reader = new BufferedReader(new InputStreamReader(process.getInputStream))

    // progress bar
    // update the following code in multi-user environment
    var lines = 0
    println("Each dot represents %d lines, %d dots per line".format(dot, dotsline))

    breakable {
      while (true) {
        val str = reader.readLine
        Option(str) match {
          case Some(s) => 
            try {
              printStream.println(str)
              // printStream.flush
              if (printStream.checkError) {
                println("Client flush error")
                break
              }
  
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
      } // end of while
    }

    println("Client %s ends".format(client.getInetAddress.getHostAddress))
  } // end of run()

}

object StableFlow {
  def main(args: Array[String]) {
    if (args.length < 2) {
      println("Usage: scala StableFlow port /path/to/file [flow-speed=1k]")
      println("Example 1: scala StableFlow 9999 /tmp/1G.file"
        + " [default flow speed = 1k]")
      println("Example 2: scala StableFlow 9999 /tmp/1G.file 1k"
        + " [default lines per dot 10]")
      println("Example 3: scala StableFlow 9999 /tmp/1G.file 1m 50")
      println("Warning: make sure you have \"pv\" installed")
      sys.exit(1)
    }

    val port = args(0).toInt
    val filepath = args(1)
    var flowspeed = "1k"
    if (args.length >= 3) {
      flowspeed = args(2)
    }
    var dot = 10
    if (args.length >= 4) {
      dot = args(3).toInt
    }

    val server = new ServerSocket(port)

    println("Server is waiting at port: %d".format(port))

    while(true) {
      val client = server.accept()
      println("Client %s connected".format(client.getInetAddress.getHostAddress))
      new ClientHandler(client, filepath)
        .setFlowSpeed(flowspeed)
        .setDot(dot)
        .start()
    }
  }
}

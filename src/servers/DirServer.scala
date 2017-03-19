package servers

import java.net.{InetSocketAddress, Socket}
import java.nio.ByteBuffer
import tool.{Secret, Tools}
import Tools._

/**
  * Created by dog on 10/9/16.
  */
object DirServer {
   def checkDir(socket: Socket): (InetSocketAddress, Secret) = {

      log("check dir 收到一个来自 " + socket.getInetAddress.getHostAddress + ":" + socket.getPort + " 的请求")


      val in = socket.getInputStream

      val portArr = new Array[Byte](2)
      val hostLen = new Array[Byte](1)


      in.read(portArr)
      in.read(hostLen)


      val hostArr = new Array[Byte](hostLen(0))
      in.read(hostArr)

      log("check dir 收到的host len " + hostLen(0))
      log("check dir 收到的host arr " + hostArr.toList)

      val port = ByteBuffer.wrap(portArr).asShortBuffer().get() & 0xFFFF;
      val host = new String(hostArr)

      log("check dir  :" + host + ":" + port)

      (new InetSocketAddress(host, port), new Secret(Array[Byte]()))

   }
}

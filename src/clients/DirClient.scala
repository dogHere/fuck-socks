package clients

import java.net.{InetSocketAddress, Socket}
import tool.{Secret, Tools}
import Tools._

/**
  * Created by dog on 10/9/16.
  */
object DirClient {
    def connectByDir (remoteAddress:InetSocketAddress, targetAddress: InetSocketAddress) :(Socket,Secret)= {

        //val port = leftSocket.getPort
        // 63     -- 2    -- 1       --   v
        // random -- port -- hostLen --   host

        val remoteSocket = new Socket()
        remoteSocket.connect(remoteAddress)
        connectByDir(remoteSocket, targetAddress)
    }

    def connectByDir(remoteSocket: Socket, targetAddress: InetSocketAddress): (Socket,Secret) = {
        val out = remoteSocket.getOutputStream

        val port = targetAddress.getPort
        val portArr = Array[Byte]((port >>> 8).toByte, port.toByte)

        val hostArr = targetAddress.getHostString.getBytes("utf-8")
        val hostLen = Array[Byte] {
            hostArr.length.toByte
        }

        log("connect by dir hostLen " + hostLen(0))
        log("connect by dir hostArr " + hostArr.toList)

        out.write(portArr)
        out.write(hostLen)
        out.write(hostArr)
        out.flush()

        (remoteSocket, new Secret(Array[Byte]()))
    }
}

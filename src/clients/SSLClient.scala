package clients

import java.net.InetSocketAddress
import javax.net.ssl.{SSLSocket, SSLSocketFactory}
import tool.{Secret, Tools}
import Tools._

/**
  * Created by dog on 10/14/16.
  */
object SSLClient {

      val connectBySSL = (remoteAddress: InetSocketAddress, targetAddress: InetSocketAddress) => {

        //val port = leftSocket.getPort
        // 63     -- 2    -- 1       --   v
        // random -- port -- hostLen --   host

        val remoteSocket = SSLSocketFactory.getDefault.createSocket().asInstanceOf[SSLSocket]
        remoteSocket.connect(remoteAddress)
        DirClient.connectByDir(remoteSocket,targetAddress)
    }


}

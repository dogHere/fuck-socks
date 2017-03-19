package servers

import java.net.{InetSocketAddress, Socket}

import tool.Secret

/**
  * Created by dog on 10/14/16.
  */
object SSLServer {

 def checkSSL(socket: Socket): (InetSocketAddress, Secret) = DirServer.checkDir(socket)
}

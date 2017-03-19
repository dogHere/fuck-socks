package clients

import java.net.{InetSocketAddress, Socket}

import tool.Tools
import Tools._

/**
  * Created by dog on 10/2/16.
  */
object SocksClient{

  /*
    * +----+----+----+----+----+----+----+----+----+----+....+----+
    * | VN | CD | DSTPORT |      DSTIP        | USERID       |NULL|
      * +----+----+----+----+----+----+----+----+----+----+....+----+
    *   1    1      2              4           variable       1
    */


  val  connectBySocks4 = (leftSocket:Socket,remoteAddress:InetSocketAddress,targetAddress:InetSocketAddress)=>{
    val port = leftSocket.getPort
    val sock4Header =  Array[Byte](0x04,0x01)  ++
      Array[Byte]( (port>>>8).toByte,port.toByte ) ++
      leftSocket.getInetAddress.getAddress

    log("socks4 header array:"+sock4Header.toList)
    log("ip and port:"+leftSocket.getPort)

    val rightSocket = new Socket()
    rightSocket.connect(remoteAddress)
    val out = rightSocket.getOutputStream
    val in  = rightSocket.getInputStream
    out.write(sock4Header)
    out.flush()
    val inArr = new Array[Byte](8)
    in.read(inArr)
    if(inArr(1) == 0x90) {
      //成功
      rightSocket
    }else{
      rightSocket.close()
      null
    }
  }


  val  connectBySocks5 = (leftSocket:Socket,remoteAddress:InetSocketAddress)=>{
    null
  }
}
package fuckSocks

/**
  * Created by dog on 10/1/16.
  */

import java.net.{InetSocketAddress, ServerSocket, Socket}
import java.util
import javax.net.ssl.{SSLServerSocketFactory, SSLSocketFactory}

import tool._
import ConnectMethodType._
import tool._
import Tools._
import clients.{SSLClient, DirClient}
import servers.{DirServer,SocksServer, SSLServer}

/**
  * Created by dog on 9/23/16.
  */
class  FuckSocks(val leftMethod:Method,val rightMethod:Method,val remoteServerAddress:InetSocketAddress) {


  def getInstance(leftSocket: Socket): FuckSocksOneThread = {
    new FuckSocksOneThread(leftSocket)
  }

  class FuckSocksOneThread(leftSocket: Socket) extends Runnable {
    override def run(): Unit = {
      log("leftMethod is "+leftMethod)
      val (rightAddress,keyRecived) =   leftMethod match {
        case SOCKS4_METHOD => {
          SocksServer.getSocksVersion(leftSocket.getInputStream)
          SocksServer.checkSocks4(leftSocket)
        }
        case SOCKS5_METHOD => {
          SocksServer.getSocksVersion(leftSocket.getInputStream)
          SocksServer.checkSocks5(leftSocket)
        }
        case DIR => DirServer.checkDir(leftSocket)
        case SSL => SSLServer.checkSSL(leftSocket)
      }
      log("right address is : "+ rightAddress.getHostString)
      val (rightSocket,keyGened )= rightMethod match {

       case DIR => {
         DirClient.connectByDir(remoteServerAddress,rightAddress)
       }
       case SSL => {
         SSLClient.connectBySSL(remoteServerAddress,rightAddress)
       }
       case NON_METHOD => {
         val rs = new Socket()
         rs.setKeepAlive(true)
         rs.connect(rightAddress)
         (rs,new Secret(Array[Byte]()))
       }
      }
      val key:Secret = if(keyRecived.key.length != 0  ) keyRecived else keyGened
      forward(leftSocket,rightSocket,key,EDWay.NONED)

      val t = new Thread(){
        override def run(): Unit ={
          while (true) {
            Thread.sleep(1000)
            log("check if left or right socket is closed l in :" + leftSocket.isInputShutdown +" l out:"+leftSocket.isInputShutdown+" :"+rightSocket.isInputShutdown+" "+rightSocket.isOutputShutdown)
            if (leftSocket.isClosed) {
              close(rightSocket)
            }
            if (rightSocket.isClosed)
              close(leftSocket)
          }
        }
      }
      t.setDaemon(true)
      t.start
      //close(leftSocket,rightSocket)
    }
  }

}

object FuckSocks {
  val timeOut = 300
  def main(args: Array[String]) {

    if (args.length == 1) {
      if (args(0) == "-h" || args(0) == "--help") {
        println(
          """
            |-h --help Show this help and exit.
            |
            |java -jar FILE.jar [OPTION] ...
            |
            |options:
            | localHost localPort leftMethod password rightMethod remoteHost remotePort
            |
            |   localHost   : localHost to listen
            |   localPort   : localPort to listen
            |   leftMethod  : socks4 socks5 ss non
            |   password    : password
            |   rightMethod : like leftMethod
            |   remoteHost  : remoteHost to connect
            |   remotePort  : remotePort to connect
            |
            |
            |examples:
            | java -jar FILE.jar 3009                      --- a ssl server at 0.0.0.0:3009
            | java -jar FILE.jar 192.168.1.101 3009        --- a ssl server at 192.168.1.101:3009
            | java -jar FILE.jar 1080 192.168.56.101 3009  --- a socks5 client listen 127.0.0.1:1080 and connect to 192.168.56.101:3009 with ssl
            | java -jar FILE.jar 127.0.0.1  1080 socks5   12345  ssl   192.168.1.101   3009  client
            |                                              --- a socks5 client listen 127.0.0.1:1080 and connect to 192.168.1.101:3009 with ssl
            |
          """.stripMargin)
        System.exit(0)
      }
    }
    if (args.length == 8) {
      //127.0.0.1  1081 socks5   12345  ss   127.0.0.1   1088  client
      start(args)
    } else if (args.length == 1) {
      val port = Integer.parseInt(args(0))
      start("0.0.0.0", port, SSL, "c2eb86ca20bab114b9544d0f2cd3888180802023", NON_METHOD, "127.0.0.1", 1088)
    } else if (args.length == 2) {

      val host = args(0)
      val port = Integer.parseInt(args(1))
      start(host, port, SSL, "c2eb86ca20bab114b9544d0f2cd3888180802023", NON_METHOD, "127.0.0.1", 1088)
    } else if (args.length == 3) {

      val port = Integer.parseInt(args(0))
      val remoteHost = args(1)
      val remotePort = Integer.parseInt(args(2))

      start("127.0.0.1", port, SOCKS5_METHOD, "c2eb86ca20bab114b9544d0f2cd3888180802023", SSL, remoteHost, remotePort)
    }
    else {

      start("0.0.0.0", 3009, SSL , "c2eb86ca20bab114b9544d0f2cd3888180802023", NON_METHOD, "127.0.0.1", 1088)
    }
  }

  def start(arr: Array[String]): Unit = {
    if (arr.length == 8) {

      val leftPort = Integer.parseInt(arr(1))
      val leftMethod = arr(2) match {
        case "socks5" => SOCKS5_METHOD
        case "socks4" => SOCKS4_METHOD
        case "non" => NON_METHOD
        case "dir" => DIR
        case "ssl" => SSL
      }
      val rightMethod = arr(4) match {
        case "socks5" => SOCKS5_METHOD
        case "socks4" => SOCKS4_METHOD
        case "non" => NON_METHOD
        case "dir"  => DIR
        case "ssl" => SSL
      }
      val rightPort = Integer.parseInt(arr(6))


      start(arr(0), leftPort, leftMethod, arr(3), rightMethod, arr(5), rightPort)
    }
  }

  def start(localHost: String, localPort: Int, listenMethod: Method, keyword: String, connectMethod: Method, remoteHost: String, remotePort: Int): Unit = {

    //config left method
    val ss = listenMethod match {
      case SSL => SSLServerSocketFactory.getDefault.createServerSocket()
      case _:Method => new ServerSocket()
    }

    ss.bind(new InetSocketAddress(localHost, localPort))
    val fuckSocks = new FuckSocks(listenMethod, connectMethod, new InetSocketAddress(remoteHost, remotePort))

    log("start " + listenMethod  + " at " + ss.getLocalSocketAddress +  (connectMethod match {
      case NON_METHOD => ""
      case _ =>  " remote connection method is " + connectMethod+ " and remote address is "+remoteHost+":"+remotePort
    }))

    try {
      while (true) {
        val s = ss.accept()
        s.setSoTimeout(timeOut * 1000)
        log("Created a new connection to " + util.Arrays.toString(s.getInetAddress.getAddress) + ":" + s.getPort)
        if (s != null) new Thread(fuckSocks.getInstance(s)).start()
      }
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      ss.close()
    }
  }





//  def setRandomLen() = {
//    this.synchronized {
//      hashKey = MessageDigest.getInstance("SHA-256").digest(((System.currentTimeMillis / randomKeySyncInterval).toString).getBytes() ++ enKeyWords)
//      randomLen = Math.abs(hashKey.map(_.toInt).reduce(_ + _))
//    }
//  }



}

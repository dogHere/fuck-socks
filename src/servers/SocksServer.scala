package servers

import java.io.InputStream
import java.net.{InetAddress, InetSocketAddress, ServerSocket, Socket}
import java.nio.ByteBuffer
import java.util

import tool.{Secret, Tools}
import Tools._

/**
  * Created by dog on 10/1/16.
  */
class SocksServer(val localHost:String,val localPort:Int) {
  var running = true
  val ss = new ServerSocket()


  def startOneThreadServer(local:Socket): Unit ={
    val remoteAddress = SocksServer.checkSocks(local)
    val remote = new Socket()
    remote.connect(remoteAddress._1)
    Tools.forward(local, remote)
  }

  def start() {
    ss.bind(new InetSocketAddress(localHost, localPort))
      while (running) {
        val local = ss.accept()
        new Thread() {
          override def run() {
            startOneThreadServer(local)
          }
        }.start()
      }
  }
}
object SocksServer{

  object SocksVersion extends Enumeration{
    type SocksVersion =  Value
    val SOCKS4,SOCKS5 = Value
  }


  def main(args: Array[String]) {
    val socks = new SocksServer("127.0.0.1",1081)

    socks.start()


  }

  val checkSocks = (socket:Socket)=>{
    val in = socket.getInputStream
    val out = socket.getOutputStream
    val version = getSocksVersion(in)

    val remoteAddress =
      if(version ==SocksVersion.SOCKS4) checkSocks4(socket)
      else if(version == SocksVersion.SOCKS5) checkSocks5(socket)
      else null


    remoteAddress
  }

  /**

    * +----+----+----+----+----+----+----+----+----+----+....+----+
    * | VN | CD | DSTPORT |      DSTIP        | USERID       |NULL|
    * +----+----+----+----+----+----+----+----+----+----+....+----+
    *  1    1      2              4           variable       1
    *

    * @return
    */
  val checkSocks4 = (s:Socket) => {


    val dataIn = s.getInputStream
    val dataOut = s.getOutputStream


    val rsv = new Array[Byte](7)
    dataIn.read(rsv)
    dataIn.read()
    //in.read(rsv)
    //in.read()//剩下的全部读取完毕

    log("check socks4 "+rsv.toList)

    val port = ByteBuffer.wrap(rsv, 1, 2).asShortBuffer().get() & 0xFFFF;

    val rs = rsv.slice(3,7)
    log("socks4 ip array"+rs.toList)
    val host = InetAddress.getByAddress(
      rs
    ).getHostAddress();

    log("check socks4 host and port: "+host+":"+port)

    val response =new  Array[Byte](8)
    response(1) = if( rsv(0) == 1) 90 else 91//成功或者失败

    log("write socks4 checked response"+response.toList)


    dataOut.write(response)
    dataOut.flush()
//    out.write(response)
//    out.flush()

    val socket = response(1) match {
      case 90=>new Socket(host,port)
      case _ => null
    }
    log("connect to "+host+":"+port)
    //socket
    (new InetSocketAddress(host,port),new Secret(Array[Byte]()))

  }



  val checkSocks5 = (socket :Socket )=>{

    val in = socket.getInputStream
    val out = socket.getOutputStream

    val nMethod = new Array[Byte](1)
    in.read(nMethod)
    val tMethod = new Array[Byte](nMethod(0))
    in.read(tMethod)
    tMethod(0) match {
      case 0x00 => {
        log("socks5 认证请求: 不需要验证" )
        val response = Array[Byte](0x05,0x00)
        out.write(response)
        out.flush()
      }
      case 0x01 => log("socks5 认证请求是： GSSAPI")
      case 0x02 => log("socks5 认证请求是用户名和密码： 不支持")
      case 0xff => log("socks5 认证请求： 没有可接受的方法")
    }

    val proxyRequest = new Array[Byte](4)
    in.read(proxyRequest)
    log("socks5 proxy request :"+proxyRequest.toList)

    val addressType = proxyRequest(3)


    val (host,port)=addressType match {
      case 0x01 => {
        log("socks5 接受ipv4")
        val ipv4Arr = new Array[Byte](4)
        val portArr = new Array[Byte](2)
        in.read(ipv4Arr)
        in.read(portArr)

        val host = InetAddress.getByAddress(ipv4Arr).getHostAddress
        val port = ByteBuffer.wrap(portArr).asShortBuffer().get & 0xFFFF

        (host,port)

      }
      case 0x03 =>{
        log("socks5  接受域名")

        //域名长度
        val len = new Array[Byte](1)
        in.read(len)
        val domain = new Array[Byte](len(0))
        val domainPort = new Array[Byte](2)
        in.read(domain)
        in.read(domainPort)

        val host = new String(domain)
        val port = ByteBuffer.wrap(domainPort).asShortBuffer().get() & 0xFFFF

        (host,port)
      }
      case 0x04 =>{
        log("socks5 接收ipv6")
        val ipv6Arr = new Array[Byte](16)
        val ipv6Port = new Array[Byte](2)

        in.read(ipv6Arr)
        in.read(ipv6Port)

        val host = InetAddress.getByAddress(ipv6Arr).getHostAddress
        val port =  ByteBuffer.wrap(ipv6Port).asShortBuffer().get() & 0xFFFF

        (host,port)

      }
    }
    //回复request
    val response = ByteBuffer.allocate(14)
    response.put(0x05.toByte)//回复版本号

    if(proxyRequest(1)==0x01) response.put(0x00.toByte)
    else                      response.put(0x01.toByte)
    response.put(0x00.toByte)////

    response.put(0x01.toByte)//ATYP
    response.put(socket.getLocalAddress.getAddress)//////////TODO
    response.putShort((socket.getLocalPort & 0xffff).toShort  )

    val tmp = util.Arrays.copyOf(response.array(),response.position())
    log("socks5 给客户端请求的回复是："+tmp.toList)

    out.write(tmp)
    out.flush()

    log("connecting to "+host+":"+port)

    (new InetSocketAddress(host,port),new Secret(Array[Byte]()))
  }

  val  getSocksVersion = (in: InputStream)=>{
    val v = new  Array[Byte](1)
    in.read(v)
    log("get version req:"+v.toList)
    import SocksVersion._
    v(0) match {
      case 0x04 => SOCKS4
      case 0x05 => SOCKS5
    }
  }
}

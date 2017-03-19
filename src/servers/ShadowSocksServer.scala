package servers

import java.net.{InetSocketAddress, Socket}
import java.nio.ByteBuffer

import tool.{Secret, Tools}
import Tools._
import fuckSocks.FuckSocks

/**
  * Created by dog on 10/2/16.
  */


//object ShadowSocksServer{
//
//  def checkShadowSocks(socket:Socket): (InetSocketAddress,Secret) ={
//
//    log("check shadowsocks 收到一个来自 "+socket.getInetAddress.getHostAddress+":"+socket.getPort +" 的请求" )
//
//
//    val in = socket.getInputStream
//
//
//
//    //读取iv，创建key
//    val iv = new Array[Byte](16)
//    in.read(iv)
//    log("shadowsocks server 收到的 iv 是："+iv.toList)
//    val key = new Secret(FuckSocks.sharedKeyArr,iv)
//
//    val cipher = key.getEncryptCipher
//
////    val buffer = new Array[Byte](128)
////    var len = 0
////    while ({
////      len = in.read(buffer)
////      len >= 0
////    }){
////      log("shadowsocks server 受到的加密结果 :长度 "+len+" 内容" +buffer.toList )
////      val de = cipher.doFinal(buffer,0,len)
////      log("shadowsocks server 解密是 长度: "+de.length+" 内容:"+de.toList)
////    }
//
//
//
//    //创建解密流
//    //val is = new CipherInputStream(in,cipher)
//
//    val msgLen = new Array[Byte](1)
//
//    val buffer = new Array[Byte](16)
//    var arrCache = new Array[Byte](0)
//
//
//    in.read(buffer)
//    log("buffer "+buffer.toList)
//    log("-- buffer"+buffer.toList)
//    arrCache = arrCache ++ cipher.doFinal(buffer)
//    log("arrCache "+arrCache.toList)
//    val len = arrCache(0)
//    var times = len / 16
//    while ( {
//      times >0
//    }){
//      in.read(buffer)
//      arrCache = arrCache ++  cipher.doFinal( buffer)
//
//      times -=  1
//    }
//    arrCache = arrCache.slice(0,len)
//    log("shadowsocks server test arrchche length: " + arrCache.length +" 内容 :"+arrCache.toList )
//
//
//
//
//
//
//
//
//
//    log("shadowsocks 还剩下的block "+key.cipher.getBlockSize)
//    //val arrCache = portArr ++ hostLen ++ hostArr
//    //if(padLen != 0) is.read(new Array[Byte](padLen))
//
//
//    log("shodowsocks port arr " +arrCache.slice(1,3).toList)
//    log("shodowsocks host arr " +arrCache.slice(4,arrCache.length).toList)
//    val port =  ByteBuffer.wrap(arrCache.slice(1,3)).asShortBuffer().get() & 0xFFFF;
//    val host = new String(arrCache.slice(4,arrCache.length))
//
//    log("check ShadowSocks  :"+ host+":"+port)
//
//    //is.read()//读取完毕剩下的
//    //is.close()
//
//    (new InetSocketAddress(host,port),key)
//
//  }
//}

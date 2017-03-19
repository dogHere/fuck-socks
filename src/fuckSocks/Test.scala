package fuckSocks

/**
  * Created by dog on 10/1/16.
  */

import java.io._
import java.net.{Socket, InetSocketAddress}
import java.nio.ByteBuffer
import java.security.SecureRandom
import javax.crypto._
import javax.crypto.spec.{SecretKeySpec, IvParameterSpec}

import fuckSocks.FuckSocks.start
import tool.{Role, ConnectMethodType, Secret}
import ConnectMethodType._

import scala.util.Random

class Test{
  val num = 10

  def add(n:Int): Int  ={
    val newnum= num+n
    return newnum
  }

  def + (n:Int):Int={
    add(n)
  }
}

object Test {
  def main(args: Array[String]) {
    start("127.0.0.1",1088,SSL,"12345",NON_METHOD,"127.0.0.1",10000)
   val key = new Secret("123".getBytes())

    val cipherEn = key.getEncryptCipher
    val encrypted = cipherEn.doFinal(Array[Byte](1,2,3,45,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21))

    val cipherDe = key.getDecryptCipher
    println(cipherDe.doFinal(encrypted.slice(3,9)).toList)






  }
}

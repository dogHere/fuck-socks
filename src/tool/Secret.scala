package tool

import java.io.{InputStream, OutputStream}
import java.util.concurrent.CountDownLatch
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}
import javax.crypto.{Cipher, CipherInputStream, CipherOutputStream}

import tool.Tools._

class Secret(val key:Array[Byte],
             val iv:Array[Byte] = Array[Byte](1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16)){



  val IV_LENGTH = 16

  val rawKey = getRawKey(key)
  val ivSpec:IvParameterSpec = new IvParameterSpec(iv)
  val skeySpec = new SecretKeySpec(rawKey,"AES")
  //val cipher = Cipher.getInstance("AES/CFB/NoPadding")



  //val cipher = Cipher.getInstance("AES/CFB/PKCS5Padding")
  val cipher = Cipher.getInstance("AES/CFB/NoPadding")
  //val cipher = Cipher.getInstance("AES/CFB/NoPadding")




  def encrypt(in:InputStream,out:OutputStream,latch: CountDownLatch): Unit ={
    val cipher = Cipher.getInstance("AES/CFB/NoPadding")

    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);



    val _out = new CipherOutputStream(out, cipher);
    val buf = new Array[Byte](512)
    var numRead = 0
    while ({numRead = in.read(buf)
      numRead >= 0} ) {
      _out.write(buf, 0, numRead)
      log("encrypt : "+ new String(buf,0,numRead))
      log("encrypt : "+ buf.toList)
      latch.countDown()
    }
  }


  def encrypt(in:InputStream,out:OutputStream): Unit ={
    val cipher = Cipher.getInstance("AES/CFB/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);

    val buffer = new Array[Byte](512)
    var numRead = 0


    while (in.available() != 0 && numRead != -1) {
      numRead = in.read(buffer)
      var arrCache = new Array[Byte](0)
      val ps = packageIt(buffer,15)
      for (i<- ps){
        if (i.length == 15)
        arrCache ++= cipher.doFinal(Array[Byte](15) ++  i)
        else {
          log("encrypt  i len : " + i.length +" 内容 "+i.toList)
          arrCache ++= cipher.doFinal(
            Array[Byte](i.length.toByte) ++ i ++ new Array[Byte](15 - i.length)
          )
        }
      }
      out.write(arrCache)
      out.flush()
    }
  }

  def decrypt(in:InputStream,out:OutputStream): Unit ={

    val cipher = Cipher.getInstance("AES/CFB/NoPadding")
    cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);

    val buffer = new Array[Byte](512)
    var numRead = 0

    while (in.available()!=0 && numRead!= -1){
      numRead = in.read(buffer)
      var arrCache = new Array[Byte](0)
      val ps = packageIt(buffer,16)
      for(i <- ps){
        val res = cipher.doFinal(i)
        log("--decrypt-- package len (0) " +res(0) +" 内容 "+res.toList )
        arrCache ++= res.slice(1,res(0))
      }
      out.write(arrCache)
      out.flush()
    }
  }

  def transform(in:InputStream,out:OutputStream): Unit ={
    val buffer = new Array[Byte](1024)
    var len = 0

    while ({
      len = in.read(buffer)
      len!= -1
    }){
      out.write(buffer,0,len)
      out.flush()
    }
    in.close()
    out.close()
  }

  val getEncryptCipher ={
    val cipher = Cipher.getInstance("AES/CFB/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
    cipher

  }

  val getDecryptCipher = {

    val cipher = Cipher.getInstance("AES/CFB/NoPadding")
    cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);
    cipher
  }


  def decrypt(in:InputStream,out:OutputStream,latch: CountDownLatch): Unit ={

    val cipher = Cipher.getInstance("AES/CFB/NoPadding")
    cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);

    val _in = new CipherInputStream(in, cipher);
    val buf = new Array[Byte](512);
    var numRead = 0;
    while (  {numRead = _in.read(buf)
      numRead >= 0 } ) {
      out.write(buf, 0, numRead);
      log("decrypt: "+new String(buf,0,numRead))
      log("decrypt: "+buf.toList)
      latch.countDown()
    }
  }
}

object Secret{


  def main(args: Array[String]) {
    val key  = new Secret("1".getBytes(),Array[Byte](1,3))


    //key.getRawKey(Array[Byte](1,2,3,4,5,6,7,8,9,0,11,12,14,15,10))
  }


}
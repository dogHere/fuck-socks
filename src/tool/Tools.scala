package tool

/**
  * Created by dog on 10/1/16.
  */

import java.io._
import java.net.Socket
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.concurrent.{BlockingQueue, CountDownLatch}
import java.util.{Date, Random}
import javax.crypto.KeyGenerator

import tool.EDWay.EDWay


/**
  * Created by dog on 9/25/16.
  */
object Tools {



  def  close  (closeable: AutoCloseable*){
    closeable.foreach(
      x=> x match {
        case _:AutoCloseable=> x.close()
        case _ =>
      }
    )
  }
  def  isAlive  (sockets:Socket*){
    sockets.map(
      x=>x match {
        case _:Socket => !x.isClosed
        case _=>false
      }
    ).reduce(_&_)
  }

class Boll(val buffer:Array[Byte],val len:Int)

  def product(in:InputStream,pool:BlockingQueue[Boll],lock:Object): Unit ={
    val buffer = new Array[Byte](512)
    var len = 0
    while (
      {
        len = in.read(buffer)
        true
      }
    ){
      log("bokk is product")

      pool.add(new Boll(buffer,len))
      log("pool size "+pool.size())

    }

  }

  def custem(out:OutputStream,pool:BlockingQueue[Boll],lock:Object): Unit ={
    while (true){
      val boll = pool.take()
      if(boll.len!= -1) {
        out.write(boll.buffer, 0, boll.len)
        out.flush()
      }

    }
  }




  def transform(latch: CountDownLatch,in:InputStream,out:OutputStream,key:Secret,eDWay: EDWay,leftSocket:Socket):Unit={

     new Thread(){
      override def run(): Unit ={
        if       (eDWay == EDWay.DECRYPT ){
          log("正在解密....")
          key.decrypt(in,out)
        }else if (eDWay == EDWay.ENCRYPT ) {
          log("正在加密...")
          key.encrypt(in,out)
        }else if (eDWay == EDWay.NONED){
          key.transform(in,out)
        }
        latch.countDown()
      }
    }.start()
  }



  def forward(leftSocket:Socket,rightSocket:Socket,key:Secret,eDWay: EDWay): Unit ={
        val latch = new CountDownLatch(1)
        log("forward...")

        transform(latch, leftSocket.getInputStream, rightSocket.getOutputStream,key:Secret,         eDWay,leftSocket)

        Thread.sleep(50)
        transform(latch, rightSocket.getInputStream,leftSocket.getOutputStream, key:Secret, EDWay ! eDWay,leftSocket)

  }

  def forward(s1:Socket,s2:Socket): Unit ={

  }




  def log(str:String,args:Any* ): Unit ={
    println(new SimpleDateFormat("z yyyy-MM-dd HH:mm:ss.S").format(new Date)+" "+Thread.currentThread().getId+":  "
      +str)
  }


  def getRandomIv(len:Int = 16): Array[Byte] ={
    val randomArr = for(k<- 1 to len)
      yield (new Random().nextInt().toByte)
    randomArr.toArray
  }

  val  getRawKey =(seed:Array[Byte]) =>  {
    val kgen = KeyGenerator.getInstance("AES")
    val  sr = SecureRandom.getInstance("SHA1PRNG")
    sr.setSeed(seed)
    kgen.init(128, sr); // 192 and 256 bits may not be available
    val skey = kgen.generateKey()
    val raw = skey.getEncoded()
    raw
  }



//
  def packageIt(array: Array[Byte],len:Int):Array[Array[Byte]] ={

    val times = array.length/len
    val other = array.length%len
    val r:Array[Array[Byte]] = new Array[Array[Byte]](times+1)
    for(i<- 1 to times +1 )  {
      r(i-1)=
      if (i<= times)
       array.slice(len * (i-1) ,len * i  )
      else
       array.slice(len * (i-1) ,len * (i-1)  + other)
    }
    r
  }

}

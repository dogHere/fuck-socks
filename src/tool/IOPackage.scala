package tool

/**
  * Created by dog on 10/8/16.
  */
class IOPackage(val _array: Array[Byte],val trueLen:Int  ,val full:Int = 16) {

  val array = _array.slice(0,trueLen)

 // println(array.length)
  val len = array.length
  if(len>full) throw new Exception("array length is out of full")

  val inPanding = if(len<full) {
    val newArr = array++new Array[Byte](full - len)
    for(i <- len  to full-1){
      newArr(i)=(full - len).toByte
    }
    newArr
  } else array

  val exPanding = array
}
object IOPackage{
  def main(args: Array[String]) {
    val a =  Array[Byte](1,2,3,4,5,6,7,8,9,10,10)

    val io = new IOPackage(a,5)

    //io.inPanding
    println(io.inPanding.toList)
    println(io.exPanding.toList)
  }
}

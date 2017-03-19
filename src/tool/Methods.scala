package tool

/**
  * Created by dog on 10/1/16.
  */


object ConnectMethodType extends Enumeration{
  type Method = Value
  val SOCKS4_METHOD,SOCKS5_METHOD,SHADOWSOCKS_METHOD,SOCKS_METHOD,NON_METHOD,DIR,SSL = Value
}



//object EDWay{
//  type EDWay = Boolean
//  val ENCROPT:EDWay = true
//  val DECRIPT:EDWay = false
//
//}

object EDWay extends Enumeration{
  type EDWay = Value
  val ENCRYPT,DECRYPT,NONED = Value

  def !  (eDWay: EDWay) = eDWay match {
    case ENCRYPT => DECRYPT
    case DECRYPT => ENCRYPT
    case NONED   => NONED
  }

}

object Role extends Enumeration{
  type Role = Value
  val CLIENT,SERVER = Value
}
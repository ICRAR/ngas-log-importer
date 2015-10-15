package parser

class LogParser {

  val accessRegex = """^(\d{4}\-\d{2}\-\d{2}T\d{2}\:\d{2}:\d{2}\.\d{3}).*client_address=\(\'(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}).*file\_id=((\d+)\_(\d+)[^\|]+).*host=([^\s]+).*Thread\-(.*)""".r    
  val accessNoHostRegex = """^(\d{4}\-\d{2}\-\d{2}T\d{2}\:\d{2}:\d{2}\.\d{3}).*client_address=\(\'(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}).*file\_id=((\d+)\_(\d+)[^\|]+).*Thread\-(.*)""".r    

  val ingestRegex = """^(\d{4}\-\d{2}\-\d{2}T\d{2}\:\d{2}:\d{2}\.\d{3}).*client_address=\(\'(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}).*host=([^\s]+).*content-length=(\d+).*filename="((\d+)_(\d+)[^\"]+).*Thread\-(.*)""".r
  val ingestNoIpRegex = """^(\d{4}\-\d{2}\-\d{2}T\d{2}\:\d{2}:\d{2}\.\d{3}).*client_address=\(\'(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}).*host=([^\s]+).*content-length=(\d+).*filename="((\d+)_(\d+)[^\"]+).*Thread\-(.*)""".r

  val threadRegex = """.*Thread\-(.*)""".r
  val hostRegex = """.*Host ID\/IP: ([^\/]*).*Thread\-(.*)""".r
  val sizeRegex = """.*Size:\s(\d+).*Thread\-(.*)""".r
  val ipRegex = """.*HTTP reply sent to: \(\'([^\']+).*Thread\-(\d+)""".r

  // parsing types
  // threads are named differently to deal with duplicate thread join issues
  case class Access(date: String, ip: String, file: String, obsId: Long, obsDate: String, host: String, accessThread: String)
  case class IpThread(ip: String, ipThread: String)
  case class SizeThread(size: Long, sizeThread: String)
  case class Thread(thread: String)
  case class Ingest(date: String, ip: String, host: String, size: Long, file: String, obsId: Long, obsDate: String, ingestThread: String)
  case class Host(host: String, hostThread: String)
//    case class Transfer(time: Float, rate: Float, thread: String)

  def extractThread(line: String): Thread = {
    line match {
      case threadRegex(thread) => Thread(thread)
      case _ => Thread("")
    }
  }

  def extractAccess(line: String): Access = {
    line match {
      case accessRegex(date, ip, file, obsId, obsDate, host, thread) =>
        Access(date, ip, file, obsId.toLong, obsDate, host, thread)
      case accessNoHostRegex(date, ip, file, obsId, obsDate, thread) => 
        Access(date, ip, file, obsId.toLong, obsDate, "", thread)
      case _ => Access("", "", "", 0, "", "", "")
    }
  }

  def extractIp(line: String): IpThread = {
    line match {
      case ipRegex(ip, thread) => IpThread(ip, thread)
      case _ => IpThread("", "")
    }  
  }

  def extractSize(line: String): SizeThread = {
    line match {
      case sizeRegex(size, thread) => SizeThread(size.toLong, thread)
      case _ => SizeThread(0, "")
    }
  }

//    def extractTransfer(line: String): Transfer = {
//      line match {
//        case transferRegex(time, rate, thread) => Transfer(time.toFloat, rate.toFloat, thread)
//        case transferNoRateRegex(time, thread) => Transfer(time.toFloat, 0.0, thread)
//          case _ => Transfer(0.0, 0.0, thread)
//      }
//    }

  def extractHost(line: String): Host = {
    line match {
      case hostRegex(host, thread) => Host(host, thread)
      case _ => Host("", "")
    }
  }    

  def extractIngest(line: String): Ingest = {
    line match { 
      case ingestRegex(date, ip, host, size, file, obsId, obsDate, thread) =>
        Ingest(date, ip, host, size.toLong, file, obsId.toLong, obsDate, thread)
      case ingestNoIpRegex(date, host, size, file, obsId, obsDate, thread) =>
        Ingest(date, "", host, size.toLong, file, obsId.toLong, obsDate, thread)
      case _ => Ingest("", "", "", 0, "", 0, "", "")
    }
  }
}

import sttp.client._
import sttp.model.Uri
import ujson.Value

object UserInfoPapser {
  val sort: Option[String] = None
  val query = "http language:scala"

  def getMaxRate(user: String): Integer = {
    val maxRatedRepoTuple = getReposInfo(user).maxBy(tup => tup._2)
    maxRatedRepoTuple._2
  }

  def getReposInfo(user: String): Array[(String, Integer)] ={
    val reqString = uri"https://api.github.com/users/$user/repos"
    val json = getRequest(reqString)
    try {
      json.arr
        .filter(repo => !repo("private").bool)
        .toArray
        .map { x => (x("full_name").toString()
          .replaceAll("\"", ""),
          x("stargazers_count").toString().toInt)
        }
    }
    catch {
      case e: ujson.Value.InvalidData =>
        Array(("notFound", -1))
    }
  }

  def getRequest(req: Uri): Value ={
    val request = basicRequest.get(req)
    implicit val backend: SttpBackend[Identity, Nothing, NothingT] = HttpURLConnectionBackend()
    val response = request.send()
    val b = response.body
    b match {
      case Left(b) => ujson.read(b)
      case Right(b) => ujson.read(b)
    }
  }

  def getFollowers(user: String): Array[String] ={
    val reqString = uri"https://api.github.com/users/$user/followers"
    val resp = getRequest(reqString)
    try {
      resp.arr.map(x =>
        x("login").toString()).toArray
    }
    catch {
      case e : ujson.Value.InvalidData =>
        Array("notFound")
    }
  }
}

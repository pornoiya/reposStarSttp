import scala.io.StdIn.readLine

import UserInfoPapser._
object main {

  def main(args: Array[String]): Unit = {
    Console.print("Enter the username: ")
    var userName = readLine()
    var followers = getFollowers(userName)
    while(followers.exists(f => f.equals("notFound"))){
      Console.print("User does not exist try again: ")
      userName = readLine()
      followers = getFollowers(userName)
    }
    val maxRate = getMaxRate(userName)
    val ratedFollowersRepos = followers.flatMap(follower =>
      getReposInfo(follower.replaceAll("\"", "")))
    println("Overrated repos of followers:")
    ratedFollowersRepos
      .filter(tup => tup._2 > maxRate)
      .foreach(x => println(x._1))
  }

}

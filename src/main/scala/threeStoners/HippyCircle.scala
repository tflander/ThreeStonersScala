package threeStoners

case class HippyCircle (stoners: Seq[Stoner]) {
  
  val stonerMap = stoners.groupBy(_.stonerId)
  val weedGuys = stoners.filter(_.supply == Weed)
  val paperGuys = stoners.filter(_.supply == Paper)
  val matchesGuys = stoners.filter(_.supply == Matches)

  def getStonerById(id: String): Stoner = {
    stonerMap.get(id).get.head
  }
  
}

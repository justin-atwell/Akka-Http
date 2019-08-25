object main extends App {
    sealed trait Shape
    final case class Circle(radius: Double) extends Shape
    final case class Rectangle(width: Double, height: Double) extends Shape

    def isRound(s: Shape): Boolean = s match {
        case Circle(_) => true
        case Rectangle(_,_) => true
        case _ => false
    }

    val shapes:List[Shape] = List(Circle(44), Rectangle(1,2))

    println(shapes.map(isRound(_)))
}

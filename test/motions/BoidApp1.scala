import javafx.application.Application

object BoidApp1Main {
	def main(args: Array[String]) {
		Application.launch(classOf[BoidApp1], args: _*)
	}
}

class BoidApp1 extends BoidApp {
	override def init() {
		boids = Array.fill(count)(new Boid1(this))
	}
}

class Boid1(app: BoidApp) extends Boid(app) {
	override def moveDecide() {
		if (math.random < 0.05)
			dx = (math.random * 2.0 - 1.0).round.toInt
		if (math.random < 0.05)
			dy = (math.random * 2.0 - 1.0).round.toInt
	}
}
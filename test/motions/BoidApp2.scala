import javafx.application.Application

//アプリケーション起動オブジェクト
object BoidAppMain2 {
	def main(args: Array[String]) {
		Application.launch(classOf[BoidApp2], args: _*)
	}
}

//graphics window application class
class BoidApp2 extends BoidApp {
	val cohesionRate = 0.01 //結合パラメータ（群の中心に向かう強さ）
	val separationDis = 25 //分離パラメータ（ぶつからないための距離）
	val alignmentRate = 0.5 //整列パラメータ（群に合わせる強さ）
	val speedLimit = 8
	override def init() { //初期化処理
		boids = Array.fill(count)(new Boid2(this)) //全個体生成
	}
}

//Boid2個体クラス
class Boid2(app: BoidApp2) extends Boid(app) {
	override def moveDecide() { //移動量決定処理
		cohesion //結合（群の中心に向かう）
		separation //分離（ぶつからないように距離を取る）
		alignment //整列（群と同じ方向と速度に合わせる）
		val rate = math.sqrt(dx*dx + dy*dy) / app.speedLimit
		if (rate > 1.0) { //速度制限
			dx /= rate
			dy /= rate
		}
	}

	def cohesion() { //結合（群の中心に向かう）
		val cx = app.boids.map(_.x).sum / app.count //（全個体の座標合計値）/（個体数）＝（群の中心座標）
		val cy = app.boids.map(_.y).sum / app.count
		dx += (cx - x) * app.cohesionRate //自分と群の中心との差に結合パラメータを掛ける
		dy += (cy - y) * app.cohesionRate //差が大きいほど、移動量の修正量が大きくなり、中心に近づいていく
	}

	def separation() { //分離（ぶつからないように距離を取る）
		for (o <- app.boids if o != this) { //自分以外の全個体に対して、
			val ax = o.x - x //直線距離を計算し、
			val ay = o.y - y
			val dis = math.sqrt(ax*ax + ay*ay)
			if (dis < app.separationDis) { //一定距離以内に接近している個体があれば、
				dx -= ax
				dy -= ay
			}
		}
	}

	def alignment() { //整列（群と同じ方向と速度に合わせる）
		val ax = app.boids.map(_.dx).sum / app.count //（全個体の移動量の合計値）/（個体数）＝（群全体の平均移動量つまり方向ベクトル）
		val ay = app.boids.map(_.dy).sum / app.count
		dx += (ax - dx) * app.alignmentRate //個体の移動量を、全体の移動量の平均に近づける
		dy += (ay - dy) * app.alignmentRate
	}
}
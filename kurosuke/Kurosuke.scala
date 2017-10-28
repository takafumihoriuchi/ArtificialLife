import javafx.application.Application
import javafx.scene._
import javafx.stage._
import javafx.scene.layout._
import javafx.scene.canvas._
import javafx.scene.shape._
import javafx.scene.text._
import javafx.scene.effect._
import javafx.scene.control._
import javafx.geometry._
import javafx.scene.paint.Color
import javafx.event.EventHandler
import javafx.animation._

//アプリケーション起動オブジェクト
object KurosukeMain {
	def main(args: Array[String]) {
		Application.launch(classOf[Kurosuke], args: _*)
	}
}

//graphics window application class
class Kurosuke extends Application {
	val w = 800 //ウィンドウ内部の幅
	val h = 800 //ウィンドウ内部の高さ
	val count = 70 //個体数
	val range = 120 //個体発生位置範囲
	val pane = new Pane
	val shapes = pane.getChildren //全図形
	var boids: Array[Boid] = null //全個体
	var active = true //スレッド継続フラグ
	val cohesionRate = 0.01 //結合パラメータ（群の中心に向かう強さ）
	val separationDis = 20 //分離パラメータ（ぶつからないための距離）
	val alignmentRate = 0.3 //整列パラメータ（群に合わせる強さ）
	val speedLimit = 8

	override def init() { //初期化処理
		boids = Array.fill(count)(new Boid(this)) //全個体生成
	}

	override def start(stage: Stage) { //開始処理
		stage.setScene(new Scene(pane, w, h))
		stage.show

		new Thread() { //移動計算スレッド
			override def run() {
				while (active) {
					for (b <- boids) {
						b.moveDecide //移動量決定
						b.move //移動位置計算
					}
					Thread.sleep(15) //速度調整
				}
			}
		}.start

		new AnimationTimer { //描画タイマー処理
			override def handle(now: Long) {
				for (b <- boids) {
					b.draw //描画
				}
			}
		}.start
	}

	//終了時の処理（スレッド継続フラグオフ）
	override def stop() { active = false }
}

//Boid2個体クラス
class Boid(app: Kurosuke) {

	var x = app.w / 2 + app.range * (math.random - 0.5) //個体の初期位置を設定
	var y = app.h / 2 + app.range * (math.random - 0.5) //画面の中心から、rangeを上限下限として、それに-0.5~0.5をかけた値
	var dx, dy = 1.0
	val shape = new Rectangle(12, 12) //図形生成
	app.shapes.add(shape) //図形追加

	def moveDecide() { //移動量決定処理
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
				dx -= ax *0.04
				dy -= ay *0.04
			}
		}
	}

	def alignment() { //整列（群と同じ方向と速度に合わせる）
		val ax = app.boids.map(_.dx).sum / app.count //（全個体の移動量の合計値）/（個体数）＝（群全体の平均移動量つまり方向ベクトル）
		val ay = app.boids.map(_.dy).sum / app.count
		dx += (ax - dx) * app.alignmentRate //個体の移動量を、全体の移動量の平均に近づける
		dy += (ay - dy) * app.alignmentRate
	}

	def move() {
		x += dx
		y += dy
		//壁なら方向転換
		if (x < 0 || x >= app.w) { dx = -dx; x += dx * 2 } //図形座標を変更
		if (y < 0 || y >= app.h) { dy = -dy; y += dy * 2 }
	}

	def draw() {
		shape.setX(x-5) //-5は何を表しているのだろうか…
		shape.setY(y-5)
	}
}
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



//アプリケーションの起動
object BoidAppMain {
	def main(args: Array[String]) {
		Application.launch(classOf[BoidApp], args: _*)
	}
}



//グラフィッスクウィンドウアプリケーションクラス
class BoidApp extends Application {
	val w = 800 //ウィンドウ内部の幅
	val h = 800 //ウィンドウ内部の高さ
	val count = 30 //個体数
	val range = 120 //個体発生位置範囲
	val pane = new Pane
	val shapes = pane.getChildren //全図形
	var boids: Array[Boid] = null //全個体
	var active = true //スレッド継続フラグ

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



//Boid個体クラス
class Boid(app: BoidApp) {
	var x = app.w / 2 + app.range * (math.random - 0.5) //個体の初期位置を設定
	var y = app.h / 2 + app.range * (math.random - 0.5) //画面の中心から、rangeを上限下限として、それに-0.5~0.5をかけた値
	var dx, dy = 1.0
	val shape = new Rectangle(10, 10) //図形生成
	app.shapes.add(shape) //図形追加

	def moveDecide() {
		dx = (math.random * 2.0 - 1.0).round.toInt //ランダム移動方向
		dy = (math.random * 2.0 - 1.0).round.toInt
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
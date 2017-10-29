import scala.collection.mutable.ArrayBuffer
import javafx.application.Application
import javafx.scene._
import javafx.stage._
import javafx.scene.layout._
import javafx.event.EventHandler
import javafx.scene.input._
import javafx.animation._

//アプリケーション起動オブジェクト
object GameAppMain {
	def main(args: Array[String]) {
		Application.launch(classOf[GameApp], args: _*)
	}
}

//グラフィックスウィンドウアプリケーションクラス
class GameApp extends Application {
	val uw, uh = 40 //ユニット幅と高さ
	var w, h: Int = 0 //ゲーム画面の幅と高さ
	val pane = new Pane
	val shapes = pane.getChildren //図形の集まり
	var map: Array[Array[Int]] = null //マップ情報
	var elems: ArrayBuffer[GameElem] = null //キャラクタ配列
	var active = true //移動スレッド稼働フラグ
	var keyCode: KeyCode = null //キーコード

	override def init() {
		map = GameMap.makeMap
		elems = GameElem.makeElems(map, this, makeElem)
		h = map.length * uh
		w = map(0).length * uw
	}

	def makeElem(typ: Int) = {
		typ match {
			case GameElem.WALL => new Wall(this)
			case GameElem.PLAYER => new Player(this)
			case GameElem.ALIEN => new Alien(this)
			case _ => null
		}
	}

	override def start(stage: Stage) { //処理開始
		val scene = new Scene(pane, w, h)
		stage.setScene(scene)
		stage.show
		//キー押下（おうか）時の処理
		scene.setOnKeyPressed(new EventHandler[KeyEvent] {
			def handle(e: KeyEvent) {keyCode = e.getCode}
		})
		//キー解放時の処理
		scene.setOnKeyReleased(new EventHandler[KeyEvent] {
			def handle(e: KeyEvent) {keyCode = KeyCode.ESCAPE}
		})
		new Thread() { //移動計算スレッド
			override def run() {
				while (active) { //稼働フラグが真の間ループ
					//移動
					for (e <- elems if e.typ >= GameElem.PLAYER) e.move
					Thread.sleep(8) //速度調整
				}
			}
		}.start
		new AnimationTimer { //描画タイマー処理
			override def handle(now: Long) {
				//キャラクタの描画
				for (e <- elems if !(e.typ == GameElem.ROAD || e.typ  == GameElem.WALL)) e.draw
			}
		}.start
	}

	override def stop() { active = false } //終了時の処理

	def isWall(x: Int, y: Int) = { //壁にぶつかるか
		val r1 = y / uh
		val c1 = x / uw
		val r2 = (y + uh - 1) / uh
		val c2 = (x + uw - 1) / uw
		map(r1)(c1) == GameElem.WALL || map(r1)(c2) == GameElem.WALL || //c2? ||?
		map(r2)(c2) == GameElem.WALL || map(r2)(c2) == GameElem.WALL
	}

	//衝突検出
	def getCollision(me: GameElem, x: Int, y: Int): Int = {
		for (other <- elems if other.typ > 1 && other != me) {
			if (math.abs(other.x - x) < uw && math.abs(other.y - y) < uh)
				return other.typ
		}
		-1
	}
}
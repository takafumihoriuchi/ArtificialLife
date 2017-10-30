import javafx.application.Application
import javafx.scene.shape._
import javafx.scene.paint.Color
import scala.collection._
import scala.collection.mutable.ArrayBuffer

//アプリケーション起動オブジェクト
object GameAppMain3 {
	def main(args: Array[String]) {
		Application.launch(classOf[GameApp3], args: _*)
	}
}

//グラフィックスウィンドウアプリケーションクラス
class GameApp3 extends GameApp {
	var astarAlien: AStarAlien = null

	override def init() {
		super.init
		astarAlien.init(this)
	}

	override def makeElem(typ: Int) = { //キャラクタ生成
		typ match {
			case GameElem.PLAYER => new Player(this)
			case GameElem.ALIEN => {
				val a = //新たな敵クラス
					if (astarAlien == null) { //追跡NPCを一つにするため
						astarAlien = new Alien3(this) //このNPCを追跡者にする
						astarAlien.s.setStroke(Color.BLACK) //枠線を強調
						astarAlien
					} else {
						new Alien(this) //他は普通のNPC
					}
				a
			}
			case _ => GameElem.makeElem(typ, this) //他は同じ生成法
		}
	}
}

//Routeキャラクタクラス
class Route(app: GameApp) extends GameElem(app) {
	var pos = new ArrayBuffer[java.lang.Double](0)
	val p = new Polyline() //Polylineでルートを表現する
	p.setStrokeWidth(2)
	setShape(p)
	typ = -2

	override def draw() {
		if (pos != null) { //ルートが変更されたならPolylineを再セット
			p.getPoints.clear //Polylineをクリアして、座標を再設定する
			p.getPoints.addAll(JavaConverters.asJavaCollection(pos.toIterable))
			pos = null
		}
	}
}

trait AStarAlien extends Alien { //Alienに追加するA*機能
	var astar: AStar = null //A*エンジン
	var targetPlayer: Player = null //追跡対象

	def init(app: GameApp) {
		astar = new AStar(app)
		astar.init(GameMap.r, GameMap.c) //A*エンジンの作成（行、列）
		app.elems.collect{ case p: Player => targetPlayer = p } //追跡対象設定
	}

	def getDir(nextR: Int, nextC: Int) = { //行と列から方向を得る
		val dy = nextR - r
		val dx = nextC - c
		dirOffset.indexWhere(a => a._1 == dx && a._2 == dy)
	}

	def nextMove(astar: AStar) { //移動先候補を選択（AStar）
		if (reached) { //別ユニットに移動するとき再探索
			nextDir = -1
			val unit = astar.Search(r, c, targetPlayer) //最適ルートを探索
			if (unit != null) {
				nextDir = getDir(unit.r, unit.c)
			}
			if (nextDir == -1) {
				nextDir = if (math.random < 0.005) (dir + (1 + math.random * 3).toInt) % 4 else dir
			}
		} else {
			nextDir = dir
		}
	}
}

class Alien3(app: GameApp3) extends Alien(app) with AStarAlien {
	override def nextMove() {
		nextMove(astar)
	}
}
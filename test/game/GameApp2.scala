//package ex07

//import ex06._
import javafx.application.Application

//アプリケーション起動オブジェクト
object GameAppMain2 {
	def main(args: Array[String]) {
		Application.launch(classOf[GameApp2], args: _*)
	}
}

//グラフィックスウィンドウアプリケーション
class GameApp2 extends GameApp {
	var bread = new Breadcrumbs(15, this) //パンくず作成（長さ）

	override def init() {
		super.init
		bread.init(GameMap.r, GameMap.c) //パンくずマップ作成
	}

	override def makeElem(typ: Int) = { //キャラクタ生成
		typ match {
			case GameElem.PLAYER => new Player2(this) //新プレイヤークラス
			case GameElem.ALIEN => new Alien2(this) //新たな敵クラス
			case _ => GameElem.makeElem(typ, this) //他は同じ生成方
		}
	}
}

class Player2(app: GameApp2) extends Player(app) {
	override def move() {
		super.move
		if (reached) app.bread.drop(r,c) //ユニットを移動したらパンくずを落とす
	}
}

//Alienに追加するパンくず機能
trait BreadAlien extends Alien {
	//移動先候補を選択（パンくず、ランダム方向転換）
	def nextMove(bread: Breadcrumbs) {
		if (reached) {
			var tryDir = bread.trail(r,c,dir)
			if (tryDir != -1) {
				nextDir = tryDir
			} else {
				nextDir = if (math.random < 0.005) (dir + (1 + math.random * 3).toInt) % 4 else dir
			}
		} else {
			nextDir = dir
		}
	}
}

//パンくず機能を追加したAlien2クラス
class Alien2(app: GameApp2) extends Alien(app) with BreadAlien {
	override def nextMove() {
		nextMove(app.bread)
	}
}
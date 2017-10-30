/*
実装が不完全、有限状態の推移が機能していない。要修正。
*/

import javafx.application.Application
import javafx.scene.text._

//アプリケーション起動オブジェクト
object GameAppMain4 {
	def main(args: Array[String]) {
		Application.launch(classOf[GameApp], args: _*)
	}
}

//グラフィックスウィンドウアプリケーションクラス
class GameApp4 extends GameApp {
	var bread = new Breadcrumbs(15, this) //パンくず作成（長さ）

	override def init() {
		super.init
		bread.init(GameMap.r, GameMap.c) //パンくずマップ作成（行、列）
		elems.collect{ case p:Alien4 => p.init(this) }
	}

	override def makeElem(typ: Int) = { //キャラクタ生成
		typ match {
			case GameElem.PLAYER => new Player4(this)
			case GameElem.ALIEN => new Alien4(this) //新たな敵クラス
			case _ => GameElem.makeElem(typ, this) //他は同じ生成法
		}
	}
}

class Player4(app: GameApp4) extends Player(app) {
	var attackWait = 0

	override def move() {
		if (attackWait > 0) { //攻撃ダメージ
			attackWait -= 1
			return
		}
		super.move
		if (reached) app.bread.drop(r,c) //ユニットを移動したらパンくずを落とす
	}
}

class Alien4(app: GameApp4) extends Alien(app) with BreadAlien with AStarAlien {
	val fsm = new FSM(this, app) //有限状態マシン
	var touchedPlayer: Player4 = null //接触プレイヤー
	var attackWait = 0
	val text = new Text("")
	text.setFont(Font.font ("MeiryoUI", 11))
	app.shapes.add(text)

	override def draw() {
		super.draw
		text.setX(x + 3)
		text.setY(y + app.uh*0.5)
		text.setText(fsm.stateLabel(fsm.state) + "\n  " + fsm.energy.toInt)
	}

	override def nextMove() {
		fsm.action
		fsm.state match {
			case fsm.SEARCH => nextMove(app.bread) //パンくず拾い
			case fsm.CHASE => nextMove(astar) //A*アルゴリズム
			case fsm.ATTACK => 	attackWait = 50 //攻撃
								touchedPlayer.attackWait = 100
			case fsm.SLEEP => //休止中は処理しない
			case fsm.MOVE | fsm.ESCAPE => super[Alien].nextMove //ランダム
		}
	}

	override def move() {
		if (attackWait > 0) { //攻撃中
			attackWait -= 1
			return
		}
		nextMove //次の移動先候補を選ぶ
		//astar.routeLine.visible = fsm.state == fsm.CHASE
		//プレイヤーとの衝突判定
		if (fsm.state == fsm.ESCAPE) { //逃避中は接触無視
			touchedPlayer = null
		} else {
			touchedPlayer = app.elems.collect{ case p: Player4 => p }.find( p =>
				math.abs(p.x - (x + dirOffset(nextDir)._1)) < app.uw &&
				math.abs(p.y - (y + dirOffset(nextDir)._2)) < app.uh
			).orNull
		}
		fsm.touched = touchedPlayer != null
		//攻撃中か休止中は移動しない
		if (fsm.touched || fsm.state == fsm.SLEEP) return
		moveExec //移動実行
	}
}
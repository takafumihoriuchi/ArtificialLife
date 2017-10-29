import javafx.scene.shape._
import javafx.scene.paint.Color

//ノンプレイヤークラス
class Alien(app: GameApp) extends GameElem(app) {
	var nextDir = 0
	val dirOffset = Array((1,0),(0,1),(-1,0),(0,-1))
	val tryPlan = Array(1,3,2) //右、左、後
	val s = new Rectangle(app.uw, app.uh)
	s.setArcWidth(15)
	s.setArcHeight(15)
	s.setScaleX(0.9)
	s.setScaleY(0.9)
	s.setStroke(Color.FIREBRICK)
	s.setFill(Color.INDIANRED)
	setShape(s)

	def nextMove() { //移動先候補を選択（ランダム方向転換）
		nextDir = if (math.random < 0.005)
			(dir + 1 + (math.random * 2).round.toInt) % 4 else dir
	}

	def moveExec() { //可能であれば移動実行
		var x1 = x + dirOffset(nextDir)._1
		var y1 = y + dirOffset(nextDir)._2
		if (app.isWall(x1,y1) || app.getCollision(this,x1,y1) != -1) {
			//後ろに転換する確率は下げる。迷路状hのマップで、行ったり来たりするのを避けるため。
			nextDir = (nextDir + tryPlan((math.random * 2.1).toInt)) % 4
			x1 = x + dirOffset(nextDir)._1
			y1 = y + dirOffset(nextDir)._2
		}
		if (!app.isWall(x1,y1) && app.getCollision(this,x1,y1) == -1) {
			dir = nextDir //壁でなく他のキャラクタに衝突しなければ進む
			setPos(x1,y1)
		} 
	}

	override def move() {
		nextMove //次の移動先候補を選ぶ
		moveExec //移動実行
	}
}
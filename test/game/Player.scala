import javafx.scene.shape._
import javafx.scene.input._
import javafx.scene.paint.Color

//プレイヤークラス
class Player(app: GameApp) extends GameElems(app) {
	val s = new Rectangle(app.uw, app.uh)
	s.setArcWidth(15)
	s.setArcHeight(15)
	s.setScaleX(0.9)
	s.setScaleY(0.9)
	s.setStroke(Color.ROYALBLUE)
	s.setFill(Color.CORNFLOWERBLUE)
	setShape(s)
	var lastDx, lastDy = -1

	override def move() {
		var dx, dy = 0
		app.keyCode match { //押されているキーに対する処理
			case KeyCode.LEFT => dx = -1
			case KeyCode.RIGHT => dx = 1
			case KeyCode.UP => dy = -1
			case KeyCode.DOWN => dy = 1
			case KeyCode.ESCAPE => //キーが離された後の状態
				//1マスの中間位置ならば、uwとuhはユニットの高さと幅
				if (x % app.uw != 0 || y % app.uh != 0) {
					dx = lastDx //移動を継続
					dy = lastDy
				} else {
					app.keyCode = null
					return
				}
			case _ => return
		}
		//壁でなく他のキャラクタに衝突しなければ進む
		val x1 = x + dx
		val y1 = y + dy
		if (!app.isWall(x1, y1) && app.getCollision(this, x1, y1) == -1) { //app.getCollisionメソッドは移動先で他のキャラクタと衝突するかを判定する機能
			setPos(x1, y1)
			lastDx = dx
			lastDy = dy
		}
	}
}
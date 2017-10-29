//package ex07

//import ex06._
import javafx.scene.shape._
import javafx.scene.paint.Color

case class Pos(val r: Int, val c: Int) {}

//パンくず拾いクラス
class Breadcrumbs(len: Int, app: GameApp) {
	var map: Array[Array[Int]] = null //パンくずが置かれたマップ
	var pList: List[Pos] = Nil //パンくずの位置リスト
	val dirOffset = Array((1,0),(0,1),(-1,0),(0,-1))
	val tryPlan = Array(0,1,3,2) //前、右、左、後ろの順で調べる
	val bread = Array.fill[Bread](len)(new Bread(app))

	def init(r: Int, c: Int) {
		map = Array.ofDim[Int](r,c)
		bread.foreach(app.elems.append(_)) //描画対象キャラクタに追加
	}

	def drop(r: Int, c: Int) { //パンくずを落とす
		if (pList != Nil && pList.length >= len) {
			val pos = pList.head //古い位置を取り出して
			map(pos.r)(pos.c) = 0 //マップから消す
			pList = pList.tail //リストから除く
		}
		val p = Pos(r,c)
		//すでにあれば、まずは消す
		if (map(r)(c) != 0) 
			pList = pList.diff(List(p))
		pList = pList :+ p //新たな位置をリストに追加
		map(r)(c) = 1
		plotBread
	}

	def plotBread() {
		var i = 0
		for (p <- pList) {
			val x = ((p.c + 0.5) * app.uw - 2).toInt
			val y = ((p.r + 0.5) * app.uh - 2).toInt
			bread(i).setPos(x,y)
			i += 1
		}
	}

	def trail(r: Int, c: Int, dir: Int): Int = { //パンくずの方向を探す
		for (i <- 0 to 3) {
			val tryDir = (dir + tryPlan(i)) % 4
			val d = dirOffset(tryDir)
			if (map(r+d._2)(c+d._1) == 1) return tryDir //見つかった
		}
		return -1 //見つからなかった
	}
}

//パンくずキャラクタクラス
class Bread(app: GameApp) extends GameElem(app) {
	val s = new Rectangle(4,4)
	s.setFill(Color.CORNFLOWERBLUE)
	setShape(s)
	typ = -1
	setPos(-100, -100) //見えない場所に置いておく
}
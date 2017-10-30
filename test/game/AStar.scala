import scala.collection.mutable.ArrayBuffer

//A*用ユニット情報クラス
class AStarUnit(val r: Int, val c: Int) {
	var open = 0 //オープン状態
	var movement = 0 //スタートからの移動量
	var distance = 0 //ゴールまでの距離
	var totalCost = 0 //コスト
	var from: AStarUnit = null

	def calcCost(targetR: Int, targetC: Int) { //コスト計算
		movement = math.abs(r - from.r) + math.abs(c - from.c) + from.movement
		distance = math.abs(r - targetR) + math.abs(c - targetC)
		totalCost = movement + distance
	}
}

//A*アルゴリズムエンジンクラス
class AStar(val app: GameApp) {
	var mapR, mapC = 0 //マップサイズ
	var aMap: Array[Array[AStarUnit]] = null
	val AStarDir = Array((-1,0),(1,0),(0,-1),(0,1))
	var openList: List[AStarUnit] = Nil //オープンリスト
	var routeLine = new Route(app) //ルートのPolyline用キャラクタ

	def init(r: Int, c: Int) {
		app.elems.append(routeLine)
		mapR = r
		mapC = c
		aMap = Array.ofDim[AStarUnit](mapR, mapC)
		for (i <- 0 until mapR; j <- 0 until mapC)
			aMap(i)(j) = new AStarUnit(i, j)
	}

	def Search(r: Int, c: Int, target: Player): AStarUnit = {
		//Polyline用のDouble型
		val a = new ArrayBuffer[java.lang.Double]()
		if (!SearchRoute(r, c, target.r, target.c)) { //最適解探索
			routeLine.pos = a
			return null
		}
		var route: List[AStarUnit] = Nil
		var unit = aMap(target.r)(target.c) //目的地点
		while (!(unit.r == r && unit.c == c)) { //開始地点まで遡る
			route = unit :: route //最適ルートに追加
			unit = unit.from
		}
		for (unit <- route) {
			a += unit.c * app.uw + app.uw / 2 //x座標
			a += unit.r * app.uh + app.uh / 2 //y座標
		}
		routeLine.pos = a.slice(0, a.length-1) //Polyline用座標に設定
		route.head //最適ルートの一歩目を返す
	}

	def remove(list: List[AStarUnit], target: AStarUnit): List[AStarUnit] = {
		list match {
			case Nil => Nil
			case a::aa => if (a == target) aa else a :: remove(aa, target)
		}
	}

	def movable(r: Int, c: Int) = { //移動可能か調べる
		app.map(r)(c) != GameElem.WALL && !app.elems.exists(
			e => e.typ == GameElem.ALIEN && e.r == r && e.c == c)
	}

	def SearchRoute(startR: Int, startC: Int, targetR: Int, targetC: Int): Boolean = {
		aMap.foreach(_.foreach(_.open = 0)) //未オープンにしておく
		var unit = aMap(startR)(startC) //開始ユニット
		unit.open = 1 //オープンにする
		openList = List(unit) //オープンリストに入れる
		while (openList != Nil) {
			val unit = openList.minBy(_.totalCost) //最小コストのものを選択
			if (unit.r == targetR && unit.c == targetC) return true
			unit.open = -1 //クローズにする
			openList = remove(openList, unit) //unitをオープンリストから削除
			for (dir <- AStarDir) { //隣接ユニットを調べる
				val r = unit.r + dir._1
				val c = unit.c + dir._2
				if (r >= 0 && c >= 0 && r < mapR && c < mapC) {
					val around = aMap(r)(c)
					if (around.open == 0 && movable(r,c)) {
						around.open = 1 //オープンにする
						openList = around :: openList //オープンリストに追加
						around.from = unit //どこから来たか記憶
						around.calcCost(targetR, targetC) //コスト計算
					}
				}
			}
		}
		return false
	}
}
//package ex06

object GameMap {
	var r, c = 0 //行列サイズ
	val mark = "_#o@" //マーク（道、壁、プレイヤー、NPC）
	val mapData =
		"""
		############
		#@____##__@#
		#_#_#____#_#
		#_#_##_###_#
		#____#_#___#
		##_____#_###
		#___#______#
		#_#_###_##_#
		#_#______#_#
		#_##_#_#_#_#
		#@___#____o#
		############
		"""
	def makeMap() = {
		val a = mapData.split("\\n").map(_.trim).filter(_ > "").map(_.split("").map(mark.indexOf(_)))
		r = a.length
		c = a(0).length
		a
	}
}
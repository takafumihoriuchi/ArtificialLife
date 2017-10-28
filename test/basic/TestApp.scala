//package ex.basic


class Syouhin(val code: String, var price: Int) {

	def calc(n: Int) = {
		price * n
	}

	def disp(n: Int) {
		println(code + "\t" + calc(n) + "円")
	}
}


class SaleSyouhin(code: String, price: Int, var rate: Double) extends Syouhin(code, price) {
	
	override def calc(n: Int) = {
		(price * n * rate).toInt
	}

}


object TestApp extends App {

	val s1 = new Syouhin("CPU", 10000)
	val s2 = new SaleSyouhin("HDD", 10000, 0.7)
	s1.disp(2)
	s2.disp(2)

}
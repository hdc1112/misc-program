package dachuan

class B extends A {
    override val a: String = "ccc"
}

object B {
    def main(args: Array[String]) {
        val obj = new A
        println(obj.a)
    }
}

// vim: set ts=4 sw=4 et:

class BoundedManifest[T <: Any : Manifest](value: T) {
    val m = manifest[T]
    m.erasure.toString match {
        case "class java.lang.String" => println("String")
        case "double" | "int"  => println("Numeric value.")
        case x => println("WTF is a '%s'?".format(x))
    }
}

class ImplicitManifest[T <: Any](value: T)(implicit m: Manifest[T]) {
    m.erasure.toString match {
        case "class java.lang.String" => println("String")
        case "double" | "int" => println("Numeric value.")
        case x => println("WTF is a '%s'?".format(x))
    }
}

object Test extends App {
    override def main(args: Array[String]) {
        new BoundedManifest("Foo Bar!")
        // String 
        new BoundedManifest(5)
        // Numeric value.
        new BoundedManifest(5.2)
        // Numeric value.
        new BoundedManifest(BigDecimal("8.62234525"))
        // WTF is a 'class scala.math.BigDecimal'?
        new ImplicitManifest("Foo Bar!")
        // String 
        new ImplicitManifest(5)
        // Numeric value.
        new ImplicitManifest(5.2)
        // Numeric value.
        new ImplicitManifest(BigDecimal("8.62234525"))
        // WTF is a 'class scala.math.BigDecimal'?
    }
}

// vim: set ts=4 sw=4 et:

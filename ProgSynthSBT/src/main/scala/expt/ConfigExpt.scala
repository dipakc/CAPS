package expt

object ConfigExpt {

    abstract class A {

    }

    class X extends A {
    	val config: Config = new Config(XConfig, List(10, 11))
    }

    class Y extends A {
    	val config: Config = new Config(XConfig, List(10, 11))
    }

    class Config(val parent: Config, var params: List[Int]) {
        def isInList(elem: Int): Boolean = {
            if (params contains elem)
                true
            else if(parent != null)
                parent.isInList(elem)
            else
                false
        }
    }

    object AConfig extends Config(null, List(1, 2, 3))
    object XConfig extends Config(AConfig, List(2, 3, 5))
    object YConfig extends Config(AConfig, List(4, 5))

    def main(args: Array[String]) {
    	val x1 = new X()
    	println(x1.config.isInList(10),
    	x1.config.isInList(2),
    	x1.config.isInList(1),
    	x1.config.isInList(35))
    }

}
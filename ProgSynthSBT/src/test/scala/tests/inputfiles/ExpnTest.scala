package tests.inputfiles
import progsynth.types.Var

class C(val a: Int)

object ExpnTest {
	val y = 5
	def sum(x: Int, y: Int) = x + y

	def testMethod() = {
		val z = 5
		val z2 = sum _
		val z3 = new C(5)
		var t = 2

		val x1 = 5
		//Literal(Constant(5))
		val x2 = null
		//Literal(Constant(null))
		val x3 = z
		//Ident("z")
		// sym=value z, sym.owner=method testMethod, sym.tpe=Int,
		// tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		val x4 = y
		//Select( // sym=value y, sym.owner=object ExpnTest, sym.tpe==> Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//  This("ExpnTest"), // sym=object ExpnTest, sym.owner=package inputfiles, sym.tpe=object tests.inputfiles.ExpnTest, tpe=tests.inputfiles.ExpnTest.type, tpe.sym=object ExpnTest, tpe.sym.owner=package inputfiles, tpe.decls=List(constructor ExpnTest: ()object tests.inputfiles.ExpnTest, , value y: => Int, , value y: Int, , method sum: (x: Int, y: Int)Int, , method testMethod: ()Unit, )
		//  "y")
		val x5 = ExpnTest.this.y
		//Select( // sym=value y, sym.owner=object ExpnTest, sym.tpe==> Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//  This("ExpnTest"), // sym=object ExpnTest, sym.owner=package inputfiles, sym.tpe=object tests.inputfiles.ExpnTest, tpe=tests.inputfiles.ExpnTest.type, tpe.sym=object ExpnTest, tpe.sym.owner=package inputfiles, tpe.decls=List(constructor ExpnTest: ()object tests.inputfiles.ExpnTest, , value y: => Int, , value y: Int, , method sum: (x: Int, y: Int)Int, , method testMethod: ()Unit, )
		//  "y")
		val x6 = this.y
		//Select( // sym=value y, sym.owner=object ExpnTest, sym.tpe==> Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//  This(""), // sym=object ExpnTest, sym.owner=package inputfiles, sym.tpe=object tests.inputfiles.ExpnTest, tpe=tests.inputfiles.ExpnTest.type, tpe.sym=object ExpnTest, tpe.sym.owner=package inputfiles, tpe.decls=List(constructor ExpnTest: ()object tests.inputfiles.ExpnTest, , value y: => Int, , value y: Int, , method sum: (x: Int, y: Int)Int, , method testMethod: ()Unit, )
		//  "y")
		val x7 = this
		//This("") // sym=object ExpnTest, sym.owner=package inputfiles, sym.tpe=object tests.inputfiles.ExpnTest, tpe=tests.inputfiles.ExpnTest.type, tpe.sym=object ExpnTest, tpe.sym.owner=package inputfiles, tpe.decls=List(constructor ExpnTest: ()object tests.inputfiles.ExpnTest, , value y: => Int, , value y: Int, , method sum: (x: Int, y: Int)Int, , method testMethod: ()Unit, )
		val x8 = ExpnTest.this
		//This("ExpnTest") // sym=object ExpnTest, sym.owner=package inputfiles, sym.tpe=object tests.inputfiles.ExpnTest, tpe=tests.inputfiles.ExpnTest.type, tpe.sym=object ExpnTest, tpe.sym.owner=package inputfiles, tpe.decls=List(constructor ExpnTest: ()object tests.inputfiles.ExpnTest, , value y: => Int, , value y: Int, , method sum: (x: Int, y: Int)Int, , method testMethod: ()Unit, )
		val x9 = 5 * z
		//Apply( // sym=method *, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//  Select( // sym=method *, sym.owner=class Int, sym.tpe=(x: Int)Int, tpe=(x: Int)Int, tpe.sym=<none>
		//    Literal(Constant(5)),
		//    "$times"),
		//  List( // 1 arguments(s)
		//    Ident("z") // sym=value z, sym.owner=method testMethod, sym.tpe=Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//  )
		//)
		val x10 = {p: Int => p + 1} //anonymous function
		//List(Function(
		//    ValDef( // sym=value p, sym.owner=value $anonfun, sym.tpe=Int, tpe=<notype>, tpe.sym=<none>
		//      ram>, // flags=<param>, annots=List()
		//      "p",
		//      TypeTree(), // sym=class Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala,
		//      EmptyTree
		//    )
		//)
		//  Apply( // sym=method +, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//    Select( // sym=method +, sym.owner=class Int, sym.tpe=(x: Int)Int, tpe=(x: Int)Int, tpe.sym=<none>
		//      Ident("p"), // sym=value p, sym.owner=value $anonfun, sym.tpe=Int, tpe=p.type, tpe.sym=value p, tpe.sym.owner=value $anonfun, tpe.decls=List(method toByte: => Byte, , method toShort: => Short, , method toChar: => Char, , method toInt: => Int, , method toLong: => Long, , method toFloat: => Float, , method toDouble: => Double, , method unary_+: => Int, , method unary_-: => Int, , method unary_~: => Int, , method +: (x: String)String, , method <<: (x: <?>)Int, , method <<: (x: <?>)Int, , method >>>: (x: <?>)Int, , method >>>: (x: <?>)Int, , method >>: (x: <?>)Int, , method >>: (x: <?>)Int, , method ==: (x: Byte)Boolean, , method ==: (x: Short)Boolean, , method ==: (x: Char)Boolean, , method ==: (x: Int)Boolean, , method ==: (x: Long)Boolean, , method ==: (x: Float)Boolean, , method ==: (x: Double)Boolean, , method !=: (x: Byte)Boolean, , method !=: (x: Short)Boolean, , method !=: (x: Char)Boolean, , method !=: (x: Int)Boolean, , method !=: (x: Long)Boolean, , method !=: (x: Float)Boolean, , method !=: (x: Double)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method >: (x: Byte)Boolean, , method >: (x: Short)Boolean, , method >: (x: Char)Boolean, , method >: (x: Int)Boolean, , method >: (x: Long)Boolean, , method >: (x: Float)Boolean, , method >: (x: Double)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method |: (x: <?>)Int, , method |: (x: <?>)Int, , method |: (x: <?>)Int, , method |: (x: <?>)Int, , method |: (x: <?>)Long, , method &: (x: <?>)Int, , method &: (x: <?>)Int, , method &: (x: <?>)Int, , method &: (x: <?>)Int, , method &: (x: <?>)Long, , method ^: (x: <?>)Int, , method ^: (x: <?>)Int, , method ^: (x: <?>)Int, , method ^: (x: <?>)Int, , method ^: (x: <?>)Long, , method +: (x: Byte)Int, , method +: (x: Short)Int, , method +: (x: Char)Int, , method +: (x: Int)Int, , method +: (x: Long)Long, , method +: (x: Float)Float, , method +: (x: Double)Double, , method -: (x: <?>)Int, , method -: (x: <?>)Int, , method -: (x: <?>)Int, , method -: (x: <?>)Int, , method -: (x: <?>)Long, , method -: (x: <?>)Float, , method -: (x: <?>)Double, , method *: (x: Byte)Int, , method *: (x: Short)Int, , method *: (x: Char)Int, , method *: (x: Int)Int, , method *: (x: Long)Long, , method *: (x: Float)Float, , method *: (x: Double)Double, , method /: (x: <?>)Int, , method /: (x: <?>)Int, , method /: (x: <?>)Int, , method /: (x: <?>)Int, , method /: (x: <?>)Long, , method /: (x: <?>)Float, , method /: (x: <?>)Double, , method %: (x: Byte)Int, , method %: (x: Short)Int, , method %: (x: Char)Int, , method %: (x: Int)Int, , method %: (x: Long)Long, , method %: (x: Float)Float, , method %: (x: Double)Double, , method getClass: ()Class[Int], ),
		//      "$plus"),
		//    List( // 1 arguments(s)
		//      Literal(Constant(1))
		//    )
		//  )
		//)

		val x11 = sum(2, z) //function application
		//Apply( // sym=method sum, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//  Select( // sym=method sum, sym.owner=object ExpnTest, sym.tpe=(x: Int, y: Int)Int, tpe=(x: Int, y: Int)Int, tpe.sym=<none>
		//    This("ExpnTest"), // sym=object ExpnTest, sym.owner=package inputfiles, sym.tpe=object tests.inputfiles.ExpnTest, tpe=tests.inputfiles.ExpnTest.type, tpe.sym=object ExpnTest, tpe.sym.owner=package inputfiles, tpe.decls=List(constructor ExpnTest: ()object tests.inputfiles.ExpnTest, , value y: => Int, , value y: Int, , method sum: (x: Int, y: Int)Int, , method testMethod: ()Unit, )
		//    "sum"),
		//  List( // 2 arguments(s)
		//    Literal(Constant(2)),
		//    Ident("z") // sym=value z, sym.owner=method testMethod, sym.tpe=Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//  )
		//)
		val x12 = sum _ //method value
		/*
		List(Block( // sym=null, tpe=(Int, Int) => Int, tpe.sym=trait Function2, tpe.sym.owner=package scala
		  List(), // no statement
		  Function(
		      ValDef( // sym=value x, sym.owner=value $anonfun, sym.tpe=Int, tpe=<notype>, tpe.sym=<none>
		        ram> <synthetic>, // flags=<param> <synthetic>, annots=List()
		        "x",
		        TypeTree(), // sym=class Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala,
		        EmptyTree
		      )
		      ValDef( // sym=value y, sym.owner=value $anonfun, sym.tpe=Int, tpe=<notype>, tpe.sym=<none>
		        ram> <synthetic>, // flags=<param> <synthetic>, annots=List()
		        "y",
		        TypeTree(), // sym=class Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala,
		        EmptyTree
		      )
		  )
		    Apply( // sym=method sum, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		      Select( // sym=method sum, sym.owner=object ExpnTest, sym.tpe=(x: Int, y: Int)Int, tpe=(x: Int, y: Int)Int, tpe.sym=<none>
		        This("ExpnTest"), // sym=object ExpnTest, sym.owner=package inputfiles, sym.tpe=object tests.inputfiles.ExpnTest, tpe=tests.inputfiles.ExpnTest.type, tpe.sym=object ExpnTest, tpe.sym.owner=package inputfiles, tpe.decls=List(constructor ExpnTest: ()object tests.inputfiles.ExpnTest, , value y: => Int, , value y: Int, , method sum: (x: Int, y: Int)Int, , method testMethod: ()Unit, )
		        "sum"),
		      List( // 2 arguments(s)
		        Ident("x"), // sym=value x, sym.owner=value $anonfun, sym.tpe=Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala,
		        Ident("y") // sym=value y, sym.owner=value $anonfun, sym.tpe=Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		      )
		    )
		  )
		)
		*/

		val x13 = -z
		//Select( // sym=method unary_-, sym.owner=class Int, sym.tpe==> Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//  Ident("z"), // sym=value z, sym.owner=method testMethod, sym.tpe=Int, tpe=z.type, tpe.sym=value z, tpe.sym.owner=method testMethod, tpe.decls=List(method toByte: => Byte, , method toShort: => Short, , method toChar: => Char, , method toInt: => Int, , method toLong: => Long, , method toFloat: => Float, , method toDouble: => Double, , method unary_+: => Int, , method unary_-: => Int, , method unary_~: => Int, , method +: (x: String)String, , method <<: (x: <?>)Int, , method <<: (x: <?>)Int, , method >>>: (x: <?>)Int, , method >>>: (x: <?>)Int, , method >>: (x: <?>)Int, , method >>: (x: <?>)Int, , method ==: (x: Byte)Boolean, , method ==: (x: Short)Boolean, , method ==: (x: Char)Boolean, , method ==: (x: Int)Boolean, , method ==: (x: Long)Boolean, , method ==: (x: Float)Boolean, , method ==: (x: Double)Boolean, , method !=: (x: Byte)Boolean, , method !=: (x: Short)Boolean, , method !=: (x: Char)Boolean, , method !=: (x: Int)Boolean, , method !=: (x: Long)Boolean, , method !=: (x: Float)Boolean, , method !=: (x: Double)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method >: (x: Byte)Boolean, , method >: (x: Short)Boolean, , method >: (x: Char)Boolean, , method >: (x: Int)Boolean, , method >: (x: Long)Boolean, , method >: (x: Float)Boolean, , method >: (x: Double)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method |: (x: <?>)Int, , method |: (x: <?>)Int, , method |: (x: <?>)Int, , method |: (x: <?>)Int, , method |: (x: <?>)Long, , method &: (x: <?>)Int, , method &: (x: <?>)Int, , method &: (x: <?>)Int, , method &: (x: <?>)Int, , method &: (x: <?>)Long, , method ^: (x: <?>)Int, , method ^: (x: <?>)Int, , method ^: (x: <?>)Int, , method ^: (x: <?>)Int, , method ^: (x: <?>)Long, , method +: (x: Byte)Int, , method +: (x: Short)Int, , method +: (x: Char)Int, , method +: (x: Int)Int, , method +: (x: Long)Long, , method +: (x: Float)Float, , method +: (x: Double)Double, , method -: (x: <?>)Int, , method -: (x: <?>)Int, , method -: (x: <?>)Int, , method -: (x: <?>)Int, , method -: (x: <?>)Long, , method -: (x: <?>)Float, , method -: (x: <?>)Double, , method *: (x: Byte)Int, , method *: (x: Short)Int, , method *: (x: Char)Int, , method *: (x: Int)Int, , method *: (x: Long)Long, , method *: (x: Float)Float, , method *: (x: Double)Double, , method /: (x: <?>)Int, , method /: (x: <?>)Int, , method /: (x: <?>)Int, , method /: (x: <?>)Int, , method /: (x: <?>)Long, , method /: (x: <?>)Float, , method /: (x: <?>)Double, , method %: (x: Byte)Int, , method %: (x: Short)Int, , method %: (x: Char)Int, , method %: (x: Int)Int, , method %: (x: Long)Long, , method %: (x: Float)Float, , method %: (x: Double)Double, , method getClass: ()Class[Int], ),
		//  "unary_$minus")
		val x14 = z2(4,5) //method val application
		//Apply( // sym=method apply, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//  Select( // sym=method apply, sym.owner=trait Function2, sym.tpe=(v1: T1, v2: T2)R, tpe=(v1: Int, v2: Int)Int, tpe.sym=<none>
		//    Ident("z2"), // sym=value z2, sym.owner=method testMethod, sym.tpe=(Int, Int) => Int, tpe=z2.type, tpe.sym=value z2, tpe.sym.owner=method testMethod, tpe.decls=List(method $init$: ()Unit, , method apply: (v1: T1, v2: T2)R, , method curried: => T1 => T2 => R, , method curry: => T1 => T2 => R, , method tupled: => (T1, T2) => R, , method toString: ()java.lang.String, ),
		//    "apply"),
		//  List( // 2 arguments(s)
		//    Literal(Constant(4)),
		//    Literal(Constant(5))
		//  )
		//)
		val x15 = math.sin _ //method value
		/*List(Block( // sym=null, tpe=Double => Double, tpe.sym=trait Function1, tpe.sym.owner=package scala
		  List(), // no statement
		  Function(
		      ValDef( // sym=value x, sym.owner=value $anonfun, sym.tpe=Double, tpe=<notype>, tpe.sym=<none>
		        ram> <synthetic>, // flags=<param> <synthetic>, annots=List()
		        "x",
		        TypeTree(), // sym=class Double, tpe=Double, tpe.sym=class Double, tpe.sym.owner=package scala,
		        EmptyTree
		      )
		  )
		    Apply( // sym=method sin, tpe=Double, tpe.sym=class Double, tpe.sym.owner=package scala
		      Select( // sym=method sin, sym.owner=class MathCommon, sym.tpe=(x: Double)Double, tpe=(x: Double)Double, tpe.sym=<none>
		        Select( // sym=object Math, sym.owner=package scala, sym.tpe=object Math, tpe=Math.type, tpe.sym=object Math, tpe.sym.owner=package scala
		          Ident("scala"), // sym=package scala, sym.owner=package <root>, sym.tpe=package scala, tpe=type, tpe.sym=package scala, tpe.sym.owner=package <root>,
		          "Math"),
		        "sin"),
		      List( // 1 arguments(s)
		        Ident("x") // sym=value x, sym.owner=value $anonfun, sym.tpe=Double, tpe=Double, tpe.sym=class Double, tpe.sym.owner=package scala
		      )
		    )
		  )
		)*/
		val x16 = (4, 5) //tuples
		//Apply( // sym=constructor Tuple2, tpe=(Int, Int), tpe.sym=class Tuple2, tpe.sym.owner=package scala
		//  Select( // sym=constructor Tuple2, isPrimaryConstructor, sym.owner=class Tuple2, sym.tpe=(_1: T1, _2: T2)(T1, T2), tpe=(_1: Int, _2: Int)(Int, Int), tpe.sym=<none>
		//    New( // sym=null, tpe=(Int, Int), tpe.sym=class Tuple2, tpe.sym.owner=package scala
		//      TypeTree() // sym=class Tuple2, tpe=(Int, Int), tpe.sym=class Tuple2, tpe.sym.owner=package scala
		//    ),
		//    "<init>"),
		//  List( // 2 arguments(s)
		//    Literal(Constant(4)),
		//    Literal(Constant(5))
		//  )
		//)

		val x17 = new C(5) //object creation
		//Apply( // sym=constructor C, tpe=tests.inputfiles.C, tpe.sym=class C, tpe.sym.owner=package inputfiles, tpe.decls=List(value a: => Int, , value a: Int, , constructor C: (a: Int)tests.inputfiles.C, )
		//  Select( // sym=constructor C, isPrimaryConstructor, sym.owner=class C, sym.tpe=(a: Int)tests.inputfiles.C, tpe=(a: Int)tests.inputfiles.C, tpe.sym=<none>
		//    New( // sym=null, tpe=tests.inputfiles.C, tpe.sym=class C, tpe.sym.owner=package inputfiles, tpe.decls=List(value a: => Int, , value a: Int, , constructor C: (a: Int)tests.inputfiles.C, )
		//      Ident("C") // sym=class C, sym.owner=package inputfiles, sym.tpe=tests.inputfiles.C, tpe=tests.inputfiles.C, tpe.sym=class C, tpe.sym.owner=package inputfiles, tpe.decls=List(value a: => Int, , value a: Int, , constructor C: (a: Int)tests.inputfiles.C, )
		//    ),
		//    "<init>"),
		//  List( // 1 arguments(s)
		//    Literal(Constant(5))
		//  )
		//)

		val x18 = z3
		//Ident("z3") // sym=value z3, sym.owner=method testMethod, sym.tpe=tests.inputfiles.C, tpe=tests.inputfiles.C, tpe.sym=class C, tpe.sym.owner=package inputfiles, tpe.decls=List(value a: => Int, , value a: Int, , constructor C: (a: Int)tests.inputfiles.C, )
		val x19 = new {def getName() = "xyz"}
		//Block( // sym=null, tpe=java.lang.Object{def getName(): java.lang.String}, tpe.sym=anonymous class $anon, tpe.sym.owner=value x19, tpe.decls=List(constructor $anon: ()java.lang.Object{def getName(): java.lang.String}, , method getName: ()java.lang.String, )
		//  List( // 1 statement(s)
		//    ClassDef( // sym=anonymous class $anon, sym.owner=value x19, sym.tpe=java.lang.Object{def getName(): java.lang.String}, tpe=<notype>, tpe.sym=<none>
		//      al, // flags=final, annots=List()
		//      "$anon",
		//      List(), // no type parameter
		//      Template( // sym=value <local $anon>, sym.owner=anonymous class $anon, sym.tpe=<notype>, tpe=java.lang.Object{def getName(): java.lang.String}, tpe.sym=anonymous class $anon, tpe.sym.owner=value x19, tpe.decls=List(constructor $anon: ()java.lang.Object{def getName(): java.lang.String}, , method getName: ()java.lang.String, )
		//        List(class Object), // parents
		//        ValDef( // sym=<none>, sym.tpe=<notype>, tpe=<notype>, tpe.sym=<none>
		//          0, // flags=, annots=List()
		//          "_",
		//          TypeTree(), // sym=<none>, tpe=<notype>, tpe.sym=<none>,
		//          EmptyTree
		//        ),
		//        List( // body
		//          DefDef( // sym=constructor $anon, isPrimaryConstructor, sym.owner=anonymous class $anon, sym.tpe=()java.lang.Object{def getName(): java.lang.String}, tpe=<notype>, tpe.sym=<none>
		//            thod>, // flags=<method>, annots=List()
		//            "<init>",
		//            List(), // no type parameter
		//            List(List()), // no parameter
		//            anonymous class $anon,
		//            Block( // sym=null, tpe=Unit, tpe.sym=class Unit, tpe.sym.owner=package scala
		//              List( // 1 statement(s)
		//                Apply( // sym=constructor Object, tpe=java.lang.Object, tpe.sym=class Object, tpe.sym.owner=package lang
		//                  Select( // sym=constructor Object, isPrimaryConstructor, sym.owner=class Object, sym.tpe=()java.lang.Object, tpe=()java.lang.Object, tpe.sym=<none>
		//                    Super("") // sym=anonymous class $anon, tpe=super.type, tpe.sym=anonymous class $anon, tpe.sym.owner=value x19, tpe.decls=List(constructor Object: ()java.lang.Object, , method getClass: ()java.lang.Class[_], , method hashCode: ()Int, , method equals: (x$1: Any)Boolean, , method clone: ()java.lang.Object, , method toString: ()java.lang.String, , method notify: ()Unit, , method notifyAll: ()Unit, , method wait: (x$1: Long)Unit, , method wait: (x$1: Long, x$2: Int)Unit, , method wait: ()Unit, , method finalize: ()Unit, , method ##: ()Int, , method ==: (x$1: AnyRef)Boolean, , method !=: (x$1: AnyRef)Boolean, , method eq: (x$1: AnyRef)Boolean, , method ne: (x$1: AnyRef)Boolean, , method synchronized: [T0](x$1: T0)T0, , method $isInstanceOf: [T0]()Boolean, , method $asInstanceOf: [T0]()T0, )
		//                      This(""), // sym=anonymous class $anon, sym.owner=value x19, sym.tpe=java.lang.Object{def getName(): java.lang.String}, tpe=java.lang.Object{def getName(): java.lang.String}, tpe.sym=anonymous class $anon, tpe.sym.owner=value x19, tpe.decls=List(constructor $anon: ()java.lang.Object{def getName(): java.lang.String}, , method getName: ()java.lang.String, )
		//                    "<init>"),
		//                  Nil // no argument
		//                )
		//              ),
		//              Literal(Constant(()))
		//            )
		//          ),
		//          DefDef( // sym=method getName, sym.owner=anonymous class $anon, sym.tpe=()java.lang.String, tpe=<notype>, tpe.sym=<none>
		//            thod>, // flags=<method>, annots=List()
		//            "getName",
		//            List(), // no type parameter
		//            List(List()), // no parameter
		//            java.lang.String,
		//            Literal(Constant(xyz))
		//          )
		//        )
		//      )
		//    )
		//  ),
		//  Apply( // sym=constructor $anon, tpe=java.lang.Object{def getName(): java.lang.String}, tpe.sym=anonymous class $anon, tpe.sym.owner=value x19, tpe.decls=List(constructor $anon: ()java.lang.Object{def getName(): java.lang.String}, , method getName: ()java.lang.String, )
		//    Select( // sym=constructor $anon, isPrimaryConstructor, sym.owner=anonymous class $anon, sym.tpe=()java.lang.Object{def getName(): java.lang.String}, tpe=()java.lang.Object{def getName(): java.lang.String}, tpe.sym=<none>
		//      New( // sym=null, tpe=java.lang.Object{def getName(): java.lang.String}, tpe.sym=anonymous class $anon, tpe.sym.owner=value x19, tpe.decls=List(constructor $anon: ()java.lang.Object{def getName(): java.lang.String}, , method getName: ()java.lang.String, )
		//        Ident("$anon") // sym=anonymous class $anon, sym.owner=value x19, sym.tpe=java.lang.Object{def getName(): java.lang.String}, tpe=java.lang.Object{def getName(): java.lang.String}, tpe.sym=anonymous class $anon, tpe.sym.owner=value x19, tpe.decls=List(constructor $anon: ()java.lang.Object{def getName(): java.lang.String}, , method getName: ()java.lang.String, )
		//      ),
		//      "<init>"),
		//    Nil // no argument
		//  )
		//)

		val x20 = {y; z; 3} //block
		//Block( // sym=null, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//  List( // 2 statement(s)
		//    Select( // sym=value y, sym.owner=object ExpnTest, sym.tpe==> Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//      This("ExpnTest"), // sym=object ExpnTest, sym.owner=package inputfiles, sym.tpe=object tests.inputfiles.ExpnTest, tpe=tests.inputfiles.ExpnTest.type, tpe.sym=object ExpnTest, tpe.sym.owner=package inputfiles, tpe.decls=List(constructor ExpnTest: ()object tests.inputfiles.ExpnTest, , value y: => Int, , value y: Int, , method sum: (x: Int, y: Int)Int, , method testMethod: ()Unit, )
		//      "y"),
		//    Ident("z") // sym=value z, sym.owner=method testMethod, sym.tpe=Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//  ),
		//  Literal(Constant(3))
		//)

		val x21 = Int
		//Select( // sym=object Int, sym.owner=package scala, sym.tpe=object Int, tpe=Int.type, tpe.sym=object Int, tpe.sym.owner=package scala
		//  Ident("scala"), // sym=package scala, sym.owner=package <root>, sym.tpe=package scala, tpe=type, tpe.sym=package scala, tpe.sym.owner=package <root>,
		//  "Int")

		val x22 = - sum(2, z)
		//Select( // sym=method unary_-, sym.owner=class Int, sym.tpe==> Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//  Apply( // sym=method sum, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//    Select( // sym=method sum, sym.owner=object ExpnTest, sym.tpe=(x: Int, y: Int)Int, tpe=(x: Int, y: Int)Int, tpe.sym=<none>
		//      This("ExpnTest"), // sym=object ExpnTest, sym.owner=package inputfiles, sym.tpe=object tests.inputfiles.ExpnTest, tpe=tests.inputfiles.ExpnTest.type, tpe.sym=object ExpnTest, tpe.sym.owner=package inputfiles, tpe.decls=List(constructor ExpnTest: ()object tests.inputfiles.ExpnTest, , value y: => Int, , value y: Int, , method sum: (x: Int, y: Int)Int, , method testMethod: ()Unit, )
		//      "sum"),
		//    List( // 2 arguments(s)
		//      Literal(Constant(2)),
		//      Ident("z") // sym=value z, sym.owner=method testMethod, sym.tpe=Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//    )
		//  ),
		//  "unary_$minus")

		val x23 = (t = 5)
		//Assign(
		//  Ident("t") // sym=variable t, sym.owner=method testMethod, sym.tpe=Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//  Literal(Constant(5))
		//)

		val x24 = 2 + z * y
		//Apply( // sym=method +, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//  Select( // sym=method +, sym.owner=class Int, sym.tpe=(x: Int)Int, tpe=(x: Int)Int, tpe.sym=<none>
		//    Literal(Constant(2)),
		//    "$plus"),
		//  List( // 1 arguments(s)
		//    Apply( // sym=method *, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//      Select( // sym=method *, sym.owner=class Int, sym.tpe=(x: Int)Int, tpe=(x: Int)Int, tpe.sym=<none>
		//        Ident("z"), // sym=value z, sym.owner=method testMethod, sym.tpe=Int, tpe=z.type, tpe.sym=value z, tpe.sym.owner=method testMethod, tpe.decls=List(method toByte: => Byte, , method toShort: => Short, , method toChar: => Char, , method toInt: => Int, , method toLong: => Long, , method toFloat: => Float, , method toDouble: => Double, , method unary_+: => Int, , method unary_-: => Int, , method unary_~: => Int, , method +: (x: String)String, , method <<: (x: <?>)Int, , method <<: (x: <?>)Int, , method >>>: (x: <?>)Int, , method >>>: (x: <?>)Int, , method >>: (x: <?>)Int, , method >>: (x: <?>)Int, , method ==: (x: Byte)Boolean, , method ==: (x: Short)Boolean, , method ==: (x: Char)Boolean, , method ==: (x: Int)Boolean, , method ==: (x: Long)Boolean, , method ==: (x: Float)Boolean, , method ==: (x: Double)Boolean, , method !=: (x: Byte)Boolean, , method !=: (x: Short)Boolean, , method !=: (x: Char)Boolean, , method !=: (x: Int)Boolean, , method !=: (x: Long)Boolean, , method !=: (x: Float)Boolean, , method !=: (x: Double)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method >: (x: Byte)Boolean, , method >: (x: Short)Boolean, , method >: (x: Char)Boolean, , method >: (x: Int)Boolean, , method >: (x: Long)Boolean, , method >: (x: Float)Boolean, , method >: (x: Double)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method |: (x: <?>)Int, , method |: (x: <?>)Int, , method |: (x: <?>)Int, , method |: (x: <?>)Int, , method |: (x: <?>)Long, , method &: (x: <?>)Int, , method &: (x: <?>)Int, , method &: (x: <?>)Int, , method &: (x: <?>)Int, , method &: (x: <?>)Long, , method ^: (x: <?>)Int, , method ^: (x: <?>)Int, , method ^: (x: <?>)Int, , method ^: (x: <?>)Int, , method ^: (x: <?>)Long, , method +: (x: Byte)Int, , method +: (x: Short)Int, , method +: (x: Char)Int, , method +: (x: Int)Int, , method +: (x: Long)Long, , method +: (x: Float)Float, , method +: (x: Double)Double, , method -: (x: <?>)Int, , method -: (x: <?>)Int, , method -: (x: <?>)Int, , method -: (x: <?>)Int, , method -: (x: <?>)Long, , method -: (x: <?>)Float, , method -: (x: <?>)Double, , method *: (x: Byte)Int, , method *: (x: Short)Int, , method *: (x: Char)Int, , method *: (x: Int)Int, , method *: (x: Long)Long, , method *: (x: Float)Float, , method *: (x: Double)Double, , method /: (x: <?>)Int, , method /: (x: <?>)Int, , method /: (x: <?>)Int, , method /: (x: <?>)Int, , method /: (x: <?>)Long, , method /: (x: <?>)Float, , method /: (x: <?>)Double, , method %: (x: Byte)Int, , method %: (x: Short)Int, , method %: (x: Char)Int, , method %: (x: Int)Int, , method %: (x: Long)Long, , method %: (x: Float)Float, , method %: (x: Double)Double, , method getClass: ()Class[Int], ),
		//        "$times"),
		//      List( // 1 arguments(s)
		//        Select( // sym=value y, sym.owner=object ExpnTest, sym.tpe==> Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//          This("ExpnTest"), // sym=object ExpnTest, sym.owner=package inputfiles, sym.tpe=object tests.inputfiles.ExpnTest, tpe=tests.inputfiles.ExpnTest.type, tpe.sym=object ExpnTest, tpe.sym.owner=package inputfiles, tpe.decls=List(constructor ExpnTest: ()object tests.inputfiles.ExpnTest, , value y: => Int, , value y: Int, , method sum: (x: Int, y: Int)Int, , method testMethod: ()Unit, )
		//          "y")
		//      )
		//    )
		//  )
		//)

		val x25 = z % 2
		//Apply( // sym=method %, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//  Select( // sym=method %, sym.owner=class Int, sym.tpe=(x: Int)Int, tpe=(x: Int)Int, tpe.sym=<none>
		//    Ident("z"), // sym=value z, sym.owner=method testMethod, sym.tpe=Int, tpe=z.type, tpe.sym=value z, tpe.sym.owner=method testMethod, tpe.decls=List(method toByte: => Byte, , method toShort: => Short, , method toChar: => Char, , method toInt: => Int, , method toLong: => Long, , method toFloat: => Float, , method toDouble: => Double, , method unary_+: => Int, , method unary_-: => Int, , method unary_~: => Int, , method +: (x: String)String, , method <<: (x: <?>)Int, , method <<: (x: <?>)Int, , method >>>: (x: <?>)Int, , method >>>: (x: <?>)Int, , method >>: (x: <?>)Int, , method >>: (x: <?>)Int, , method ==: (x: Byte)Boolean, , method ==: (x: Short)Boolean, , method ==: (x: Char)Boolean, , method ==: (x: Int)Boolean, , method ==: (x: Long)Boolean, , method ==: (x: Float)Boolean, , method ==: (x: Double)Boolean, , method !=: (x: Byte)Boolean, , method !=: (x: Short)Boolean, , method !=: (x: Char)Boolean, , method !=: (x: Int)Boolean, , method !=: (x: Long)Boolean, , method !=: (x: Float)Boolean, , method !=: (x: Double)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method >: (x: Byte)Boolean, , method >: (x: Short)Boolean, , method >: (x: Char)Boolean, , method >: (x: Int)Boolean, , method >: (x: Long)Boolean, , method >: (x: Float)Boolean, , method >: (x: Double)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method |: (x: <?>)Int, , method |: (x: <?>)Int, , method |: (x: <?>)Int, , method |: (x: <?>)Int, , method |: (x: <?>)Long, , method &: (x: <?>)Int, , method &: (x: <?>)Int, , method &: (x: <?>)Int, , method &: (x: <?>)Int, , method &: (x: <?>)Long, , method ^: (x: <?>)Int, , method ^: (x: <?>)Int, , method ^: (x: <?>)Int, , method ^: (x: <?>)Int, , method ^: (x: <?>)Long, , method +: (x: Byte)Int, , method +: (x: Short)Int, , method +: (x: Char)Int, , method +: (x: Int)Int, , method +: (x: Long)Long, , method +: (x: Float)Float, , method +: (x: Double)Double, , method -: (x: <?>)Int, , method -: (x: <?>)Int, , method -: (x: <?>)Int, , method -: (x: <?>)Int, , method -: (x: <?>)Long, , method -: (x: <?>)Float, , method -: (x: <?>)Double, , method *: (x: Byte)Int, , method *: (x: Short)Int, , method *: (x: Char)Int, , method *: (x: Int)Int, , method *: (x: Long)Long, , method *: (x: Float)Float, , method *: (x: Double)Double, , method /: (x: <?>)Int, , method /: (x: <?>)Int, , method /: (x: <?>)Int, , method /: (x: <?>)Int, , method /: (x: <?>)Long, , method /: (x: <?>)Float, , method /: (x: <?>)Double, , method %: (x: Byte)Int, , method %: (x: Short)Int, , method %: (x: Char)Int, , method %: (x: Int)Int, , method %: (x: Long)Long, , method %: (x: Float)Float, , method %: (x: Double)Double, , method getClass: ()Class[Int], ),
		//    "$percent"),
		//  List( // 1 arguments(s)
		//    Literal(Constant(2))
		//  )
		//)

		val x26 = if(y > 0) 2
		//If(
		//  Apply( // sym=method >, tpe=Boolean, tpe.sym=class Boolean, tpe.sym.owner=package scala
		//    Select( // sym=method >, sym.owner=class Int, sym.tpe=(x: Int)Boolean, tpe=(x: Int)Boolean, tpe.sym=<none>
		//      Select( // sym=value y, sym.owner=object ExpnTest, sym.tpe==> Int, tpe=tests.inputfiles.ExpnTest.y.type, tpe.sym=value y, tpe.sym.owner=object ExpnTest, tpe.decls=List(method toByte: => Byte, , method toShort: => Short, , method toChar: => Char, , method toInt: => Int, , method toLong: => Long, , method toFloat: => Float, , method toDouble: => Double, , method unary_+: => Int, , method unary_-: => Int, , method unary_~: => Int, , method +: (x: String)String, , method <<: (x: <?>)Int, , method <<: (x: <?>)Int, , method >>>: (x: <?>)Int, , method >>>: (x: <?>)Int, , method >>: (x: <?>)Int, , method >>: (x: <?>)Int, , method ==: (x: Byte)Boolean, , method ==: (x: Short)Boolean, , method ==: (x: Char)Boolean, , method ==: (x: Int)Boolean, , method ==: (x: Long)Boolean, , method ==: (x: Float)Boolean, , method ==: (x: Double)Boolean, , method !=: (x: Byte)Boolean, , method !=: (x: Short)Boolean, , method !=: (x: Char)Boolean, , method !=: (x: Int)Boolean, , method !=: (x: Long)Boolean, , method !=: (x: Float)Boolean, , method !=: (x: Double)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method >: (x: Byte)Boolean, , method >: (x: Short)Boolean, , method >: (x: Char)Boolean, , method >: (x: Int)Boolean, , method >: (x: Long)Boolean, , method >: (x: Float)Boolean, , method >: (x: Double)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method |: (x: <?>)Int, , method |: (x: <?>)Int, , method |: (x: <?>)Int, , method |: (x: <?>)Int, , method |: (x: <?>)Long, , method &: (x: <?>)Int, , method &: (x: <?>)Int, , method &: (x: <?>)Int, , method &: (x: <?>)Int, , method &: (x: <?>)Long, , method ^: (x: <?>)Int, , method ^: (x: <?>)Int, , method ^: (x: <?>)Int, , method ^: (x: <?>)Int, , method ^: (x: <?>)Long, , method +: (x: Byte)Int, , method +: (x: Short)Int, , method +: (x: Char)Int, , method +: (x: Int)Int, , method +: (x: Long)Long, , method +: (x: Float)Float, , method +: (x: Double)Double, , method -: (x: <?>)Int, , method -: (x: <?>)Int, , method -: (x: <?>)Int, , method -: (x: <?>)Int, , method -: (x: <?>)Long, , method -: (x: <?>)Float, , method -: (x: <?>)Double, , method *: (x: Byte)Int, , method *: (x: Short)Int, , method *: (x: Char)Int, , method *: (x: Int)Int, , method *: (x: Long)Long, , method *: (x: Float)Float, , method *: (x: Double)Double, , method /: (x: <?>)Int, , method /: (x: <?>)Int, , method /: (x: <?>)Int, , method /: (x: <?>)Int, , method /: (x: <?>)Long, , method /: (x: <?>)Float, , method /: (x: <?>)Double, , method %: (x: Byte)Int, , method %: (x: Short)Int, , method %: (x: Char)Int, , method %: (x: Int)Int, , method %: (x: Long)Long, , method %: (x: Float)Float, , method %: (x: Double)Double, , method getClass: ()Class[Int], )
		//        This("ExpnTest"), // sym=object ExpnTest, sym.owner=package inputfiles, sym.tpe=object tests.inputfiles.ExpnTest, tpe=tests.inputfiles.ExpnTest.type, tpe.sym=object ExpnTest, tpe.sym.owner=package inputfiles, tpe.decls=List(constructor ExpnTest: ()object tests.inputfiles.ExpnTest, , value y: => Int, , value y: Int, , method sum: (x: Int, y: Int)Int, , method testMethod: ()Unit, )
		//        "y"),
		//      "$greater"),
		//    List( // 1 arguments(s)
		//      Literal(Constant(0))
		//    )
		//  )
		//  Literal(Constant(2))
		//  Literal(Constant(()))
		//)

		val x27 = while (y > 0) 2
		//List(LabelDef(
		//while$1
		//)
		//  If(
		//    Apply( // sym=method >, tpe=Boolean, tpe.sym=class Boolean, tpe.sym.owner=package scala
		//      Select( // sym=method >, sym.owner=class Int, sym.tpe=(x: Int)Boolean, tpe=(x: Int)Boolean, tpe.sym=<none>
		//        Select( // sym=value y, sym.owner=object ExpnTest, sym.tpe==> Int, tpe=tests.inputfiles.ExpnTest.y.type, tpe.sym=value y, tpe.sym.owner=object ExpnTest, tpe.decls=List(method toByte: => Byte, , method toShort: => Short, , method toChar: => Char, , method toInt: => Int, , method toLong: => Long, , method toFloat: => Float, , method toDouble: => Double, , method unary_+: => Int, , method unary_-: => Int, , method unary_~: => Int, , method +: (x: String)String, , method <<: (x: <?>)Int, , method <<: (x: <?>)Int, , method >>>: (x: <?>)Int, , method >>>: (x: <?>)Int, , method >>: (x: <?>)Int, , method >>: (x: <?>)Int, , method ==: (x: Byte)Boolean, , method ==: (x: Short)Boolean, , method ==: (x: Char)Boolean, , method ==: (x: Int)Boolean, , method ==: (x: Long)Boolean, , method ==: (x: Float)Boolean, , method ==: (x: Double)Boolean, , method !=: (x: Byte)Boolean, , method !=: (x: Short)Boolean, , method !=: (x: Char)Boolean, , method !=: (x: Int)Boolean, , method !=: (x: Long)Boolean, , method !=: (x: Float)Boolean, , method !=: (x: Double)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method >: (x: Byte)Boolean, , method >: (x: Short)Boolean, , method >: (x: Char)Boolean, , method >: (x: Int)Boolean, , method >: (x: Long)Boolean, , method >: (x: Float)Boolean, , method >: (x: Double)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method |: (x: <?>)Int, , method |: (x: <?>)Int, , method |: (x: <?>)Int, , method |: (x: <?>)Int, , method |: (x: <?>)Long, , method &: (x: <?>)Int, , method &: (x: <?>)Int, , method &: (x: <?>)Int, , method &: (x: <?>)Int, , method &: (x: <?>)Long, , method ^: (x: <?>)Int, , method ^: (x: <?>)Int, , method ^: (x: <?>)Int, , method ^: (x: <?>)Int, , method ^: (x: <?>)Long, , method +: (x: Byte)Int, , method +: (x: Short)Int, , method +: (x: Char)Int, , method +: (x: Int)Int, , method +: (x: Long)Long, , method +: (x: Float)Float, , method +: (x: Double)Double, , method -: (x: <?>)Int, , method -: (x: <?>)Int, , method -: (x: <?>)Int, , method -: (x: <?>)Int, , method -: (x: <?>)Long, , method -: (x: <?>)Float, , method -: (x: <?>)Double, , method *: (x: Byte)Int, , method *: (x: Short)Int, , method *: (x: Char)Int, , method *: (x: Int)Int, , method *: (x: Long)Long, , method *: (x: Float)Float, , method *: (x: Double)Double, , method /: (x: <?>)Int, , method /: (x: <?>)Int, , method /: (x: <?>)Int, , method /: (x: <?>)Int, , method /: (x: <?>)Long, , method /: (x: <?>)Float, , method /: (x: <?>)Double, , method %: (x: Byte)Int, , method %: (x: Short)Int, , method %: (x: Char)Int, , method %: (x: Int)Int, , method %: (x: Long)Long, , method %: (x: Float)Float, , method %: (x: Double)Double, , method getClass: ()Class[Int], )
		//          This("ExpnTest"), // sym=object ExpnTest, sym.owner=package inputfiles, sym.tpe=object tests.inputfiles.ExpnTest, tpe=tests.inputfiles.ExpnTest.type, tpe.sym=object ExpnTest, tpe.sym.owner=package inputfiles, tpe.decls=List(constructor ExpnTest: ()object tests.inputfiles.ExpnTest, , value y: => Int, , value y: Int, , method sum: (x: Int, y: Int)Int, , method testMethod: ()Unit, )
		//          "y"),
		//        "$greater"),
		//      List( // 1 arguments(s)
		//        Literal(Constant(0))
		//      )
		//    )
		//    Block( // sym=null, tpe=Unit, tpe.sym=class Unit, tpe.sym.owner=package scala
		//      List( // 1 statement(s)
		//        Literal(Constant(2))
		//      ),
		//      Apply( // sym=method while$1, tpe=Unit, tpe.sym=class Unit, tpe.sym.owner=package scala
		//        Ident("while$1"), // sym=method while$1, sym.owner=value x27, sym.tpe=()Unit, tpe=()Unit, tpe.sym=<none>,
		//        Nil // no argument
		//      )
		//    )
		//    Literal(Constant(()))
		//  )
		//)

		val x28 = do {y} while (y > 0)
		//List(LabelDef(
		//doWhile$1
		//)
		//  Block( // sym=null, tpe=Unit, tpe.sym=class Unit, tpe.sym.owner=package scala
		//    List( // 1 statement(s)
		//      Select( // sym=value y, sym.owner=object ExpnTest, sym.tpe==> Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//        This("ExpnTest"), // sym=object ExpnTest, sym.owner=package inputfiles, sym.tpe=object tests.inputfiles.ExpnTest, tpe=tests.inputfiles.ExpnTest.type, tpe.sym=object ExpnTest, tpe.sym.owner=package inputfiles, tpe.decls=List(constructor ExpnTest: ()object tests.inputfiles.ExpnTest, , value y: => Int, , value y: Int, , method sum: (x: Int, y: Int)Int, , method testMethod: ()Unit, )
		//        "y")
		//    ),
		//    If(
		//      Apply( // sym=method >, tpe=Boolean, tpe.sym=class Boolean, tpe.sym.owner=package scala
		//        Select( // sym=method >, sym.owner=class Int, sym.tpe=(x: Int)Boolean, tpe=(x: Int)Boolean, tpe.sym=<none>
		//          Select( // sym=value y, sym.owner=object ExpnTest, sym.tpe==> Int, tpe=tests.inputfiles.ExpnTest.y.type, tpe.sym=value y, tpe.sym.owner=object ExpnTest, tpe.decls=List(method toByte: => Byte, , method toShort: => Short, , method toChar: => Char, , method toInt: => Int, , method toLong: => Long, , method toFloat: => Float, , method toDouble: => Double, , method unary_+: => Int, , method unary_-: => Int, , method unary_~: => Int, , method +: (x: String)String, , method <<: (x: <?>)Int, , method <<: (x: <?>)Int, , method >>>: (x: <?>)Int, , method >>>: (x: <?>)Int, , method >>: (x: <?>)Int, , method >>: (x: <?>)Int, , method ==: (x: Byte)Boolean, , method ==: (x: Short)Boolean, , method ==: (x: Char)Boolean, , method ==: (x: Int)Boolean, , method ==: (x: Long)Boolean, , method ==: (x: Float)Boolean, , method ==: (x: Double)Boolean, , method !=: (x: Byte)Boolean, , method !=: (x: Short)Boolean, , method !=: (x: Char)Boolean, , method !=: (x: Int)Boolean, , method !=: (x: Long)Boolean, , method !=: (x: Float)Boolean, , method !=: (x: Double)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method <=: (x: <?>)Boolean, , method >: (x: Byte)Boolean, , method >: (x: Short)Boolean, , method >: (x: Char)Boolean, , method >: (x: Int)Boolean, , method >: (x: Long)Boolean, , method >: (x: Float)Boolean, , method >: (x: Double)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method >=: (x: <?>)Boolean, , method |: (x: <?>)Int, , method |: (x: <?>)Int, , method |: (x: <?>)Int, , method |: (x: <?>)Int, , method |: (x: <?>)Long, , method &: (x: <?>)Int, , method &: (x: <?>)Int, , method &: (x: <?>)Int, , method &: (x: <?>)Int, , method &: (x: <?>)Long, , method ^: (x: <?>)Int, , method ^: (x: <?>)Int, , method ^: (x: <?>)Int, , method ^: (x: <?>)Int, , method ^: (x: <?>)Long, , method +: (x: Byte)Int, , method +: (x: Short)Int, , method +: (x: Char)Int, , method +: (x: Int)Int, , method +: (x: Long)Long, , method +: (x: Float)Float, , method +: (x: Double)Double, , method -: (x: <?>)Int, , method -: (x: <?>)Int, , method -: (x: <?>)Int, , method -: (x: <?>)Int, , method -: (x: <?>)Long, , method -: (x: <?>)Float, , method -: (x: <?>)Double, , method *: (x: Byte)Int, , method *: (x: Short)Int, , method *: (x: Char)Int, , method *: (x: Int)Int, , method *: (x: Long)Long, , method *: (x: Float)Float, , method *: (x: Double)Double, , method /: (x: <?>)Int, , method /: (x: <?>)Int, , method /: (x: <?>)Int, , method /: (x: <?>)Int, , method /: (x: <?>)Long, , method /: (x: <?>)Float, , method /: (x: <?>)Double, , method %: (x: Byte)Int, , method %: (x: Short)Int, , method %: (x: Char)Int, , method %: (x: Int)Int, , method %: (x: Long)Long, , method %: (x: Float)Float, , method %: (x: Double)Double, , method getClass: ()Class[Int], )
		//            This("ExpnTest"), // sym=object ExpnTest, sym.owner=package inputfiles, sym.tpe=object tests.inputfiles.ExpnTest, tpe=tests.inputfiles.ExpnTest.type, tpe.sym=object ExpnTest, tpe.sym.owner=package inputfiles, tpe.decls=List(constructor ExpnTest: ()object tests.inputfiles.ExpnTest, , value y: => Int, , value y: Int, , method sum: (x: Int, y: Int)Int, , method testMethod: ()Unit, )
		//            "y"),
		//          "$greater"),
		//        List( // 1 arguments(s)
		//          Literal(Constant(0))
		//        )
		//      )
		//      Apply( // sym=method doWhile$1, tpe=Unit, tpe.sym=class Unit, tpe.sym.owner=package scala
		//        Ident("doWhile$1"), // sym=method doWhile$1, sym.owner=value x28, sym.tpe=()Unit, tpe=()Unit, tpe.sym=<none>,
		//        Nil // no argument
		//      )
		//      Literal(Constant(()))
		//    )
		//  )
		//)

		val x29 = for(i <- 1 until 2) yield (i)
		//List(Apply( // sym=method map, tpe=scala.collection.immutable.IndexedSeq[Int], tpe.sym=trait IndexedSeq, tpe.sym.owner=package immutable, tpe.decls=List(method $init$: ()Unit, , method companion: => scala.collection.generic.GenericCompanion[scala.collection.immutable.IndexedSeq], , method toIndexedSeq: [B<: <?>]=> scala.collection.immutable.IndexedSeq[B], )
		//  Apply( // sym=method map, tpe=(implicit bf: scala.collection.generic.CanBuildFrom[scala.collection.immutable.IndexedSeq[Int],Int,scala.collection.immutable.IndexedSeq[Int]])scala.collection.immutable.IndexedSeq[Int], tpe.sym=<none>
		//    TypeApply( // sym=method map, tpe=(f: Int => Int)(implicit bf: scala.collection.generic.CanBuildFrom[scala.collection.immutable.IndexedSeq[Int],Int,scala.collection.immutable.IndexedSeq[Int]])scala.collection.immutable.IndexedSeq[Int], tpe.sym=<none>
		//      Select( // sym=method map, sym.owner=trait TraversableLike, sym.tpe=[B, That](f: A => B)(implicit bf: scala.collection.generic.CanBuildFrom[Repr,B,That])That, tpe=[B, That](f: Int => B)(implicit bf: scala.collection.generic.CanBuildFrom[scala.collection.immutable.IndexedSeq[Int],B,That])That, tpe.sym=<none>
		//        Apply( // sym=method until, tpe=scala.collection.immutable.Range, tpe.sym=class Range, tpe.sym.owner=package immutable, tpe.decls=List(value start: => Int, , value start: Int, , value end: => Int, , value end: Int, , value step: => Int, , value step: Int, , constructor Range: (start: <?>, end: <?>, step: <?>)scala.collection.immutable.Range, , method par: => scala.collection.parallel.immutable.ParRange, , lazy value numRangeElements: => Int, , lazy value numRangeElements: Int, , method copy: (start: <?>, end: <?>, step: <?>)scala.collection.immutable.Range, , method by: (step: <?>)scala.collection.immutable.Range, , method isInclusive: => Boolean, , method foreach: [U<: <?>](f: <?>)Unit, , method length: => Int, , lazy value last: => Int, , lazy value last: Int, , method isEmpty: => Boolean, , method apply: (idx: <?>)Int, , method take: (n: <?>)scala.collection.immutable.Range, , method drop: (n: <?>)scala.collection.immutable.Range, , method init: => scala.collection.immutable.Range, , method tail: => scala.collection.immutable.Range, , method skipCount: (p: <?>)Int, , method isWithinBoundaries: (elem: <?>)Boolean, , method locationAfterN: (n: <?>)Int, , method newEmptyRange: (value: <?>)scala.collection.immutable.Range, , method takeWhile: (p: <?>)scala.collection.immutable.Range, , method dropWhile: (p: <?>)scala.collection.immutable.Range, , method span: (p: <?>)(scala.collection.immutable.Range, scala.collection.immutable.Range), , method splitAt: (n: <?>)(scala.collection.immutable.Range, scala.collection.immutable.Range), , method takeRight: (n: <?>)scala.collection.immutable.Range, , method dropRight: (n: <?>)scala.collection.immutable.Range, , method reverse: => scala.collection.immutable.Range, , method inclusive: => scala.collection.immutable.Range, , method contains: (x: <?>)Boolean, , method toIterable: => scala.collection.immutable.Range, , method toSeq: => scala.collection.immutable.Range, , method equals: (other: <?>)Boolean, , method toString: ()String, )
		//          Select( // sym=method until, sym.owner=class RichInt, sym.tpe=(end: Int)scala.collection.immutable.Range, tpe=(end: Int)scala.collection.immutable.Range, tpe.sym=<none>
		//            Apply( // sym=method intWrapper, tpe=scala.runtime.RichInt, tpe.sym=class RichInt, tpe.sym.owner=package runtime, tpe.decls=List(value self: => Int, , value self: Int, , constructor RichInt: (self: <?>)scala.runtime.RichInt, , type ResultWithoutStep: RichInt.this.ResultWithoutStep, , method isWhole: ()Boolean, , method until: (end: Int)scala.collection.immutable.Range, , method until: (end: Int, step: Int)scala.collection.immutable.Range, , method to: (end: <?>)scala.collection.immutable.Range.Inclusive, , method to: (end: <?>, step: <?>)scala.collection.immutable.Range.Inclusive, , method min: (that: <?>)Int, , method max: (that: <?>)Int, , method abs: => Int, , method toBinaryString: => String, , method toHexString: => String, , method toOctalString: => String, )
		//              Select( // sym=method intWrapper, sym.owner=class LowPriorityImplicits, sym.tpe=(x: Int)scala.runtime.RichInt, tpe=(x: Int)scala.runtime.RichInt, tpe.sym=<none>
		//                Select( // sym=object Predef, sym.owner=package scala, sym.tpe=object Predef, tpe=type, tpe.sym=object Predef, tpe.sym.owner=package scala
		//                  This("scala"), // sym=package scala, sym.owner=package <root>, sym.tpe=package scala, tpe=type, tpe.sym=package scala, tpe.sym.owner=package <root>
		//                  "Predef"),
		//                "intWrapper"),
		//              List( // 1 arguments(s)
		//                Literal(Constant(1))
		//              )
		//            ),
		//            "until"),
		//          List( // 1 arguments(s)
		//            Literal(Constant(2))
		//          )
		//        ),
		//        "map"),
		//      List(
		//      TypeTree(), // sym=class Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala,
		//      TypeTree() // sym=trait IndexedSeq, tpe=scala.collection.immutable.IndexedSeq[Int], tpe.sym=trait IndexedSeq, tpe.sym.owner=package immutable, tpe.decls=List(method $init$: ()Unit, , method companion: => scala.collection.generic.GenericCompanion[scala.collection.immutable.IndexedSeq], , method toIndexedSeq: [B >: A]=> scala.collection.immutable.IndexedSeq[B], )
		//      )
		//    ),
		//    List( // 1 arguments(s)
		//      Function(
		//          ValDef( // sym=value i, sym.owner=value $anonfun, sym.tpe=Int, tpe=<notype>, tpe.sym=<none>
		//            ram>, // flags=<param>, annots=List()
		//            "i",
		//            TypeTree(), // sym=class Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala,
		//            EmptyTree
		//          )
		//      )
		//        Ident("i") // sym=value i, sym.owner=value $anonfun, sym.tpe=Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//      )
		//    )
		//  ),
		//  List( // 1 arguments(s)
		//    TypeApply( // sym=method canBuildFrom, tpe=scala.collection.generic.CanBuildFrom[scala.collection.immutable.IndexedSeq.Coll,Int,scala.collection.immutable.IndexedSeq[Int]], tpe.sym=trait CanBuildFrom, tpe.sym.owner=package generic, tpe.decls=List(method apply: (from: <?>)scala.collection.mutable.Builder[Elem,To], , method apply: ()scala.collection.mutable.Builder[Elem,To], )
		//      Select( // sym=method canBuildFrom, sym.owner=object IndexedSeq, sym.tpe=[A]=> scala.collection.generic.CanBuildFrom[scala.collection.immutable.IndexedSeq.Coll,A,scala.collection.immutable.IndexedSeq[A]], tpe=[A]=> scala.collection.generic.CanBuildFrom[scala.collection.immutable.IndexedSeq.Coll,A,scala.collection.immutable.IndexedSeq[A]], tpe.sym=trait CanBuildFrom, tpe.sym.owner=package generic, tpe.decls=List(method apply: (from: <?>)scala.collection.mutable.Builder[Elem,To], , method apply: ()scala.collection.mutable.Builder[Elem,To], )
		//        Select( // sym=object IndexedSeq, sym.owner=package immutable, sym.tpe=object scala.collection.immutable.IndexedSeq, tpe=scala.collection.immutable.IndexedSeq.type, tpe.sym=object IndexedSeq, tpe.sym.owner=package immutable, tpe.decls=List(constructor IndexedSeq: ()object scala.collection.immutable.IndexedSeq, , class Impl: scala.collection.immutable.IndexedSeq.Impl, , method canBuildFrom: [A]=> scala.collection.generic.CanBuildFrom[scala.collection.immutable.IndexedSeq.Coll,A,scala.collection.immutable.IndexedSeq[A]], , method newBuilder: [A<: <?>]=> scala.collection.mutable.Builder[A,scala.collection.immutable.IndexedSeq[A]], )
		//          This("immutable"), // sym=package immutable, sym.owner=package collection, sym.tpe=package scala.collection.immutable, tpe=scala.collection.immutable.type, tpe.sym=package immutable, tpe.sym.owner=package collection
		//          "IndexedSeq"),
		//        "canBuildFrom"),
		//      List(
		//      TypeTree() // sym=class Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//      )
		//    )
		//  )
		//)


		val x30 = 1 until 2
		//Apply( // sym=method until, tpe=scala.collection.immutable.Range, tpe.sym=class Range, tpe.sym.owner=package immutable, tpe.decls=List(value start: => Int, , value start: Int, , value end: => Int, , value end: Int, , value step: => Int, , value step: Int, , constructor Range: (start: <?>, end: <?>, step: <?>)scala.collection.immutable.Range, , method par: => scala.collection.parallel.immutable.ParRange, , lazy value numRangeElements: => Int, , lazy value numRangeElements: Int, , method copy: (start: <?>, end: <?>, step: <?>)scala.collection.immutable.Range, , method by: (step: <?>)scala.collection.immutable.Range, , method isInclusive: => Boolean, , method foreach: [U<: <?>](f: <?>)Unit, , method length: => Int, , lazy value last: => Int, , lazy value last: Int, , method isEmpty: => Boolean, , method apply: (idx: <?>)Int, , method take: (n: <?>)scala.collection.immutable.Range, , method drop: (n: <?>)scala.collection.immutable.Range, , method init: => scala.collection.immutable.Range, , method tail: => scala.collection.immutable.Range, , method skipCount: (p: <?>)Int, , method isWithinBoundaries: (elem: <?>)Boolean, , method locationAfterN: (n: <?>)Int, , method newEmptyRange: (value: <?>)scala.collection.immutable.Range, , method takeWhile: (p: <?>)scala.collection.immutable.Range, , method dropWhile: (p: <?>)scala.collection.immutable.Range, , method span: (p: <?>)(scala.collection.immutable.Range, scala.collection.immutable.Range), , method splitAt: (n: <?>)(scala.collection.immutable.Range, scala.collection.immutable.Range), , method takeRight: (n: <?>)scala.collection.immutable.Range, , method dropRight: (n: <?>)scala.collection.immutable.Range, , method reverse: => scala.collection.immutable.Range, , method inclusive: => scala.collection.immutable.Range, , method contains: (x: <?>)Boolean, , method toIterable: => scala.collection.immutable.Range, , method toSeq: => scala.collection.immutable.Range, , method equals: (other: <?>)Boolean, , method toString: ()String, )
		//  Select( // sym=method until, sym.owner=class RichInt, sym.tpe=(end: Int)scala.collection.immutable.Range, tpe=(end: Int)scala.collection.immutable.Range, tpe.sym=<none>
		//    Apply( // sym=method intWrapper, tpe=scala.runtime.RichInt, tpe.sym=class RichInt, tpe.sym.owner=package runtime, tpe.decls=List(value self: => Int, , value self: Int, , constructor RichInt: (self: <?>)scala.runtime.RichInt, , type ResultWithoutStep: RichInt.this.ResultWithoutStep, , method isWhole: ()Boolean, , method until: (end: Int)scala.collection.immutable.Range, , method until: (end: Int, step: Int)scala.collection.immutable.Range, , method to: (end: <?>)scala.collection.immutable.Range.Inclusive, , method to: (end: <?>, step: <?>)scala.collection.immutable.Range.Inclusive, , method min: (that: <?>)Int, , method max: (that: <?>)Int, , method abs: => Int, , method toBinaryString: => String, , method toHexString: => String, , method toOctalString: => String, )
		//      Select( // sym=method intWrapper, sym.owner=class LowPriorityImplicits, sym.tpe=(x: Int)scala.runtime.RichInt, tpe=(x: Int)scala.runtime.RichInt, tpe.sym=<none>
		//        Select( // sym=object Predef, sym.owner=package scala, sym.tpe=object Predef, tpe=type, tpe.sym=object Predef, tpe.sym.owner=package scala
		//          This("scala"), // sym=package scala, sym.owner=package <root>, sym.tpe=package scala, tpe=type, tpe.sym=package scala, tpe.sym.owner=package <root>
		//          "Predef"),
		//        "intWrapper"),
		//      List( // 1 arguments(s)
		//        Literal(Constant(1))
		//      )
		//    ),
		//    "until"),
		//  List( // 1 arguments(s)
		//    Literal(Constant(2))
		//  )
		//)


		val x31 = throw new RuntimeException
		//Throw(
		//  Apply( // sym=constructor RuntimeException, tpe=java.lang.RuntimeException, tpe.sym=class RuntimeException, tpe.sym.owner=package lang
		//    Select( // sym=constructor RuntimeException, isPrimaryConstructor, sym.owner=class RuntimeException, sym.tpe=()java.lang.RuntimeException, tpe=()java.lang.RuntimeException, tpe.sym=<none>
		//      New( // sym=null, tpe=java.lang.RuntimeException, tpe.sym=class RuntimeException, tpe.sym.owner=package lang
		//        Select( // sym=type RuntimeException, sym.owner=object scala, sym.tpe=RuntimeException, tpe=java.lang.RuntimeException, tpe.sym=class RuntimeException, tpe.sym.owner=package lang
		//          Select( // sym=object scala, sym.owner=package scala, sym.tpe=object package, tpe=type, tpe.sym=object scala, tpe.sym.owner=package scala
		//            Ident("scala"), // sym=package scala, sym.owner=package <root>, sym.tpe=package scala, tpe=type, tpe.sym=package scala, tpe.sym.owner=package <root>,
		//            "package"),
		//          "RuntimeException")
		//      ),
		//      "<init>"),
		//    Nil // no argument
		//  )
		//)

		val x32 = try Integer.parseInt("5") catch { case e => 10 }
		//List(Try(
		//  Apply( // sym=method parseInt, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//    Select( // sym=method parseInt, sym.owner=object Integer, sym.tpe=(x$1: java.lang.String)Int, tpe=(x$1: java.lang.String)Int, tpe.sym=<none>
		//      Select( // sym=object Integer, sym.owner=package lang, sym.tpe=object java.lang.Integer, tpe=java.lang.Integer.type, tpe.sym=object Integer, tpe.sym.owner=package lang
		//        Select( // sym=package lang, sym.owner=package java, sym.tpe=package java.lang, tpe=java.lang.type, tpe.sym=package lang, tpe.sym.owner=package java
		//          This("java"), // sym=package java, sym.owner=package <root>, sym.tpe=package java, tpe=java.type, tpe.sym=package java, tpe.sym.owner=package <root>
		//          "lang"),
		//        "Integer"),
		//      "parseInt"),
		//    List( // 1 arguments(s)
		//      Literal(Constant(5))
		//    )
		//  )
		//    CaseDef(
		//      Bind(
		//      e
		//        Ident("_") // sym=<none>, sym.tpe=<notype>, tpe=java.lang.Throwable, tpe.sym=class Throwable, tpe.sym.owner=package lang
		//      )
		//      EmptyTree
		//      Literal(Constant(10))
		//    )
		//)
		//  EmptyTree
		//)
		var x33 = (5 * z + t ) * (t + 2)
		//Apply( // sym=method *, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//  Select( // sym=method *, sym.owner=class Int, sym.tpe=(x: Int)Int, tpe=(x: Int)Int, tpe.sym=<none>
		//    Apply( // sym=method +, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//      Select( // sym=method +, sym.owner=class Int, sym.tpe=(x: Int)Int, tpe=(x: Int)Int, tpe.sym=<none>
		//        Apply( // sym=method *, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//          Select( // sym=method *, sym.owner=class Int, sym.tpe=(x: Int)Int, tpe=(x: Int)Int, tpe.sym=<none>
		//            Literal(Constant(5)),
		//            "$times"),
		//          List( // 1 arguments(s)
		//            Ident("z") // sym=value z, sym.owner=method testMethod, sym.tpe=Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//          )
		//        ),
		//        "$plus"),
		//      List( // 1 arguments(s)
		//        Ident("t") // sym=variable t, sym.owner=method testMethod, sym.tpe=Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//      )
		//    ),
		//    "$times"),
		//  List( // 1 arguments(s)
		//    Apply( // sym=method +, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
		//      Select( // sym=method +, sym.owner=class Int, sym.tpe=(x: Int)Int, tpe=(x: Int)Int, tpe.sym=<none>
		//        Ident("t"), // sym=variable t, sym.owner=method testMethod, sym.tpe=Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala,
		//        "$plus"),
		//      List( // 1 arguments(s)
		//        Literal(Constant(2))
		//      )
		//    )
		//  )
		//)
	}

}

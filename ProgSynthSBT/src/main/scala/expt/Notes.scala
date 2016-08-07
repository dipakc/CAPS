package expt

object Notes {
//scala.reflect.generic.Trees.TermTree
//trait MyTrees extends Trees { self: Universe =>
//	trait Specification {
//		var pre: String
//		var post: String
//	}
//
//	trait MyTree extends super.Tree with Specification
//	trait MyValOrDefDef extends super.ValOrDefDef with MyMemberDef
//	//trait MyAlternative extends super.Alternative with MyValOrDefDef
//
//	case class MyApply(	override val fun: MyTree,
//						override val args: List[MyTree],
//						override var pre:String,
//						override var post:String)
//						extends super.Apply(fun, args)
//						with MyGenericApply
//
//	trait MyApplyDynamic extends super.ApplyDynamic with MyTermTree	 with	SymTree
//	trait MyArrayValue extends super.ArrayValue with MyTermTree
//	case class MyAssign(override val lhs: MyTree,
//						override val rhs: MyTree,
//						override var pre: String,
//						override var post: String)
//						extends super.Assign(lhs, rhs)
//						with MyTermTree
//	// TODO: Ident is a class. How to solve this?
//	//trait MyBackQuotedIdent extends super.BackQuotedIdent with MyIdent
//	trait MyBind extends super.Bind with MyTermTree
//
//	case class MyBlock(override val stats: List[MyTree],
//						override val expr: MyTree,
//						override var pre: String,
//						override var post: String)
//						extends super.Block(stats, expr)
//						with MyTermTree
//
//	trait MyCaseDef extends super.CaseDef with MyTree
//
//	case class MyDefDef(override val mods: Modifiers,
//						override val name: TermName,
//						override val tparams: List[MyTypeDef],
//						override val vparamss: List[List[MyValDef]],
//						override val tpt: MyTree,
//						override val rhs: MyTree,
//						override var pre: String,
//						override var post: String)
//						extends super.DefDef(mods, name, tparams, vparamss, tpt, rhs)
//						with MyValOrDefDef
//
//	trait MyDefTree extends super.DefTree with MySymTree
//	//case object MyEmptyTree extends super.EmptyTree with MyTermTree
//	trait MyFunction extends super.Function with MyTermTree	 with	SymTree
//	trait MyGenericApply extends super.GenericApply with MyTree
//
//	case class MyIdent(override val name: Name,
//						override var pre: String,
//						override var post: String)
//						extends super.Ident(name)
//						with MyRefTree
//
//	case class MyIf(override val cond: MyTree,
//					override val thenp: MyTree,
//					override val elsep: MyTree,
//					override var pre: String,
//					override var post: String)
//					extends super.If(cond, thenp, elsep)
//				with MyTermTree
//
//	case class MyLiteral(override val value: Constant,
//						override var pre: String,
//						override var post: String)
//						extends super.Literal(value) with MyTermTree
//
//	trait MyMatch extends super.Match with MyTermTree
//	trait MyMemberDef extends super.MemberDef with MyTermTree
//	trait MyNew extends super.New with MyTermTree
//	trait MyRefTree extends super.RefTree with MyTermTree	 with	SymTree
//	trait MyReturn extends super.Return with MyTermTree	 with	SymTree
//
//	case class MySelect(	override val qualifier: MyTree,
//							override val name: Name,
//							override var pre: String,
//							override var post: String)
//						extends super.Select(qualifier, name)
//								with MyRefTree
//
//	trait MyStar extends super.Star with MyTermTree
//	trait MySuper extends super.Super with MyTermTree
//	trait MySymTree extends super.SymTree with MyTermTree
//	trait MyTermTree extends super.TermTree with MyTree
//	trait MyThis extends super.This with MyTermTree
//	trait MyThrow extends super.Throw with MyTermTree
//	trait MyTry extends super.Try with MyTermTree with SymTree
//	trait MyTypeApply extends super.TypeApply with MyTermTree
//	trait MyTyped extends super.Typed with MyTermTree with SymTree
//	case class MyTypeDef(override val mods: Modifiers,
//						override val name: TypeName,
//						override val tparams: List[MyTypeDef],
//						override val rhs: MyTree,
//						override var pre: String,
//						override var post: String)
//						extends super.TypeDef(mods, name, tparams, rhs: Tree)
//						with MyMemberDef
//
//	trait MyUnApply extends super.UnApply with MyRefTree
//	case class MyValDef(override val mods: Modifiers,
//						override val name: TermName,
//						override val tpt: MyTree,
//						override val rhs: MyTree,
//						override var pre: String,
//						override var post:String)
//						extends super.ValDef(mods, name, tpt, rhs)
//						with MyValOrDefDef
//
//}
//
//class MyTreeStructures extends Universe

/* OCaml Code
type predicate_t = fol formula ;;
type assignment_t = fol formula;;

type program_ann_t = {id:int; pre: predicate_t; post: predicate_t;
											mutable program: program_t option}
and program_t =
	| IF of guardedcmd_t list
	| DO of guardedcmd_t list
	| Composition of program_ann_t * program_ann_t
	| Assignment of assignment_t
and guardedcmd_t = { guard: predicate_t; cmd: program_ann_t };;
*/


//Jar : D:\eclipseworkspace\eclipse-rcp-indigo-win32\default\pluginJar\makejar\makejar.bat
//-Xprint:ProgSynth -Yshow-trees   -Xplugin:..\pluginJar\ProgSynth.jar  -d bin\ src\localhost\Max.scala
//-Xprint:ProgSynth -Yshow-trees   -Xplugin:..\pluginJar\ProgSynth.jar  -d bin\ inputFileToCompile\Max.scala
//-Ybrowse:ProgSynth
}
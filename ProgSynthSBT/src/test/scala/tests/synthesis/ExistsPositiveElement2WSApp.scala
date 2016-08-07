package tests.synthesis
import progsynth._
import progsynth.types._
import progsynth.types.Types._
import progsynth.ProgSynth._
import progsynth.debug.FormulaParseTreeViewer
import scalaz.{ Const => SConst, _ }
import Scalaz._
import progsynth.{utils=>psu}

import progsynth.printers.XHTMLPrintersOld._
/*
object ExistsPositiveElement2WSApp extends App{
    //def updatePost(prog: ProgramAnn, formula: FOLFormula) = {

    //}
    val r = VarInt("r") //> r  : progsynth.types.Var = VarInt(r)
    val c1 = ConstInt("1") //> c1  : progsynth.types.Const = ConstInt(1)
    val c0 = ConstInt("0") //> c0  : progsynth.types.Const = ConstInt(0)
    val i = VarInt("i") //> i  : progsynth.types.Var = VarInt(i)
    val n = VarInt("n") //> n  : progsynth.types.Var = VarInt(n)
    val N = VarInt("N") //> N  : progsynth.types.Var = VarInt(N)
    val arr = VarArrayInt("arr") //> arr  : progsynth.types.Var = Var(arr,PSArrayInt)

    val ip = mkUnknownProgC {
        Invariant(None,
            ((r eqeq c1) iff Exists(i, c0 <= i && i < n && arr.select(i) > c0)) && n <= N && (N neq n),
            None)
    }(1) {
        Invariant(None,
            ((r eqeq c1) iff Exists(i, c0 <= i && i < n + c1 && arr.select(i) > c0)) && n + c1 <= N,
            None)
    }
    psu.overwriteFile("""d:\tmp\pa_1.html""", programAnnToHtmlMain(ip))
    val ip2 = ip.copyPost(
        (((r eqeq c1) iff Exists(i, c0 <= i && ((i < n) || (i eqeq c1)) && arr.select(i) > c0)) && n + c1 <= N).inv) //> ip2  : tests.synthesis.ExistsPositiveElement2WS.ip.type = UnknownProg(Invar

    val ip3 =
        mkUnknownProgC(
            pre = (((r eqeq c1) iff Exists(i, c0 <= i && i < n && arr.select(i) > c0)) && n <= N && (N neq n)).inv)(upid = 1)(
                post = (((r eqeq c1) iff Exists(i, c0 <= i && ((i < n) || (i eqeq c1)) && arr.select(i) > c0)) && n + c1 <= N).inv) //> ip3  : progsynth.types.UnknownProg = UnknownProg(Invariant(None,And(And(Iff

    ip3.post.formula.setFIdAll.toStringId

    ip3.post.formula.saveDotJpg

    val ip4 = ip3.copyPost(ip3.post.formula.splitRange(5).inv)

    val ip5 =
        mkUnknownProgC(
            pre = (((r eqeq c1) iff Exists(i, c0 <= i && i < n && arr.select(i) > c0)) && n <= N && (N neq n)).inv)(upid = 1)(
                post = (((r eqeq c1) iff (Exists(i, c0 <= i && (i < n) && arr.select(i) > c0) || Exists(i, c0 <= i && (i eqeq c1) && arr.select(i) > c0))) && n + c1 <= N).inv) //> ip5  : progsynth.types.UnknownProg = UnknownProg(Invariant(None,And(And(Iff

    ip4.post

    ip5.post
}
*/
package tests.inputprog
import progsynth.spec.StaticAssertions._
import progsynth.spec.PredicateDefsTrait
import progsynth.utils.folformulautils.BoolToFormula._
import progsynth.types._
import progsynth.types.Types._
//import scalaz.BooleanW
//import scalaz.Scalaz._
//import scalaz.Booleans
import progsynth.spec.StaticAssertions.@@
import progsynth.spec.StaticAssertions.@@._

object UserDefinedPredicate extends App {
	//"src/main/scala/progsynth/spec/StaticAssertions.scala"


	object xyz extends PredicateDefsTrait {
		def Sorted(arr: Array[Int], m: Int, n: Int) = definePredicate { /*(i: Int, j: Int) =>
			∀(i)∀(j)∘(m <= i && i < n && i <= j && m <= j && j < n impl arr(i) <= arr(j))*/
			TrueF
		}

		//arr[m] is the minimum of the of arr[p..q]
		def minElem(m: Int, arr: Array[Int], p: Int, q: Int) = definePredicate { /*(i: Int) =>
			∀(i)∘(p <= i && i < q impl arr(m) <= arr(i))*/
			TrueF
		}
	}
	import xyz._

	def bubbleSort(arr: Array[Int], N: Int): Array[Int] =
	sRequire(N > 0){
		var n = 0
		sLoopInv{(i:Int, j:Int)=>
			0 <= n && n <= N && N > 0 &&
			∀(i)∘(0 <= i && i < n impl minElem(i, arr, i, N))
		}
		while(n < N){ // n != N
			var k = N - 1
			sLoopInv{(i:Int, j:Int)=>
				 	0 <= n && n <= k && k < N && N > 0 &&
					∀(i)∘(0 <= i && i < n impl minElem(i, arr, i, N)) &&
				    minElem(k, arr, k, n)
			}
			while(k > n){ // k != n
				if (arr(k-1) > arr(k)){
					val tmp = arr(k)
					arr(k) = arr(k-1)
					arr(k-1) = tmp
				}
				k = k - 1
			}
			n = n + 1
		}
		arr
	} sEnsuring{ (arr) => () => Sorted(arr, 0, N) }

}
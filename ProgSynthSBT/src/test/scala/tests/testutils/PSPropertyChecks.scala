package tests.testutils

import org.scalacheck.{Properties, Gen, Arbitrary, Shrink}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.prop.PropertyChecks

trait PSPropertyChecks extends PropertyChecks {
	def forAllWithShrink[A](genA: Gen[A], shrA: org.scalacheck.Shrink[A],
				   configParams: PropertyCheckConfigParam*)
				   (fun: A => Unit)
				   (implicit config: PropertyCheckConfig):Unit ={
		forAll(genA, configParams:_*)(fun)(config, shrA)
	}
}

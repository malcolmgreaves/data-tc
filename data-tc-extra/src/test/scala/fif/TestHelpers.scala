package fif

import algebra.Semigroup

object TestHelpers {

  implicit val sg = new Semigroup[Int] {
    override def combine(a: Int, b: Int) = a + b
  }

}

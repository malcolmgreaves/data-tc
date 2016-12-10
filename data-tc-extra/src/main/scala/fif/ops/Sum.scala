package fif.ops

import fif.Data

import scala.language.higherKinds
import scala.reflect.ClassTag

object Sum extends Serializable {

  import Data.ops._

  def apply[N: Numeric: ClassTag, D[_]: Data](data: D[N]): N = {
    val add = implicitly[Numeric[N]].plus _
    data.aggregate(implicitly[Numeric[N]].zero)(add, add)
  }

  def apply[N: Numeric](first: N, numbers: N*): N = {
    val add = implicitly[Numeric[N]].plus _
    numbers.foldLeft(first)(add)
  }

}

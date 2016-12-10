package fif

import scala.language.higherKinds

class ArrayDataTest extends CollectionDataTest[Array] {

  override implicit val dataTypeClass: Data[Array] =
    ArrayData

  override val data = Array(1, 2, 3)

  override val empty = Array.empty[Int]

}
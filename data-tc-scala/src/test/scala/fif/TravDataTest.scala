package fif

import scala.language.higherKinds

class TravDataTest extends CollectionDataTest[Traversable] {

  override implicit val dataTypeClass: Data[Traversable] =
    TravData

  override val data = Traversable(1, 2, 3)

  override val empty = Traversable.empty[Int]

}
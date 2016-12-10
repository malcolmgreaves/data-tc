package fif

import scala.language.higherKinds

class SeqDataTest extends CollectionDataTest[Seq] {

  override implicit val dataTypeClass: Data[Seq] =
    SeqData

  override val data = Seq(1, 2, 3)

  override val empty = Seq.empty[Int]

}
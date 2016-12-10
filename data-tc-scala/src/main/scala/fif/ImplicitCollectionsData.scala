package fif

object ImplicitCollectionsData {

  implicit val traversableIsData: Data[Traversable] = TravData

  implicit val seqIsData: Data[Seq] = SeqData

  implicit val arrayIsData: Data[Array] = ArrayData
}

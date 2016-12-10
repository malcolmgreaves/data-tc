package fif

import org.apache.spark.rdd.RDD

object ImplicitRddData extends Serializable {

  implicit val rddIsData: Data[RDD] = RddData

}
package fif.spark

import org.scalatest.FunSuite

/**
  * Tests KryoSerializationWrapper's correctness and attempts
  * to test for presence of race conditions.
  */
class KryoSerializationTest extends FunSuite {

  test("test simple Kryo serialization with wrapper class") {

    val serialized = KryoSerializationWrapper(KryoSerializationTest)

    serialized.getValue.dumbData.foreach(x => {
      val ba = serialized.getValue.foo(x)
      val serBa = KryoSerializationWrapper(ba)
      assert(ba == serBa.getValue)
    })
  }
}

object KryoSerializationTest {

  val dumbData = RddSerializedOpsTest.dumbData

  class BadApple(val x: String)

  def foo(x: String) =
    new BadApple("foo foo!")

}

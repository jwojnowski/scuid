package me.wojnowski.scuid.circe

import io.circe.syntax.*
import me.wojnowski.scuid.Cuid2
import me.wojnowski.scuid.Cuid2Custom
import me.wojnowski.scuid.Cuid2Long
import munit.ScalaCheckSuite
import org.scalacheck.Gen
import org.scalacheck.Prop.forAll
import org.scalacheck.Test

class CodecsTest extends ScalaCheckSuite {
  override protected def scalaCheckTestParameters: Test.Parameters =
    super.scalaCheckTestParameters.withMinSuccessfulTests(200)

  test("Cuid2 encoding/decoding (success)") {
    forAll(cuid2Gen(24)) { rawCuid2 =>
      val cuid2 = Cuid2.validate(rawCuid2).get
      assertEquals(cuid2.asJson, rawCuid2.asJson)
      assertEquals(rawCuid2.asJson.as[Cuid2], Right(cuid2))
    }
  }

  test("Cuid2 decoding (failure)") {
    forAll(cuid2GenRandomLengthButNot(24)) { rawInvalidCuid2 =>
      assert(rawInvalidCuid2.asJson.as[Cuid2].isLeft)
    }
  }

  test("Cuid2Long encoding/decoding (success)") {
    forAll(cuid2Gen(32)) { rawCuid2 =>
      val cuid2 = Cuid2Long.validate(rawCuid2).get
      assertEquals(cuid2.asJson, rawCuid2.asJson)
      assertEquals(rawCuid2.asJson.as[Cuid2Long], Right(cuid2))
    }
  }

  test("Cuid2Long decoding (failure)") {
    forAll(cuid2GenRandomLengthButNot(32)) { rawInvalidCuid2 =>
      assert(rawInvalidCuid2.asJson.as[Cuid2Long].isLeft)
    }
  }

  test("Cuid2Custom encoding/decoding (success)") {
    forAll(cuid2Gen(27)) { rawCuid2 =>
      val cuid2 = Cuid2Custom.validate[27](rawCuid2).get
      assertEquals(cuid2.asJson, rawCuid2.asJson)
      assertEquals(rawCuid2.asJson.as[Cuid2Custom[27]], Right(cuid2))
    }
  }

  test("Cuid2Custom decoding (failure)") {
    forAll(cuid2GenRandomLengthButNot(27)) { rawInvalidCuid2 =>
      assert(rawInvalidCuid2.asJson.as[Cuid2Custom[27]].isLeft)
    }
  }

  private def cuid2Gen(length: Int): Gen[String] =
    for {
      prefix <- Gen.alphaLowerChar
      suffix <- Gen.stringOfN(length - 1, Gen.oneOf(Gen.alphaLowerChar, Gen.numChar))
    } yield s"$prefix$suffix"

  private def cuid2GenRandomLengthButNot(length: Int): Gen[String] =
    Gen.chooseNum(4, 36).retryUntil(_ != length).flatMap { length =>
      cuid2Gen(length)
    }

}

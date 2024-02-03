/*
 * Copyright (c) 2023 Jakub Wojnowski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.wojnowski.scuid.circe

import me.wojnowski.scuid.Cuid2
import me.wojnowski.scuid.Cuid2Custom
import me.wojnowski.scuid.Cuid2Long
import me.wojnowski.scuid.Generators

import org.scalacheck.Prop.forAll
import org.scalacheck.Test

import io.circe.syntax.*
import munit.ScalaCheckSuite

class CodecsTest extends ScalaCheckSuite with Generators {
  override protected def scalaCheckTestParameters: Test.Parameters =
    super.scalaCheckTestParameters.withMinSuccessfulTests(200)

  test("Cuid2 encoding/decoding (success)") {
    forAll(validRawCuidGen(24)) { rawCuid2 =>
      val cuid2 = Cuid2.unsafeFrom(rawCuid2)
      assertEquals(cuid2.asJson, rawCuid2.asJson)
      assertEquals(rawCuid2.asJson.as[Cuid2], Right(cuid2))
    }
  }

  test("Cuid2 decoding (failure)") {
    forAll(validRawCuid2GenRandomLengthButNot(24)) { rawInvalidCuid2 =>
      assert(rawInvalidCuid2.asJson.as[Cuid2].isLeft)
    }
  }

  test("Cuid2Long encoding/decoding (success)") {
    forAll(validRawCuidGen(32)) { rawCuid2 =>
      val cuid2 = Cuid2Long.unsafeFrom(rawCuid2)
      assertEquals(cuid2.asJson, rawCuid2.asJson)
      assertEquals(rawCuid2.asJson.as[Cuid2Long], Right(cuid2))
    }
  }

  test("Cuid2Long decoding (failure)") {
    forAll(validRawCuid2GenRandomLengthButNot(32)) { rawInvalidCuid2 =>
      assert(rawInvalidCuid2.asJson.as[Cuid2Long].isLeft)
    }
  }

  test("Cuid2Custom encoding/decoding (success)") {
    forAll(validRawCuidGen(27)) { rawCuid2 =>
      val cuid2 = Cuid2Custom.unsafeFrom[27](rawCuid2)
      assertEquals(cuid2.asJson, rawCuid2.asJson)
      assertEquals(rawCuid2.asJson.as[Cuid2Custom[27]], Right(cuid2))
    }
  }

  test("Cuid2Custom decoding (failure)") {
    forAll(validRawCuid2GenRandomLengthButNot(27)) { rawInvalidCuid2 =>
      assert(rawInvalidCuid2.asJson.as[Cuid2Custom[27]].isLeft)
    }
  }

}

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

package me.wojnowski.scuid.tapir

import me.wojnowski.scuid.Cuid2
import me.wojnowski.scuid.Cuid2Custom
import me.wojnowski.scuid.Cuid2Long
import me.wojnowski.scuid.Generators

import org.scalacheck.Prop.forAll
import org.scalacheck.Test

import scala.annotation.unused

import munit.ScalaCheckSuite
import sttp.tapir.Codec.PlainCodec
import sttp.tapir.DecodeResult

class TapirCodecTest extends ScalaCheckSuite with Generators {
  override protected def scalaCheckTestParameters: Test.Parameters =
    super.scalaCheckTestParameters.withMinSuccessfulTests(200)

  test("Cuid2 encoding/decoding (success)") {
    forAll(validRawCuidGen(24)) { rawCuid2 =>
      val cuid2 = Cuid2.unsafeFrom(rawCuid2)

      assertEquals(implicitly[PlainCodec[Cuid2]].encode(cuid2), rawCuid2)
      assertEquals(implicitly[PlainCodec[Cuid2]].decode(rawCuid2), DecodeResult.Value(cuid2))
    }
  }

  test("Cuid2 decoding (failure)") {
    forAll(validRawCuid2GenRandomLengthButNot(24)) { rawInvalidCuid2 =>
      assert(implicitly[PlainCodec[Cuid2]].decode(rawInvalidCuid2).isInstanceOf[DecodeResult.Failure])
    }
  }

  test("Cuid2Long encoding/decoding (success)") {
    forAll(validRawCuidGen(32)) { rawCuid2 =>
      val cuid2 = Cuid2Long.unsafeFrom(rawCuid2)
      assertEquals(implicitly[PlainCodec[Cuid2Long]].encode(cuid2), rawCuid2)
      assertEquals(implicitly[PlainCodec[Cuid2Long]].decode(rawCuid2), DecodeResult.Value(cuid2))
    }
  }

  test("Cuid2Long decoding (failure)") {
    forAll(validRawCuid2GenRandomLengthButNot(32)) { rawInvalidCuid2 =>
      assert(implicitly[PlainCodec[Cuid2Long]].decode(rawInvalidCuid2).isInstanceOf[DecodeResult.Failure])
    }
  }

  test("Cuid2Custom encoding/decoding (success)") {
    forAll(validRawCuidGen(27)) { rawCuid2 =>
      val cuid2 = Cuid2Custom.unsafeFrom[27](rawCuid2)
      assertEquals(implicitly[PlainCodec[Cuid2Custom[27]]].encode(cuid2), rawCuid2)
      assertEquals(implicitly[PlainCodec[Cuid2Custom[27]]].decode(rawCuid2), DecodeResult.Value(cuid2))
    }
  }

  test("Cuid2Custom decoding (failure)") {
    forAll(validRawCuid2GenRandomLengthButNot(27)) { rawInvalidCuid2 =>
      assert(implicitly[PlainCodec[Cuid2Custom[27]]].decode(rawInvalidCuid2).isInstanceOf[DecodeResult.Failure])
    }
  }

  test("Cuid2 used in an endpoint compiles") {
    import sttp.tapir.*

    @unused("just needs to compile")
    val schema1 = implicitly[Schema[Cuid2]]

    @unused("just needs to compile")
    val schema2 = implicitly[Schema[Cuid2Long]]

    @unused("just needs to compile")
    val schema3 = implicitly[Schema[Cuid2Custom[27]]]

    @unused("just needs to compile")
    val cuid2Endpoint: Endpoint[Unit, (Cuid2, Cuid2Long, Cuid2Custom[27]), Unit, Cuid2, Any] =
      endpoint
        .get
        .in("cuid24" / path[Cuid2] / "cuid32" / path[Cuid2Long] / "cuid27" / path[Cuid2Custom[27]])
        .out(plainBody[Cuid2])
  }

}

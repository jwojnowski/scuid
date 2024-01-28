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

package me.wojnowski.scuid

import me.wojnowski.scuid.Cuid2ValidationTest.*

import org.scalacheck.Gen
import org.scalacheck.Prop

import munit.ScalaCheckSuite

class Cuid2ValidationTest extends ScalaCheckSuite {
  property("Validate - succeeds for valid CUID2") {
    Prop.forAll(validRawCuid(27)) { rawCuid =>
      assertEquals(Cuid2Custom.validate[27](rawCuid).map(_.render), Some(rawCuid))
    }
  }

  property("Validate - fails with non-lowercase first character") {
    Prop.forAll(mkRawCuid(nonLowercaseGen, lowercaseAlphanumGen, 27)) { rawCuid =>
      assertEquals(Cuid2Custom.validate[27](rawCuid), None)
    }
  }

  property("Validate - fails with non-lowercase alphanum characters following the letter") {
    Prop.forAll(mkRawCuid(lowercaseGen, nonLowercaseAlphanumGen, 27)) { rawCuid =>
      assertEquals(Cuid2Custom.validate[27](rawCuid), None)
    }
  }

  test("Validate - fails with incorrect length") {
    Prop.forAll(Gen.chooseNum(4, 64).suchThat(_ != 27).flatMap(length => validRawCuid(length))) { rawCuid =>
      assertEquals(Cuid2Custom.validate[27](rawCuid), None)
    }
  }

}

object Cuid2ValidationTest {
  private val printableChars = 32.toChar to 126.toChar

  val lowercaseGen: Gen[Char] = Gen.alphaLowerChar

  val nonLowercaseGen: Gen[Char] = Gen.oneOf(printableChars.filterNot(_.isLower))

  val lowercaseAlphanumGen: Gen[Char] = Gen.oneOf(lowercaseGen, Gen.numChar)

  val nonLowercaseAlphanumGen: Gen[Char] = Gen.oneOf(printableChars.filterNot(_.isLower).filterNot(_.isDigit))

  def validRawCuid(length: Int): Gen[String] = mkRawCuid(lowercaseGen, lowercaseAlphanumGen, length)

  def mkRawCuid(firstChar: Gen[Char], otherChars: Gen[Char], length: Int): Gen[String] =
    for {
      firstLetter <- firstChar
      otherChars  <- Gen.stringOfN(length - 1, otherChars)
    } yield s"$firstLetter$otherChars"

}

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

import org.scalacheck.Gen

trait Generators {
  private val printableChars = 32.toChar to 126.toChar

  val lowercaseGen: Gen[Char] = Gen.alphaLowerChar

  val nonLowercaseGen: Gen[Char] = Gen.oneOf(printableChars.filterNot(_.isLower))

  val lowercaseAlphanumGen: Gen[Char] = Gen.oneOf(lowercaseGen, Gen.numChar)

  val nonLowercaseAlphanumGen: Gen[Char] = Gen.oneOf(printableChars.filterNot(_.isLower).filterNot(_.isDigit))

  def validRawCuidGen(length: Int): Gen[String] = rawCuidLikeGen(lowercaseGen, lowercaseAlphanumGen, length)

  def rawCuidLikeGen(firstChar: Gen[Char], otherChars: Gen[Char], length: Int): Gen[String] =
    for {
      firstLetter <- firstChar
      otherChars  <- Gen.stringOfN(length - 1, otherChars)
    } yield s"$firstLetter$otherChars"

  def validRawCuid2GenRandomLengthButNot(length: Int): Gen[String] =
    Gen.chooseNum(4, 36).retryUntil(_ != length).flatMap { length =>
      validRawCuidGen(length)
    }

}

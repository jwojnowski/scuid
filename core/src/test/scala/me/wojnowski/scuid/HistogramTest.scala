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

import cats.effect.IO

import munit.CatsEffectSuite

class HistogramTest extends CatsEffectSuite {
  test("Character histogram") {
    val numberOfCuids        = 10000
    val tolerance            = 0.1
    val numberOfAllowedChars = 36

    val idLength = 24

    val expectedBinSize = Math.ceil(idLength * numberOfCuids / numberOfAllowedChars.toDouble).toInt
    val expectedMin     = expectedBinSize - (expectedBinSize * tolerance).toInt
    val expectedMax     = expectedBinSize + (expectedBinSize * tolerance).toInt

    Cuid2Gen
      .default[IO]
      .flatMap { generator =>
        generator.generate.replicateA(numberOfCuids)
      }
      .map { cuids =>
        val combined                  = cuids.map(_.render).mkString
        val histogram: Map[Char, Int] = combined.groupBy(identity).view.mapValues(_.length).toMap

        histogram.foreach { case (char, count) =>
          assert(count >= expectedMin && count <= expectedMax, s"Char: [$char] should be between $expectedMin and $expectedMax")
        }

      }

  }
}

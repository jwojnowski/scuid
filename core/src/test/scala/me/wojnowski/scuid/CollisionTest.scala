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

import fs2.Stream

import cats.effect.IO

import scala.concurrent.duration.Duration
import scala.concurrent.duration.DurationInt

import munit.CatsEffectSuite

class CollisionTest extends CatsEffectSuite {

  override def munitTimeout: Duration = 10.minutes

  test("Check for collisions") {
    val parallelism = Runtime.getRuntime.availableProcessors
    val numberOfIds = (Math.pow(7, 8) * 2).toLong

    Cuid2Gen.default[IO].flatMap { cuid2Ops =>
      Stream
        .emit(())
        .covary[IO]
        .repeatN(numberOfIds)
        .parEvalMapUnordered(parallelism)(_ => cuid2Ops.generate)
        .compile
        .toList
        .map { cuidList =>
          val cuidSet = cuidList.toSet

          val duplicateCount = cuidList.length - cuidSet.size

          assertEquals(duplicateCount, 0)
        }
    }
  }
}

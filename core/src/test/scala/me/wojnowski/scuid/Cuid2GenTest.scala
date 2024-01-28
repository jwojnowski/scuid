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

import org.scalacheck.Gen
import org.scalacheck.effect.PropF

import java.util.concurrent.atomic.AtomicLong

import munit.CatsEffectSuite
import munit.ScalaCheckEffectSuite

class Cuid2GenTest extends CatsEffectSuite with ScalaCheckEffectSuite {
  val generator: Cuid2Gen[IO] = Cuid2Gen.default[IO].unsafeRunSync()

  test("Generate - Starts with a lowercase letter") {
    PropF.forAllF(Gen.const(())) { _ =>
      generator.generate.map(cuid => assert(cuid.render.head.isLower))
    }
  }

  test("Generate - Consists of lowercase letters and numbers") {
    PropF.forAllF(Gen.const(())) { _ =>
      generator.generate.map(cuid => assert(cuid.render.forall(char => char.isLower || char.isDigit)))
    }
  }

  test("Generate - Length is consistent (default)") {
    PropF.forAllF(Gen.const(())) { _ =>
      generator.generate.map(cuid => assertEquals(cuid.render.length, 24))
    }
  }

  test("Generate - Length is consistent (long)") {
    PropF.forAllF(Gen.const(())) { _ =>
      generator.generateLong.map(cuid => assertEquals(cuid.render.length, 32))
    }
  }

  test("Generate - Length is consistent (custom length - shorter)") {
    PropF.forAllF(Gen.const(())) { _ =>
      generator.generateCustomLength[10].map(cuid => assertEquals(cuid.render.length, 10))
    }
  }

  test("Generate - Length is consistent (custom length - between)") {
    PropF.forAllF(Gen.const(())) { _ =>
      generator.generateCustomLength[27].map(cuid => assertEquals(cuid.render.length, 27))
    }
  }

  test("counter is updated") {
    val atomicLong = new AtomicLong(0L)
    val counter    = () => IO(atomicLong.incrementAndGet())

    val ops = Cuid2Gen.custom[IO]("fingerprint", () => IO(0.5), counter, clock = () => IO(0L))

    ops.generate.replicateA(3).map { _ =>
      assertEquals(atomicLong.get(), 3L)
    }
  }
}

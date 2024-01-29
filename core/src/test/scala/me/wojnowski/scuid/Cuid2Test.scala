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

import cats.data.NonEmptyList
import cats.implicits._

import munit.ScalaCheckSuite

// TODO property-based testing?
class Cuid2Test extends ScalaCheckSuite {

  test("Cuid2 - equals (same)") {
    val cuidA = Cuid2.validate("ynl1pm6yt50t452nb9n14ick").get
    val cuidB = Cuid2.validate("ynl1pm6yt50t452nb9n14ick").get

    assert(cuidA == cuidB)
  }

  test("Cuid2 - equals (different)") {
    val cuidA = Cuid2.validate("ynl1pm6yt50t452nb9n14ick").get
    val cuidB = Cuid2.validate("x1oqtldjj6gdk4eewnc589hh").get

    assert(cuidA != cuidB)
  }

  test("Cuid2 - Eq (same)") {
    val cuidA = Cuid2.validate("cnfmhdzvebldt1js6tca962v").get
    val cuidB = Cuid2.validate("cnfmhdzvebldt1js6tca962v").get

    assert(cuidA === cuidB)
  }

  test("Cuid2 - Eq (different)") {

    val cuidA = Cuid2.validate("cnfmhdzvebldt1js6tca962v").get
    val cuidB = Cuid2.validate("x1oqtldjj6gdk4eewnc589hh").get

    assert(cuidA =!= cuidB)
  }

  test("Cuid2 - Order") {

    val cuidA = Cuid2.validate("cnfmhdzvebldt1js6tca962v").get
    val cuidB = Cuid2.validate("x1oqtldjj6gdk4eewnc589hh").get
    val cuidC = Cuid2.validate("ynl1pm6yt50t452nb9n14ick").get

    assertEquals(NonEmptyList.of(cuidC, cuidB, cuidA).sorted, NonEmptyList.of(cuidA, cuidB, cuidC))
  }

  test("Cuid2 - Show") {
    val cuidA = Cuid2.validate("cnfmhdzvebldt1js6tca962v").get
    assertEquals(cuidA.show, "cnfmhdzvebldt1js6tca962v")
  }

  test("Cuid2 - toString") {
    val cuidA = Cuid2.validate("cnfmhdzvebldt1js6tca962v").get
    assertEquals(cuidA.toString, "cnfmhdzvebldt1js6tca962v")
  }

  test("Cuid2Long - equals (same)") {
    val cuidA = Cuid2Long.validate("ynl1pm6yt50t452nb9n14ickjt438ius").get
    val cuidB = Cuid2Long.validate("ynl1pm6yt50t452nb9n14ickjt438ius").get

    assert(cuidA == cuidB)
  }

  test("Cuid2Long - equals (different)") {
    val cuidA = Cuid2Long.validate("ynl1pm6yt50t452nb9n14ickjt438ius").get
    val cuidB = Cuid2Long.validate("x1oqtldjj6gdk4eewnc589hhrk6btxxr").get

    assert(cuidA != cuidB)
  }

  test("Cuid2Long - Eq (same)") {
    val cuidA = Cuid2Long.validate("cnfmhdzvebldt1js6tca962vx3hjxivs").get
    val cuidB = Cuid2Long.validate("cnfmhdzvebldt1js6tca962vx3hjxivs").get

    assert(cuidA === cuidB)
  }

  test("Cuid2Long - Eq (different)") {

    val cuidA = Cuid2Long.validate("cnfmhdzvebldt1js6tca962vx3hjxivs").get
    val cuidB = Cuid2Long.validate("x1oqtldjj6gdk4eewnc589hhrk6btxxr").get

    assert(cuidA =!= cuidB)
  }

  test("Cuid2Long - Order") {

    val cuidA = Cuid2Long.validate("cnfmhdzvebldt1js6tca962vx3hjxivs").get
    val cuidB = Cuid2Long.validate("x1oqtldjj6gdk4eewnc589hhrk6btxxr").get
    val cuidC = Cuid2Long.validate("ynl1pm6yt50t452nb9n14ickjt438ius").get

    assertEquals(NonEmptyList.of(cuidC, cuidB, cuidA).sorted, NonEmptyList.of(cuidA, cuidB, cuidC))
  }

  test("Cuid2Long - Show") {
    val cuidA = Cuid2Long.validate("cnfmhdzvebldt1js6tca962vx3hjxivs").get
    assertEquals(cuidA.show, "cnfmhdzvebldt1js6tca962vx3hjxivs")
  }

  test("Cuid2Long - toString") {
    val cuidA = Cuid2Long.validate("cnfmhdzvebldt1js6tca962vx3hjxivs").get
    assertEquals(cuidA.toString, "cnfmhdzvebldt1js6tca962vx3hjxivs")
  }

  test("Cuid2Custom - equals (same)") {
    val cuidA = Cuid2Custom.validate[10]("ynl1pm6yt5").get
    val cuidB = Cuid2Custom.validate[10]("ynl1pm6yt5").get

    assert(cuidA == cuidB)
  }

  test("Cuid2Custom - equals (different)") {
    val cuidA = Cuid2Custom.validate[10]("ynl1pm6yt5").get
    val cuidB = Cuid2Custom.validate[10]("x1oqtldjj6").get

    assert(cuidA != cuidB)
  }

  test("Cuid2Custom - Eq (same)") {
    val cuidA = Cuid2Custom.validate[10]("cnfmhdzveb").get
    val cuidB = Cuid2Custom.validate[10]("cnfmhdzveb").get

    assert(cuidA === cuidB)
  }

  test("Cuid2Custom - Eq (different)") {

    val cuidA = Cuid2Custom.validate[10]("cnfmhdzveb").get
    val cuidB = Cuid2Custom.validate[10]("x1oqtldjj6").get

    assert(cuidA =!= cuidB)
  }

  test("Cuid2Custom - Order") {

    val cuidA = Cuid2Custom.validate[10]("cnfmhdzveb").get
    val cuidB = Cuid2Custom.validate[10]("x1oqtldjj6").get
    val cuidC = Cuid2Custom.validate[10]("ynl1pm6yt5").get

    assertEquals(NonEmptyList.of(cuidC, cuidB, cuidA).sorted, NonEmptyList.of(cuidA, cuidB, cuidC))
  }

  test("Cuid2Custom - Show") {
    val cuidA = Cuid2Custom.validate[10]("cnfmhdzveb").get
    assertEquals(cuidA.show, "cnfmhdzveb")
  }

  test("Cuid2Custom - toString") {
    val cuidA = Cuid2Custom.validate[10]("cnfmhdzveb").get
    assertEquals(cuidA.toString, "cnfmhdzveb")
  }

}

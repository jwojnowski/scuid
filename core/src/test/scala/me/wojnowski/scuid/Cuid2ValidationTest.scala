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
import org.scalacheck.Prop

import munit.ScalaCheckSuite

class Cuid2ValidationTest extends ScalaCheckSuite with Generators {
  property("Validate - succeeds for valid CUID2") {
    Prop.forAll(validRawCuidGen(27)) { rawCuid =>
      assertEquals(Cuid2Custom.validate[27](rawCuid).map(_.render), Right(rawCuid))
    }
  }

  property("Validate - fails with non-lowercase first character") {
    Prop.forAll(rawCuidLikeGen(nonLowercaseGen, lowercaseAlphanumGen, 27)) { rawCuid =>
      assertEquals(Cuid2Custom.validate[27](rawCuid), Left(ValidationError.InvalidFirstCharacter(rawCuid.head, rawCuid)))
    }
  }

  property("Validate - fails with non-lowercase alphanum characters following the letter") {
    Prop.forAll(rawCuidLikeGen(lowercaseGen, nonLowercaseAlphanumGen, 27)) { rawCuid =>
      assert(Cuid2Custom.validate[27](rawCuid).left.exists(_.isInstanceOf[ValidationError.InvalidCharacters]))
    }
  }

  test("Validate - fails with incorrect length") {
    Prop.forAll(Gen.chooseNum(4, 64).suchThat(_ != 27).flatMap(length => validRawCuidGen(length))) { rawCuid =>
      assertEquals(Cuid2Custom.validate[27](rawCuid), Left(ValidationError.WrongLength(27, rawCuid.length, rawCuid)))
    }
  }

}

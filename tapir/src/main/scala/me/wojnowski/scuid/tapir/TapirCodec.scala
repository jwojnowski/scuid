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

import sttp.tapir.Codec
import sttp.tapir.Codec.PlainCodec
import sttp.tapir.Schema

trait TapirCodec {
  implicit val schemaForCuid2: Schema[Cuid2] = Schema.string.format("Cuid2 (length 24)")

  implicit val codecForCuid2: PlainCodec[Cuid2] =
    Codec.string.mapEither(Cuid2.validate(_).toRight("Invalid Cuid2 (length 24)"))(_.render)

  implicit val schemaForCuid2Long: Schema[Cuid2Long] = Schema.string.format("Cuid2 (length 24)")

  implicit val codecForCuid2Long: PlainCodec[Cuid2Long] =
    Codec.string.mapEither(Cuid2Long.validate(_).toRight("Invalid Cuid2 (length 24)"))(_.render)

  implicit def schemaForCuid2Custom[L <: Int](implicit L: ValueOf[L]): Schema[Cuid2Custom[L]] =
    Schema.string.format(s"Cuid2 (length ${L.value})")

  implicit def codecForCuid2Custom[L <: Int](implicit L: ValueOf[L]): PlainCodec[Cuid2Custom[L]] =
    Codec.string.mapEither(Cuid2Custom.validate[L](_).toRight(s"Invalid Cuid2 (length ${L.value})"))(_.render)
}

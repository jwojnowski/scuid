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

import io.circe.Decoder
import io.circe.Encoder

trait Codecs {
  implicit val cuid2Encoder: Encoder[Cuid2]                          = Encoder[String].contramap(_.render)
  implicit val cuid2LongEncoder: Encoder[Cuid2Long]                  = Encoder[String].contramap(_.render)
  implicit def cuid2CustomEncoder[L <: Int]: Encoder[Cuid2Custom[L]] = Encoder[String].contramap(_.render)

  implicit val cuid2Decoder: Decoder[Cuid2] =
    Decoder[String].emap(Cuid2.validate(_).toRight("Invalid Cuid2 (length 24)"))

  implicit val cuid2LongDecoder: Decoder[Cuid2Long] =
    Decoder[String].emap(Cuid2Long.validate(_).toRight("Invalid Cuid2 (length 32)"))

  implicit def cuid2CustomDecoder[L <: Int](implicit L: ValueOf[L]): Decoder[Cuid2Custom[L]] =
    Decoder[String].emap(Cuid2Custom.validate(_).toRight(s"Invalid Cuid2 (length ${L.value})"))
}

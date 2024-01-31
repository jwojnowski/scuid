package me.wojnowski.scuid.circe

import io.circe.Decoder
import io.circe.Encoder
import me.wojnowski.scuid.Cuid2
import me.wojnowski.scuid.Cuid2Custom
import me.wojnowski.scuid.Cuid2Long

trait Codecs {
  implicit val cuid2Encoder: Encoder[Cuid2]                     = Encoder[String].contramap(_.render)
  implicit val cuid2LongEncoder: Encoder[Cuid2Long]             = Encoder[String].contramap(_.render)
  implicit def cuid2CustomEncoder[L <: Int]: Encoder[Cuid2Custom[L]] = Encoder[String].contramap(_.render)

  implicit val cuid2Decoder: Decoder[Cuid2] =
    Decoder[String].emap(Cuid2.validate(_).toRight("Invalid Cuid2 (length 24)"))

  implicit val cuid2LongDecoder: Decoder[Cuid2Long] =
    Decoder[String].emap(Cuid2Long.validate(_).toRight("Invalid Cuid2 (length 32)"))

  implicit def cuid2CustomDecoder[L <: Int](implicit L: ValueOf[L]): Decoder[Cuid2Custom[L]] =
    Decoder[String].emap(Cuid2Custom.validate(_).toRight(s"Invalid Cuid2 (length ${L.value})"))
}

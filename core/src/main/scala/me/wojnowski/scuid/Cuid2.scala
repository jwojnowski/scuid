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

import me.wojnowski.scuid.Cuid2Custom.isValid

import cats.Id
import cats.Monad
import cats.Order
import cats.Show
import cats.effect.Clock
import cats.effect.Sync
import cats.effect.std.SecureRandom
import cats.implicits.*

import scala.jdk.CollectionConverters.MapHasAsScala

import java.security.SecureRandom as JavaSecureRandom
import java.security.MessageDigest
import java.util.concurrent.atomic.AtomicLong

abstract sealed class Cuid2Custom[L <: Int](private val value: String)(implicit lengthValueOf: ValueOf[L]) {
  def length: Int = lengthValueOf.value

  def render: String = value

  override def toString: String = render

  override def equals(other: Any): Boolean = other match {
    case that: Cuid2Custom[_] => value == that.value
    case _                    => false
  }

  override def hashCode(): Int = value.hashCode
}

abstract sealed case class Cuid2(private val value: String)     extends Cuid2Custom[24](value) {}
abstract sealed case class Cuid2Long(private val value: String) extends Cuid2Custom[32](value) {}

object Cuid2 {
  def validate(rawString: String): Option[Cuid2] =
    Option.when(isValid[24](rawString))(new Cuid2(rawString) {})

  implicit val order: Order[Cuid2]       = Order.by(_.value)
  implicit val ordering: Ordering[Cuid2] = order.toOrdering
  implicit val show: Show[Cuid2]         = Show.show(_.value)
}

object Cuid2Long {
  def validate(rawString: String): Option[Cuid2Long] =
    Option.when(isValid[32](rawString))(new Cuid2Long(rawString) {})

  implicit val order: Order[Cuid2Long]       = Order.by(_.value)
  implicit val ordering: Ordering[Cuid2Long] = order.toOrdering
  implicit val show: Show[Cuid2Long]         = Show.show(_.value)
}

object Cuid2Custom {
  implicit def order[L <: Int]: Order[Cuid2Custom[L]]       = Order.by(_.value)
  implicit def ordering[L <: Int]: Ordering[Cuid2Custom[L]] = order[L].toOrdering
  implicit def show[L <: Int]: Show[Cuid2Custom[L]]         = Show.show(_.value)

  def validate[L <: Int: ValueOf](rawString: String): Option[Cuid2Custom[L]] =
    Option.when(isValid[L](rawString))(new Cuid2Custom(rawString) {})

  private[scuid] def isValid[L <: Int: ValueOf](rawString: String): Boolean =
    List[String => Boolean](
      _.headOption.forall(_.isLower),
      _.length === valueOf[L],
      _.drop(1).forall(char => char.isLower || char.isDigit)
    ).forall(_.apply(rawString))

}

trait Cuid2Gen[F[_]] {
  def generate: F[Cuid2]
  def generateLong: F[Cuid2Long]
  def generateCustomLength[L <: Int: ValueOf]: F[Cuid2Custom[L]]
}

object Cuid2Gen {

  private val letters = "abcdefghijklmnopqrstuvwxyz"

  private val BigLength = 32

  private type RandomDouble[F[_]] = () => F[Double]

  private type Counter[F[_]] = () => F[Long]

  private type ClockMillis[F[_]] = () => F[Long]

  def apply[F[_]](implicit gen: Cuid2Gen[F]): Cuid2Gen[F] = gen

  def default[F[_]: Sync]: F[Cuid2Gen[F]] =
    for {
      secureRandom                 <- SecureRandom.javaSecuritySecureRandom[F]
      atomicLong                   <- Sync[F].delay(new AtomicLong())
      counter                       = () => Sync[F].delay(atomicLong.incrementAndGet())
      randomDouble: RandomDouble[F] = () => secureRandom.nextDouble
      clock: ClockMillis[F]         = () => Clock[F].realTime.map(_.toMillis)
      rawMachineFingerprint        <- Sync[F].delay(System.getenv().asScala.mkString)
    } yield custom[F](rawMachineFingerprint, randomDouble, counter, clock)

  def unsafeDefault: Cuid2Gen[Id] = {
    val secureRandom = new JavaSecureRandom()
    val ref          = new AtomicLong(0L)

    val fingerprint  = System.getenv().asScala.mkString
    val randomDouble = () => secureRandom.nextDouble
    val counter      = () => ref.incrementAndGet()
    val clock        = () => System.currentTimeMillis()

    custom[Id](fingerprint, randomDouble, counter, clock)
  }

  def custom[F[_]: Monad](
    rawMachineFingerprint: String,
    random: RandomDouble[F],
    counter: Counter[F],
    clock: ClockMillis[F],
    createSha3Digest: () => MessageDigest = () => MessageDigest.getInstance("SHA3-256")
  ): Cuid2Gen[F] = new Cuid2Gen[F] {
    implicit val implicitRandom: RandomDouble[F] = random

    override def generate: F[Cuid2] =
      generateRaw[24].map(new Cuid2(_) {})

    override def generateLong: F[Cuid2Long] =
      generateRaw[32].map(new Cuid2Long(_) {})

    override def generateCustomLength[L <: Int](implicit valueOfLength: ValueOf[L]): F[Cuid2Custom[L]] =
      generateRaw[L].map(new Cuid2Custom[L](_) {})

    private def generateRaw[L <: Int](implicit valueOfLength: ValueOf[L]): F[String] =
      for {
        firstLetter  <- randomLetter
        nowMillis    <- clock.apply()
        timeBase36    = java.lang.Long.toString(nowMillis, 36)
        counterValue <- counter.apply()
        countBase36   = java.lang.Long.toString(counterValue, 36)
        fingerprint  <- enhanceFingerprint(rawMachineFingerprint)
        salt         <- createEntropy(valueOfLength.value)
        hashInput     = s"$timeBase36$salt$countBase36$fingerprint"
        trimmedHash   = hash(hashInput).substring(1, valueOfLength.value)
      } yield s"$firstLetter$trimmedHash"

    private def hash(string: String): String = {
      val hashBytes = createSha3Digest.apply().digest(string.getBytes)
      BigInt(1, hashBytes).toString(36)
    }

    private def randomLetter =
      implicitly[RandomDouble[F]].apply().map(double => Math.floor(double * letters.length).toInt).map(letters.charAt)

    private def enhanceFingerprint(rawMachineFingerprint: String): F[String] = {
      if (rawMachineFingerprint.length >= BigLength) hash(rawMachineFingerprint).pure[F]
      else createEntropy(BigLength).map(entropy => rawMachineFingerprint + entropy).map(hash)
    }.map(_.substring(0, BigLength))

    private def createEntropy(length: Int): F[String] =
      implicitly[RandomDouble[F]]
        .apply()
        .map(double => Integer.toString(Math.floor(double * 36).toInt, 36))
        .replicateA(length)
        .map(_.mkString)

  }

}

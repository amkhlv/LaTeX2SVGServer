
package com.andreimikhailov.latex2svgserver

import cats.Applicative
import cats.effect.Sync
import cats.implicits._
import io.circe.{Encoder, Decoder}
import io.circe.generic.semiauto._
import org.http4s._
import org.http4s.implicits._
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.circe._
import org.http4s.Method._

trait Formulas[F[_]]{
  def get: F[Formulas.Formula]
}

object Formulas {
  def apply[F[_]](implicit ev: Formulas[F]): Formulas[F] = ev

  final case class Formula(svg: String) extends AnyVal
  object Formula {
    implicit val formulaDecoder: Decoder[Formula] = deriveDecoder[Formula]
    implicit def formulaEntityDecoder[F[_]: Sync]: EntityDecoder[F, Formula] = jsonOf
    implicit val formulaEncoder: Encoder[Formula] = deriveEncoder[Formula]
    implicit def formulaEntityEncoder[F[_]: Applicative]: EntityEncoder[F, Formula] =
      jsonEncoderOf
  }

  final case class FormulaError(e: Throwable) extends RuntimeException

  def impl[F[_]: Sync](C: Client[F]): Formulas[F] = new Formulas[F]{
    val dsl = new Http4sClientDsl[F]{}
    import dsl._
    def get: F[Formulas.Formula] = {
      C.expect[Formula](GET(uri"https://icanhazdadjoke.com/"))
        .adaptError{ case t => FormulaError(t)} // Prevent Client Json Decoding Failure Leaking
    }
  }
}

package com.andreimikhailov.latex2svgserver

import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.`Content-Type`
import org.http4s.MediaType
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.EntityEncoder.stringEncoder
import org.http4s.circe._
import org.http4s.Header
import cats.Applicative
import org.jbibtex.{BibTeXDatabase, BibTeXEntry, Key}
import scala.jdk.CollectionConverters._

object Latex2svgserverRoutes {

  final case class FormulaForm(token: String, latex: String, size: Int, bg: String, fg: String)
  object FormulaForm {
    implicit val decoderFormulaForm: Decoder[FormulaForm] = deriveDecoder[FormulaForm]
    implicit def entityDecoderFormulaForm[F[_]: Sync]: EntityDecoder[F, FormulaForm] = jsonOf
    implicit val encoderFormulaForm: Encoder[FormulaForm] = deriveEncoder[FormulaForm]
    implicit def entityEncoderFormulaForm[F[_]: Applicative]: EntityEncoder[F, FormulaForm] = jsonEncoderOf
  }

  def formulaRoutes[F[_]: Sync](token: String): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case req @ POST -> Root / "svg" => if (req.headers.exists(_.toString().equals("BystroTeX: yes"))) {
        req.decode[FormulaForm](
          f => {
            if (f.token == token) {
              val svg = Convert.toSVG(f.latex, f.size, f.bg, f.fg, true)
              if (svg.badLatex) { 
                Ok(svg.errorMessage)
                  .map(_.withContentType(`Content-Type`(MediaType.text.plain)))
                  .map(_.withHeaders(Header("BystroTeX-error", "latex")))
                } else if (svg.generalError) {
                  Ok(svg.errorMessage)
                    .map(_.withContentType(`Content-Type`(MediaType.text.plain)))
                    .map(_.withHeaders(Header("BystroTeX-error", "general")))
                  } else {
                    Ok(svg.svg)
                      .map(_.withContentType(`Content-Type`(MediaType.image.`svg+xml`)))
                      .map(_.withHeaders(
                        Header("BystroTeX-depth", svg.depth.toString()),
                        Header("BystroTeX-height", svg.height.toString())
                      ))
                  }
                  } else { 
                    Forbidden("CSRF detected")
                  }
          }
          )
      } else {
        req.headers.foreach(h => { println(h.toString()) })
        Forbidden("foreign origin threat detected")
      }
    }
  }

  final case class BibTeXForm(token: String, k: String)
  object BibTeXForm {
    implicit val decoderBibTeXForm: Decoder[BibTeXForm] = deriveDecoder[BibTeXForm]
    implicit def entityDecoderBibTeXForm[F[_]: Sync]: EntityDecoder[F, BibTeXForm] = jsonOf
    implicit val encoderBibTeXForm: Encoder[BibTeXForm] = deriveEncoder[BibTeXForm]
    implicit def entityEncoderBibTeXForm[F[_]: Applicative]: EntityEncoder[F, BibTeXForm] = jsonEncoderOf
  }
  def bibtexRoutes[F[_]: Sync](token: String, btdb: BibTeXDatabase): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case req @ POST -> Root/"bibtex" => if (req.headers.exists(_.toString().equals("BystroTeX: yes"))) {
        req.decode[BibTeXForm](
          b => {
            if (b.token == token) {
              val entry: BibTeXEntry = btdb.resolveEntry(new Key(b.k))
              val fields = entry.getFields
              val fsset: Set[Key] = fields.keySet().asScala.toSet
              val fs: List[(Key, String)] = for (i <- fsset.toList) yield { i -> fields.get(i).toUserString }
              val marshalled = <bibentry>{for ((a,b) <- fs) yield <v key={a.getValue}>{b}</v>}</bibentry>
              val printer = new scala.xml.PrettyPrinter(80, 2)
              Ok(printer.format(marshalled))
                .map(_.withContentType(`Content-Type`(MediaType.text.plain)))
            } else Forbidden("CSRF detected")
          }
          )
      } else {
        Forbidden("foreign origin threat detected")
      }
    }
  }

  def jokeRoutes[F[_]: Sync](J: Jokes[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "joke" =>
        for {
          joke <- J.get
          resp <- Ok(joke)
        } yield resp
    }
  }

  def helloWorldRoutes[F[_]: Sync](H: HelloWorld[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "hello" / name =>
        for {
          greeting <- H.hello(HelloWorld.Name(name))
          resp <- Ok(greeting)
        } yield resp
    }
  }
}

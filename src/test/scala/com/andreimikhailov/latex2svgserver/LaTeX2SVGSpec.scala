package com.andreimikhailov.latex2svgserver

import cats.effect.IO
import org.http4s._
import org.http4s.implicits._
import org.specs2.matcher.MatchResult
import com.andreimikhailov.latex2svgserver.Latex2svgserverRoutes.FormulaForm

class LaTeX2SVGSpec extends org.specs2.mutable.Specification {

  "LaTeX2SVG" >> {
    "return 200" >> {
      uriReturns200()
    }
    "return SVG" >> {
      uriReturnsSVG()
    }
  }
  
  private[this] val retLaTeX2SVG: Response[IO] = {

    val getSVG = Request[IO](
      Method.POST, 
      uri"/svg",
      HttpVersion.`HTTP/2.0`,
      Headers.of(Header("BystroTeX", "yes"))
    ).withEntity(FormulaForm("TOKENTOKEN","x",20,"255:255:255","0:0:0"))

    Latex2svgserverRoutes.formulaRoutes[IO]("TOKENTOKEN").orNotFound(getSVG).unsafeRunSync()
  }

  private[this] def uriReturns200(): MatchResult[Status] =
    retLaTeX2SVG.status must beEqualTo(Status.Ok)

  private[this] def uriReturnsSVG(): MatchResult[String] =
    retLaTeX2SVG.as[String].unsafeRunSync() must beEqualTo("{\"message\":\"Hello, world\"}")
}

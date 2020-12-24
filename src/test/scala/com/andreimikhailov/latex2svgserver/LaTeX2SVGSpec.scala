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
    "prevent CSRF" >> {
      uriReturns403OnBadToken()
    }
    "prevent foreign origin" >> {
      uriReturns403OnBadHeader()
    }
  }
  
  private[this] def retLaTeX2SVG(goodHeader: Boolean, goodToken: Boolean): Response[IO] = {

    val getSVG = Request[IO](
      Method.POST, 
      uri"/svg",
      HttpVersion.`HTTP/2.0`,
      if (goodHeader) Headers.of(Header("BystroTeX", "yes")) else Headers.of()
    ).withEntity(FormulaForm(if (goodToken) "TOKENTOKEN" else "","x",20,"255:255:255","0:0:0"))

    Latex2svgserverRoutes.formulaRoutes[IO]("TOKENTOKEN").orNotFound(getSVG).unsafeRunSync()
  }

  private[this] def uriReturns200(): MatchResult[Status] =
    retLaTeX2SVG(true, true).status must beEqualTo(Status.Ok)

  private[this] def uriReturnsSVG(): MatchResult[String] = {
    val svg = scala.xml.XML.loadString(retLaTeX2SVG(true,true).as[String].unsafeRunSync())
    svg.head.label must beEqualTo("svg")
  }

  private[this] def uriReturns403OnBadToken(): MatchResult[Status] =
    retLaTeX2SVG(true, false).status must beEqualTo(Status.Forbidden)

  private[this] def uriReturns403OnBadHeader(): MatchResult[Status] =
    retLaTeX2SVG(false, true).status must beEqualTo(Status.Forbidden)

}

package com.andreimikhailov.latex2svgserver

import cats.effect.{ConcurrentEffect, Timer}
import cats.implicits._
import fs2.Stream
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import scala.concurrent.ExecutionContext.global
import com.typesafe.config.ConfigFactory
import java.io.{File, FileReader}
import org.jbibtex.BibTeXParser

object Latex2svgserverServer {
  // http://grokbase.com/t/gg/play-framework/146s1cwcp1/secure-random-number-generation-in-playframework
  val random = {
     val r = new java.security.SecureRandom()
     // NIST SP800-90A recommends a seed length of 440 bits (i.e. 55 bytes)
     r.setSeed(r.generateSeed(55))
     r
   }
   /**
    * Return a URL safe base64 string, which may be larger than numBytes.
    */
  def nextBase64String(numBytes:Int):String = {
     val bytes = new Array[Byte](numBytes)
     random.nextBytes(bytes)
     val encodedBytes = java.util.Base64.getUrlEncoder.encode(bytes)
     new String(encodedBytes, "UTF-8")
  }
  val tokenRandom = nextBase64String(128)
  

  val conf = ConfigFactory.load()
  val bibFile = new File(conf.getString("bibfile"))
  val btParser: BibTeXParser = new BibTeXParser()
  val ip : String = conf.getString("http.address")
  val port : Int = conf.getInt("http.port")
  val bystroServerConfFile : String = conf.getString("bystroFile")
  val bystroConf : scala.xml.Node =
    <server><version>2</version><host>{ip}</host><port>{port}</port><path>svg</path><bibpath>bibtex</bibpath><token>{tokenRandom}</token></server>
  scala.xml.XML.save(bystroServerConfFile, bystroConf, "UTF-8", true)

  def stream[F[_]: ConcurrentEffect](implicit T: Timer[F]): Stream[F, Nothing] = {
    for {
      client <- BlazeClientBuilder[F](global).stream
      helloWorldAlg = HelloWorld.impl[F]
      jokeAlg = Jokes.impl[F](client)

      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract a segments not checked
      // in the underlying routes.
      httpApp = (
        Latex2svgserverRoutes.helloWorldRoutes[F](helloWorldAlg) <+>
        Latex2svgserverRoutes.jokeRoutes[F](jokeAlg) <+>
        Latex2svgserverRoutes.bibtexRoutes[F](tokenRandom, btParser.parse(new FileReader(bibFile))) <+>
        Latex2svgserverRoutes.formulaRoutes[F](tokenRandom)
      ).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(port, ip)
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}

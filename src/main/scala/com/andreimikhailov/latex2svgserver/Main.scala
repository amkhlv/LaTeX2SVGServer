package com.andreimikhailov.latex2svgserver

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  def run(args: List[String]) =
    Latex2svgserverServer.stream[IO].compile.drain.as(ExitCode.Success)
}

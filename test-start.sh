#!/usr/bin/env bash

java  -DbystroFile=latex2svg.xml -Dbibfile=/home/andrei/a/Work/andrei.bib -Dhttp.port=11111 -Dhttp.address=127.0.0.1 -Dpidfile.path=/tmp/testing-LaTeX2SVG.pid -jar latex2svgserver.jar &
echo $! > /tmp/testing-LaTeX2SVG.pid

(
  cd bystrotex-test/
  bystrotex
)


LaTeX2SVGServer
===============

To be used with [BystroTeX](http://andreimikhailov.com/slides/bystroTeX/slides-manual/index.html)

Building
========

    sbt assembly

This produces file `latex2svgserver.jar`

Running
=======

    java  -DbystroFile=latex2svg.xml -Dbibfile=/home/andrei/a/Work/andrei.bib -Dhttp.port=11111 -Dhttp.address=127.0.0.1 -Dpidfile.path=/tmp/testing-LaTeX2SVG.pid -jar latex2svgserver.jar &

Debug level
===========

Verbosity of output is controlled by `src/main/resources/logback.xml`

    <root level="WARN">
      <appender-ref ref="STDOUT" />
    </root>

Can replace `WARN` with `INFO` or `DEBUG`


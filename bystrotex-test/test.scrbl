#lang scribble/base
@(require racket scribble/core scribble/base scribble/html-properties)
@(require "defs.rkt" bystroTeX/common bystroTeX/slides (for-syntax bystroTeX/slides_for-syntax))
@; ---------------------------------------------------------------------------------------------------
@; User definitions:
@(bystro-set-css-dir (build-path (find-system-path 'home-dir) "a" "git" "amkhlv" "profiles" "writeup"))
@(define bystro-conf   
   (bystro (bystro-connect-to-server (build-path (find-system-path 'home-dir) "a" "git" "LaTeX2SVGServer" "latex2svg.xml"))
           "test/formulas.sqlite"  ; name for the database
           "test" ; directory where to store image files of formulas
           25  ; formula size
           (list 255 255 255) ; formula background color
           (list 0 0 0) ; formula foreground color
           2   ; automatic alignment adjustment
           0   ; manual alignment adjustment
           ))
@(define singlepage-mode #t)
@(bystro-def-formula "formula-enormula-humongula!")


@title[#:style '(no-toc no-sidebar)]{Testing LaTeX2SVGServer}

@bystro-source[]

@bystro-ribbon[]

@f{x^2}

@f{y^2}

@bystro-ribbon[]

@; ---------------------------------------------------------------------------------------------------
@(bystro-close-connection bystro-conf)
@disconnect[formula-database]

  

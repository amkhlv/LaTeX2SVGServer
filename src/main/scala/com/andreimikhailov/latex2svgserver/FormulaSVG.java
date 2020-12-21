package com.andreimikhailov.latex2svgserver;

/**
 * Created by andrei on 26/01/15.
 */
public class FormulaSVG {
    public String svg;
    public int depth;
    public int height;
    public boolean badLatex;
    public boolean generalError;
    public String errorMessage;

    public FormulaSVG(String s, int d , int h, boolean bl, boolean ge, String em) {
        svg = s;
        depth = d;
        height = h;
        badLatex = bl;
        generalError = ge;
        errorMessage = em;
    }
}

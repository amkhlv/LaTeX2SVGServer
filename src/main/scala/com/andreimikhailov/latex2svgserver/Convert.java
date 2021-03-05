package com.andreimikhailov.latex2svgserver;


import java.awt.Insets;
import java.awt.Color;
import java.awt.Dimension;
import java.io.*;
import javax.swing.JLabel;

import org.scilab.forge.jlatexmath.*;
import org.scilab.forge.jlatexmath.cyrillic.CyrillicRegistration;
import org.scilab.forge.jlatexmath.greek.GreekRegistration;

//import org.apache.batik.transcoder.TranscoderOutput;
//import org.apache.batik.transcoder.TranscoderInput;
//import org.apache.fop.svg.AbstractFOPTranscoder;
//import org.apache.fop.svg.PDFTranscoder;
//import org.apache.fop.render.ps.PSTranscoder;
//import org.apache.fop.render.ps.EPSTranscoder;
//import org.apache.avalon.framework.configuration.DefaultConfiguration;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.dom.GenericDOMImplementation;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class Convert {

    public static final int PDF = 0;
    public static final int PS = 1;
    public static final int EPS = 2;
    
    public static FormulaSVG toSVG(String latex, int size, String bg, String fg,  boolean fontAsShapes) throws IOException {
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(document);
	    String[] bgcols = bg.split(":");
        String[] fgcols = fg.split(":");
        java.awt.Color bgc = new Color(
                Integer.parseInt(bgcols[0]),
                Integer.parseInt(bgcols[1]),
                Integer.parseInt(bgcols[2]));
        java.awt.Color fgc = new Color(
                Integer.parseInt(fgcols[0]),
                Integer.parseInt(fgcols[1]),
                Integer.parseInt(fgcols[2]));

        SVGGraphics2D g2 = new SVGGraphics2D(ctx, fontAsShapes);

        DefaultTeXFont.registerAlphabet(new CyrillicRegistration());
        DefaultTeXFont.registerAlphabet(new GreekRegistration());


        try {
        TeXFormula formula = new TeXFormula(latex);
        //TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, size);
        TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, size, TeXFormula.SANSSERIF);
        int height = icon.getIconHeight();
        int depth = icon.getIconDepth();

        icon.setInsets(new Insets(1, 1, 1, 1));


        g2.setSVGCanvasSize(new Dimension(icon.getIconWidth(),icon.getIconHeight()));
        g2.setColor(bgc);
        g2.fillRect(0,0,icon.getIconWidth(),icon.getIconHeight());
        JLabel jl = new JLabel();
        jl.setForeground(fgc);
        icon.paintIcon(jl, g2, 0, 0);
        Element root = g2.getRoot();
        root.setAttributeNS(
                null,
                "viewBox",
                "0 0 "+ Integer.toString(icon.getIconWidth()) + " " + Integer.toString(icon.getIconHeight())
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer out = new OutputStreamWriter(baos, "UTF-8");

        g2.stream(root, out);
        baos.flush();
        baos.close();
        String svgstring = new String( baos.toByteArray(), java.nio.charset.StandardCharsets.UTF_8);
        return new FormulaSVG(svgstring, depth, height, false, false, "");
        } catch(ParseException e) {
                    return new FormulaSVG("",0,0,true,false,e.getMessage());
        }

        //org.dom4j.io.DOMReader reader = new DOMReader();
        //org.dom4j.Document document4j = reader.read(g2.getDOMFactory());
        //return new FormulaSVG(document4j.asXML(), depth, height, false, false, "");

        
    }

}

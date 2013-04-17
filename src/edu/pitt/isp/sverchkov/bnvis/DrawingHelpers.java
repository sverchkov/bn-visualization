/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.bnvis;

import processing.core.PApplet;

/**
 *
 * @author YUS24
 */
public class DrawingHelpers {
    public static final float ARROWSIZE = 10;
    
    /**
     * Draw an arrow from x1,y1 to x2,y2 on canvas p
     * @param p Processing canvas
     * @param x1
     * @param y1
     * @param x2
     * @param y2 
     */
    public static void arrow( PApplet p, float x1, float y1, float x2, float y2 ){
        p.line( x1, y1, x2, y2 );
        p.pushMatrix();
        p.translate( x2, y2 );
        p.rotate( (float)Math.atan2( x1-x2, y2-y1 ) );
        p.line( 0, 0, -ARROWSIZE/4, -ARROWSIZE );
        p.line( 0, 0, ARROWSIZE/4, -ARROWSIZE );
        p.popMatrix();
    }
}

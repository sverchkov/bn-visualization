/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.bnvis;

import edu.pitt.isp.sverchkov.geometry.LineTerminus;
import edu.pitt.isp.sverchkov.geometry.Point;
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
    
    public static void arrow( PApplet p, LineTerminus s, LineTerminus d ){
        final Point
                source = s.getContactPointFrom( d ),
                dest = d.getContactPointFrom( s );
        
        arrow( p, (float) source.x, (float) source.y, (float) dest.x, (float) dest.y );
    }
    
    /*
    @Deprecated
    public static void arrowPointToEllipse( PApplet p, float sourceX, float sourceY, float destX, float destY, float destW, float destH ){
        final double centerX = destX+destW/2, centerY = destY+destH/2; 
        double x=0, y=destH/2 * Math.signum(sourceY-centerY);
        final double dX = sourceX-centerX;
        if( dX != 0 ){
            final double slope = (sourceY-centerY)/dX;
            x = Math.sqrt( 1f/( 4f/(destW*destW) + slope*slope*4/(destH*destH) ) ) * Math.signum(dX);
            y = x*slope;
        }
        arrow( p, sourceX, sourceY, (float)(centerX+x), (float)(centerY+y) );
    }
    
    @Deprecated
    public static void arrowEllipseToEllipse( PApplet p, float sourceX, float sourceY, float sourceW, float sourceH, float destX, float destY, float destW, float destH ){
        final double centerX = sourceX+sourceW/2, centerY = sourceY+sourceH/2;
        final double dX = destX+destW/2-centerX;
        final double dY = destY+destH/2-centerY;
        double x=0, y=sourceH/2 * Math.signum(dY);
        if( dX != 0 ){
            final double slope = dY/dX;
            x = Math.sqrt( 1f/( 4f/(sourceW*sourceW) + slope*slope*4/(sourceH*sourceH) ) ) * Math.signum(dX);
            y = x*slope;
        }
        arrowPointToEllipse( p, (float)(centerX+x), (float)(centerY+y), destX, destY, destW, destH );
    }*/
}

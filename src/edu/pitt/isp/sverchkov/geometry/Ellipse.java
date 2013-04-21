/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.geometry;

/**
 *
 * @author YUS24
 */
public class Ellipse implements LineTerminus {

    public final double x, y, width, height;
    
    public Ellipse(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public Point getLineTarget() {
        return new Point( x + width/2, y + height/2 );
    }

    @Override
    public Point getContactPointFrom(LineTerminus source) {
        final Point
                s = source.getLineTarget(),
                d = getLineTarget();
        final double dY = s.y-d.y;
        double x=0, y=height/2 * Math.signum(dY);
        final double dX = s.x-d.x;
        if( dX != 0 ){
            final double slope = dY/dX;
            x = Math.sqrt( 1.0/( 4.0/(width*width) + slope*slope*4/(height*height) ) ) * Math.signum(dX);
            y = x*slope;
        }
        return new Point( d.x + x, d.y + y );
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.geometry;

/**
 *
 * @author YUS24
 */
public final class Point implements LineTerminus {
    public final double x, y;
    public Point( double x, double y ){
        this.x = x;
        this.y = y;
    }

    @Override
    public Point getLineTarget() {
        return this;
    }

    @Override
    public Point getContactPointFrom( LineTerminus source ) {
        return this;
    }
}

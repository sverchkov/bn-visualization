/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.geometry;

/**
 *
 * @author YUS24
 */
public interface LineTerminus {
    /**
     * Gives the point to which the line should point (e.g. the center)
     * @return the point as a Point object
     */
    Point getLineTarget();
    
    /**
     * Gives the point which the line should touch (e.g. a point on the boundary)
     * @param source
     * @return the point as a Point object
     */
    Point getContactPointFrom( LineTerminus source );
}

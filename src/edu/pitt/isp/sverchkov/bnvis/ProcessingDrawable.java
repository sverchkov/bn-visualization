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
public interface ProcessingDrawable {
    // Needs to connect to the PApplet
    void setParentApplet( PApplet applet );
    // Where the magic happens
    void draw();
    // Mouseover test
    boolean isMouseOver();
    boolean isMouseOver( float mouseX, float mouseY );
    // Focus setter+getter
    void setFocus( boolean focus );
    boolean hasFocus();
    // Update needs to be called before draw
    void update(float mouseX, float mouseY, float pmouseX, float pmouseY, boolean mousePressed);
    void update();
}

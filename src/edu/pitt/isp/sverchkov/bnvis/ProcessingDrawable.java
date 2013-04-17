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
    void handleMouse(float mouseX, float mouseY, float pmouseX, float pmouseY, boolean mousePressed);
    void handleMouse();
}
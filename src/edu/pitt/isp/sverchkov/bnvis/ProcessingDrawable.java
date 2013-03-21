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
    void setParentApplet( PApplet parent );
    // Where the magic happens
    void draw();
}

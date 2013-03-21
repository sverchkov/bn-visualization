/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.bnvis;

import java.util.ArrayList;
import java.util.List;
import processing.core.PApplet;

/**
 *
 * @author YUS24
 */
public class BNNodeSketch implements ProcessingDrawable {
    
    private PApplet p; // The parent Processing applet
    
    private final List<BNNodeSketch> parents = new ArrayList<>();
    
    private int x,y;

    @Override
    public void setParentApplet(PApplet parent) {
        this.p = parent;
    }

    @Override
    public void draw() {
        p.rectMode(PApplet.CENTER);
        p.rect(x,y,100,100);
    }
    
}

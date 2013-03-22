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
public abstract class AbstractProcessingDrawable implements ProcessingDrawable {
    
    protected PApplet p;
    
    @Override
    public void setParentApplet(PApplet applet) {
        p = applet;
    }

    @Override
    public boolean isMouseOver() {
        return isMouseOver( p.mouseX, p.mouseY );
    }
}

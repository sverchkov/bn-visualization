/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.bnvis;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import processing.core.PApplet;

/**
 *
 * @author YUS24
 */
public class MainPApplet extends PApplet {

    private static final float ZOOMSTEP = 0.1f;
    private static final float MINZOOM = ZOOMSTEP;
    
    private float zoom = 1; // Proportional
    private float xpiv = 0, ypiv = 0;
    
    private final List<ProcessingDrawable> drawables = new ArrayList<>();
        
    private final MouseWheelListener mouseWheel = new MouseWheelListener(){
        @Override
        public void mouseWheelMoved(MouseWheelEvent mwe) {
            zoom += mwe.getWheelRotation()*ZOOMSTEP;
            if( zoom <= MINZOOM ) zoom = MINZOOM;
        }
    };
    
    private void transscale() {
        float
                xOffset = mouseX-pmouseX,
                yOffset = mouseY-pmouseY;
        
        xpiv -= xOffset/zoom;
        ypiv -= yOffset/zoom;

        if (mousePressed) {
            xpiv = xpiv + (xOffset/zoom);
            ypiv = ypiv + (yOffset/zoom);
        }
        
        // Pan
        translate(mouseX, mouseY);
        // Zoom
        scale(zoom);
        // Pan to zoom center
        translate(xpiv, ypiv);
    }
    
    @Override
    public void setup(){ 
        addMouseWheelListener( mouseWheel );
        size(400,600);
    }
    
    @Override
    public void draw(){
        background(255);
        stroke(0);
        
        // Remember coordinate matrix
        // pushMatrix();
        
        // Pan+Zoom
        transscale();
        
        // Objects are drawn
        for( ProcessingDrawable d : drawables )
            d.draw();
        
        // Undo coordinate matrix transformation
        // popMatrix();
        
        // Hud-like elements go here
    }
    
    public void addDrawable( ProcessingDrawable d ){
        d.setParentApplet(this);
        drawables.add(d);
    }
}

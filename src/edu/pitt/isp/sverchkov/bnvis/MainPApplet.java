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
    
    // Flag to indicate panning
    private boolean panning = true;
    
    private float
            // Mouse position in drawing space
            msX = mouseX,
            msY = mouseY,
            // Previous mouse position in drawing space
            pmsX = msX,
            pmsY = msY;
    
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
        
        // Save previous mouse position in drawing space
        pmsX = msX;
        pmsY = msY;
        
        // Mouse does not move in drawing space if we are panning
        if( !panning || !mousePressed ){
            // Update mouse position drawing space
            msX += xOffset/zoom;
            msY += yOffset/zoom;
        }
        
        // Move origin to the mouse position
        translate(mouseX, mouseY);
        // Zoom
        scale(zoom);
        // Move origin to its corrent position relative to the mouse's position
        // in drawing space
        translate(-msX, -msY);        
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
        
        // Mouse events
        panning = true;

        // All-object operations
        synchronized( drawables ){
            for( ProcessingDrawable d : drawables ){
                // Determine if we're mousing over something
                panning &= !d.isMouseOver(msX, msY);
                // Draw object
                d.draw();
            }
        }
        
        /* Mouse position test
        addDrawable( new ProcessingDrawable(){
            
            private float x1 = msX, y1 = msY, x0 = pmsX, y0 = pmsY;

            @Override
            public void setParentApplet(PApplet parent) {
            }

            @Override
            public void draw() {
                line( x0, y0, x1, y1 );
            }
        });
        */
        
        // Undo coordinate matrix transformation
        // popMatrix();
        
        // Hud-like elements go here
    }
    
    public void addDrawable( ProcessingDrawable d ){
        d.setParentApplet(this);
        synchronized( drawables ){
            drawables.add(d);
        }
    }
}

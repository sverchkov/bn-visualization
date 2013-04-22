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
            zoom -= mwe.getWheelRotation()*ZOOMSTEP;
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
            boolean nofocus = true;
            for( ProcessingDrawable d : drawables ){
                // Determine if we're mousing over something
                if( nofocus && d.isMouseOver(msX, msY) ){
                    // If we are, disable catch focus, disable panning
                    panning = false;
                    nofocus = false;
                    // Send the focus to the object
                    d.setFocus( true );
                }else
                    d.setFocus( false );
                // Object update call
                d.update( msX, msY, pmsX, pmsY, mousePressed, mouseButton );
            }
            // Draw objects
            for( ProcessingDrawable d : drawables )
                d.draw();
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
            d.init();
        }
    }
    
    public void removeDrawables(){
        synchronized( drawables ){
            drawables.clear();
        }
    }
    
    public void zoomToFitDrawables(){
        float
                minX = Float.POSITIVE_INFINITY,
                minY = Float.POSITIVE_INFINITY,
                maxX = Float.NEGATIVE_INFINITY,
                maxY = Float.NEGATIVE_INFINITY;
        synchronized( drawables ){
            for( ProcessingDrawable d : drawables ){
                minX = Math.min( minX, d.getMinX() );
                maxX = Math.max( maxX, d.getMaxX() );
                minY = Math.min( minY, d.getMinY() );
                maxY = Math.max( maxY, d.getMaxY() );
            }
        }
        zoomTo( minX, minY, maxX, maxY );            
    }
    
    public void zoomTo( ProcessingDrawable d ){
        if( drawables.contains(d) ){
            // Put a x4 buffer around the object
            final float
                    w = d.getMaxX() - d.getMinX(),
                    h = d.getMaxY() - d.getMinY();
            zoomTo( d.getMinX()-4*w, d.getMinY()-4*h, d.getMaxX()+4*w, d.getMaxY()+4*h );
        }
    }
    
    public void zoomTo( float minX, float minY, float maxX, float maxY ){
        if( minX < maxX && minY < maxY ){
            // Recall
            // msX = mouseX / zoom + origin
            
            // Figure out zoom, and adjust corner to get it centered
            final float
                    w = maxX - minX,
                    h = maxY - minY,
                    wRat = width/w,
                    hRat = height/h;
            if( wRat < hRat ){
                zoom = wRat;
                minY = minY - (height/zoom - h)/2;
            }else{
                zoom = hRat;
                minX = minX - (width/zoom - w)/2;
            }
            // Pan
            pmsX = msX = mouseX/zoom + minX;
            pmsY = msY = mouseY/zoom + minY;
        }
    }
        
}

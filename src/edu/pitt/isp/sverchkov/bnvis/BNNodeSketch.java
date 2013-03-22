/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.bnvis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import processing.core.PApplet;

/**
 *
 * @author YUS24
 */
public class BNNodeSketch extends AbstractProcessingDrawable implements ProcessingDrawable {
    
    private final List<BNNodeSketch> parents = new ArrayList<>();
    
    private float x,y,width = 50,height = 50;
    
    public BNNodeSketch( int x, int y ){
        this.x = x;
        this.y = y;
    }
    
    public void addParentNodes( BNNodeSketch ... parentNodes ){
        addParentNodes( Arrays.asList(parentNodes) );
    }
    
    public void addParentNodes( Collection<BNNodeSketch> parentNodes ){
        parents.addAll( parentNodes );
    }
    
    @Override
    public boolean isMouseOver( float mouseX, float mouseY ){
        return mouseX >= x && mouseX <= x+width && mouseY >= y && mouseY <= y+height;
    }

    @Override
    public void draw() {
        // p.rectMode(PApplet.CENTER);
        p.rect(x,y,width,height);
        for( BNNodeSketch parent : parents ){
            p.line( parent.x, parent.y, x, y );
        }
    }
    
}

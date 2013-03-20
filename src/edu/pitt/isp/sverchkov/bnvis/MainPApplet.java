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
public class MainPApplet extends PApplet {
    
    @Override
    public void setup(){ 
        size(400,400);
        background(0);
    }
    
    @Override
    public void draw(){
        stroke(255);
        if (mousePressed) {
            line(mouseX, mouseY, pmouseX, pmouseY );
        }
    }
}

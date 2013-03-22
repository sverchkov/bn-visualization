/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.bnvis;

import java.awt.BorderLayout;
import javax.swing.JFrame;

/**
 *
 * @author YUS24
 */
public class BNVisualization {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // Set up JFrame
        
        JFrame frame = new JFrame("BNVis v.000");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainPApplet applet = new MainPApplet();
        frame.getContentPane().add( applet, BorderLayout.CENTER );
        applet.init();
        frame.pack();
        frame.setSize(500, 500);
        frame.setVisible(true);
        
        // Link network to display applet
        
        // For now, just place drawable objects in there
        BNNodeSketch
                a = new BNNodeSketch( 100, 100),
                b = new BNNodeSketch( 200, 100),
                c = new BNNodeSketch( 150, 300);
        applet.addDrawable( a );
        applet.addDrawable( b );
        applet.addDrawable( c );
        c.addParentNodes( a, b );
    }
}

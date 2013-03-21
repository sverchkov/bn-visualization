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
        
        JFrame frame = new JFrame("BNVis v.000");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainPApplet applet = new MainPApplet();
        frame.getContentPane().add( applet, BorderLayout.CENTER );
        applet.init();
        frame.pack();
        frame.setVisible(true);
    }
}

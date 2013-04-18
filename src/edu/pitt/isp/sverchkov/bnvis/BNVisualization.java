/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.bnvis;

import edu.pitt.isp.sverchkov.bn.BNUtils;
import edu.pitt.isp.sverchkov.bn.BayesNet;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import javax.swing.*;

/**
 *
 * @author YUS24
 */
public class BNVisualization {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        BNVisualization bnv = new BNVisualization();
        
        bnv.run();
    }
    
    private static BayesNet<String,String> newNet(){
        return new BayesNet<String,String>(){

            @Override
            public int size() {
                return 3;
            }

            @Override
            public Collection<String> parents(String node) {
                return node.equals("C") ? Arrays.asList("A","B") : Collections.EMPTY_SET;
            }

            @Override
            public Collection<String> values(String node) {
                return Arrays.asList( "Yes", "No", "Maybe" );
            }

            @Override
            public double probability(Map<String, String> outcomes, Map<String, String> conditions) {
                return 0.33333333;
            }

            @Override
            public Iterator<String> iterator() {
                return Arrays.asList("A","B","C").iterator();
            }
        };
    }

    private static List<BNNodeSketch> sketchesFromNet(BayesNet<String,String> net) {
        
        List<BNNodeSketch> nodes = new ArrayList<>();
        Map<String,BNNodeSketch> nodeMap = new HashMap<>();
        
        // Make models and sketches
        for( String name : BNUtils.nodesInTopOrder(net) ){
            BNNodeModel model = new BNNodeModelImpl( net, name );
            BNNodeSketch node = new BNNodeSketch(0,0,model);
            nodes.add(node);
            nodeMap.put(name, node);
        }
        
        // Connect parents
        for( Map.Entry<String,BNNodeSketch> entry : nodeMap.entrySet() )
            for( String parent : net.parents(entry.getKey()) )
                entry.getValue().addParentNodes( nodeMap.get(parent) );        
        
        return nodes;
    }
    
    private BayesNet currentNet = newNet();
    private final MainPApplet applet;
    private final JFrame frame;

    public BNVisualization(){
        
        // Set up JFrame        
        frame = new JFrame("BNVis v.000");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Make a menubar
        final JMenuBar menuBar = new JMenuBar();
        final JMenu menu = new JMenu("File");
        final JMenuItem menuItemOpen = new JMenuItem("Open");
        
        // Make a file chooser object
        final JFileChooser fc = new JFileChooser();
        
        // Add the listener
        menuItemOpen.addActionListener( new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if( e.getSource() == menuItemOpen ){                
                    // Filechooser call
                    final int result = fc.showOpenDialog( frame );

                    if( result == JFileChooser.APPROVE_OPTION ){
                        File file = fc.getSelectedFile();

                        // Read network object

                        // Load to canvas
                        clearAndFillApplet();
                    }
                }
            }
        } );
        
        // Connect items to frame
        menu.add(menuItemOpen);
        menuBar.add(menu);        
        frame.setJMenuBar(menuBar);
        
        // Processing canvas
        applet = new MainPApplet();
        frame.getContentPane().add( applet, BorderLayout.CENTER );
    }
    
    public void run(){
        applet.init();
        
        frame.pack();
        frame.setSize(500, 500);
        frame.setVisible(true);
    }
    
    private void clearAndFillApplet(){
        
        applet.removeDrawables();
        
        // Link network to display applet
        List<BNNodeSketch> sketches = sketchesFromNet( currentNet );

        for( ProcessingDrawable d : sketches )
            applet.addDrawable( d );

    }
    
}

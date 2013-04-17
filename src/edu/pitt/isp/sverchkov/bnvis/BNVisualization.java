/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.bnvis;

import edu.pitt.isp.sverchkov.bn.BNUtils;
import edu.pitt.isp.sverchkov.bn.BayesNet;
import java.awt.BorderLayout;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 *
 * @author YUS24
 */
public class BNVisualization {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // Make a menubar
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem menuItemOpen = new JMenuItem("Open");
        
        menu.add(menuItemOpen);
        menuBar.add(menu);        
        
        // Set up JFrame        
        JFrame frame = new JFrame("BNVis v.000");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setJMenuBar(menuBar);
        
        // Processing canvas
        MainPApplet applet = new MainPApplet();
        frame.getContentPane().add( applet, BorderLayout.CENTER );
        applet.init();
        
        frame.pack();
        frame.setSize(500, 500);
        frame.setVisible(true);
        
        // Link network to display applet
        List<BNNodeSketch> sketches = sketchesFromNet( newNet() );

        for( ProcessingDrawable d : sketches )
            applet.addDrawable( d );
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
}

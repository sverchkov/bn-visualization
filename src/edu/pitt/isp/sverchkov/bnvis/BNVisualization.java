/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.bnvis;

import edu.pitt.isp.sverchkov.bn.BNUtils;
import edu.pitt.isp.sverchkov.bn.BayesNet;
import edu.pitt.isp.sverchkov.smile.BayesNetSMILE;
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

    private static List<BNNodeSketch> sketchesFromNet(BayesNet<String,String> net, Map<String,List<Float>> placement ) {
        
        List<BNNodeSketch> nodes = new ArrayList<>();
        Map<String,BNNodeSketch> nodeMap = new HashMap<>();
        
        // Make models and sketches
        for( String name : BNUtils.nodesInTopOrder(net) ){
            BNNodeModel model = new BNNodeModelImpl( net, name );
            
            Float x=0f, y=0f;
            List<Float> coords = null;
            if( null != placement && null != (coords = placement.get(name)) )
                if( coords.size() >= 2 ){
                    if( null == (x = coords.get(0)) ) x = 0f;
                    if( null == (y = coords.get(1)) ) y = 0f;
                }
            
            BNNodeSketch node = new BNNodeSketch(x,y,model);
            nodes.add(node);
            nodeMap.put(name, node);
        }
        
        // Connect parents 
        for( Map.Entry<String,BNNodeSketch> entry : nodeMap.entrySet() )
            for( String parent : net.parents(entry.getKey()) )
                entry.getValue().addParentNodes( nodeMap.get(parent) );        
        
        return nodes;
    }
    
    private BayesNet<String,String> currentNet = newNet();
    private final MainPApplet applet;
    private final JFrame frame;
    private Map<String,List<Float>> nodePlacement;
    private final Map<String,BNNodeSketch> sketchMap= new HashMap<>();

    public BNVisualization(){
        
        // Set up JFrame        
        frame = new JFrame("BNVis v.000");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Make a menubar
        final JMenuBar menuBar = new JMenuBar();

        final JMenu fileMenu = new JMenu("File");
        final JMenuItem menuItemOpen = new JMenuItem("Open");

        final JMenu smileMenu = new JMenu("SMILE");
        final JMenuItem menuItemConvertIDs = new JMenuItem("Toggle SMILE ID conversion");
        
        final JMenu zoomMenu = new JMenu("Zoom");
        final JMenuItem menuItemFitNet = new JMenuItem("Zoom to fit net");
        final JMenuItem menuItemZoomNode = new JMenuItem("Zoom to node");
        
        // Make a file chooser object
        final JFileChooser fc = new JFileChooser();
        
        // Make a listener for button actions
        final ActionListener actionListener = new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                
                final Object source = e.getSource();
                // For the save button
                if( source == menuItemOpen ){                
                    // Filechooser call
                    final int result = fc.showOpenDialog( frame );

                    if( result == JFileChooser.APPROVE_OPTION ){
                        File file = fc.getSelectedFile();

                        // Read network object
                        BayesNetSMILE net = new BayesNetSMILE( file );
                        
                        // Get node placements
                        nodePlacement = new HashMap<>();
                        for( String node : net )
                            nodePlacement.put( node, net.getNodeCoordinates(node) );

                        // Set net object and load to canvas
                        currentNet = net;
                        clearAndFillApplet();
                    }
                    
                }else if( source == menuItemConvertIDs ){
                    if( currentNet instanceof BayesNetSMILE ){
                        BayesNetSMILE net = (BayesNetSMILE) currentNet;
                        net.convertIDs(!net.isConvertIDs());
                        clearAndFillApplet();
                    }
                }else if( source == menuItemFitNet ){
                    applet.zoomToFitDrawables();
                }else if( source == menuItemZoomNode ){
                    String[] options = sketchMap.keySet().toArray( new String[0] );
                    if( options.length > 0 ){
                        String result = (String) JOptionPane.showInputDialog(
                                frame,
                                "Select a node to zoom in on:",
                                "Zoom to node",
                                JOptionPane.PLAIN_MESSAGE,
                                null, //icon
                                options,
                                options[0] );
                        ProcessingDrawable d = sketchMap.get( result );
                        if( d != null ) applet.zoomTo(d);
                    }
                }
            }
        };
                
        menuItemOpen.addActionListener(actionListener);
        menuItemConvertIDs.addActionListener(actionListener);
        menuItemFitNet.addActionListener(actionListener);
        menuItemZoomNode.addActionListener(actionListener);
        
        // Connect items to frame
        fileMenu.add(menuItemOpen);
        smileMenu.add(menuItemConvertIDs);
        zoomMenu.add(menuItemFitNet);
        zoomMenu.add(menuItemZoomNode);
        menuBar.add(fileMenu);
        menuBar.add(smileMenu);
        menuBar.add(zoomMenu);
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
        List<BNNodeSketch> sketches = sketchesFromNet( currentNet, nodePlacement );
        
        // Fill find menu
        fillFindMenu( sketches );

        for( ProcessingDrawable d : sketches )
            applet.addDrawable( d );

        applet.zoomToFitDrawables();
    }
    
    private void fillFindMenu( List<BNNodeSketch> sketches ){
        sketchMap.clear();
        for( final BNNodeSketch sketch : sketches )
            sketchMap.put( sketch.name(), sketch );
    }
}

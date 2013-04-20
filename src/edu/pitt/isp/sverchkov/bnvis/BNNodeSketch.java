/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.bnvis;

import edu.pitt.isp.sverchkov.arrays.ArrayTools;
import java.util.*;
import processing.core.PApplet;

/**
 *
 * @author YUS24
 */
public class BNNodeSketch extends AbstractProcessingDrawable implements ProcessingDrawable {
    
    public static final float SPACING = 3;
    public static final float BARHEIGHT = 10;
    public static final float TITLESIZE = 20;
    public static final float VLABELSIZE = 10;
    
    private final Map<String,BNNodeSketch> parents = new HashMap<>();
    private final Map<String,Float> outHandleXOs = new HashMap<>(); // X offset of outgoing handles
    private final BNNodeModel model;
    
    private float x,y,width = 50,height = 50;
    private float oldX, oldY;
    private boolean dragging = false;
    private boolean expanded = true;
    private boolean focusP = false;
    
    private float nodeTitleY;
    private float[][][] barXYWs;
    private CPTRow[] visibleRows;
    private float[] valueTextX;
    private float valueTextY;
    private int highlightCol = -1, highlightRow = -1;
    
    public BNNodeSketch( float x, float y, BNNodeModel m ){
        this.x = x;
        this.y = y;
        this.model = m;
    }
    
    public void addParentNodes( BNNodeSketch ... parentNodes ){
        for( BNNodeSketch node : parentNodes )
            parents.put( node.model.name(), node );
    }
    
    public void addParentNodes( Collection<BNNodeSketch> parentNodes ){
        addParentNodes( parentNodes.toArray( new BNNodeSketch[parentNodes.size()] ) );
    }
    
    @Override
    public boolean isMouseOver( float mouseX, float mouseY ){
        return mouseX >= x && mouseX <= x+width && mouseY >= y && mouseY <= y+height;
    }

    @Override
    public void draw() {
                
        if( expanded ){

            // NodeTitle
            p.fill(0);
            p.textAlign( PApplet.CENTER );
            p.textSize(TITLESIZE);
            p.text(model.name(), x + width/2, nodeTitleY );
            
            // Draw CPT Bars
            for( int r=0; r<barXYWs.length; r++ )
                for( int c=0; c<barXYWs[r].length; c++ ){
                    
                    // Draw the bar
                    setColorForCell( r, c, true, false );
                    p.rect( barXYWs[r][c][0], barXYWs[r][c][1], barXYWs[r][c][2], BARHEIGHT );
                    
                    // Link to parent
                    setColorForCell( r, -1, false, true );
                    for( Map.Entry<String,String> parent: visibleRows[r].parentAssignment().entrySet() )
                        DrawingHelpers.arrow(p,
                            parents.get(parent.getKey()).OutHandleXFor(parent.getValue()),
                            parents.get(parent.getKey()).OutHandleY(),
                            x-SPACING,
                            barXYWs[r][c][1] + BARHEIGHT/2 );
                }
            
            // Draw value labels
            {
                p.textSize(VLABELSIZE);
                int c = 0;
                for( String value : model.values() ){
                    setColorForCell( -1, c, true, false );
                    p.text(value, valueTextX[c], valueTextY );
                    ++c;
                }
            }
            
        }else{        
            // p.rectMode(PApplet.CENTER);
            p.rect(x,y,width,height);
            for( BNNodeSketch parent : parents.values() ){
                DrawingHelpers.arrow(p, parent.x, parent.y, x, y );
            }        
        }        
    }

    @Override
    public void update(float mouseX, float mouseY, float pmouseX, float pmouseY, boolean mousePressed) {
        if( focus ){
            if ( dragging && mousePressed ){
                x = oldX + mouseX - pmouseX;
                y = oldY + mouseY - pmouseY;            
            }
            oldX = x;
            oldY = y;
            dragging = mousePressed;
        }
        
        // Only recompute positions & highlighting on demand
        if( focus || focusP ) recomputeDrawing( mouseX, mouseY );
        
        focusP = focus;
    }
    
    private void setColorForCell( int row, int col, boolean fill, boolean stroke ){
        
        int color = col < 0 ? 0 : (col%2)*50;
        if( ( col != -1 && col == highlightCol ) || ( row != -1 && row == highlightRow ) ){
            color = p.color( 0, 78+color, 0 );
        }
        if( fill ) p.fill( color ); else p.noFill();
        if( stroke ) p.stroke( color ); else p.noStroke();
    }
    
    public float OutHandleXFor( String value ){
        return x + outHandleXOs.get(value);
    }
    
    public float OutHandleY(){
        return y + height;
    }

    private void recomputeDrawing(float mouseX, float mouseY) {
        
        // Recompute positions of all elements
        recomputePositions();
        
        // Highlighting
        highlightCol = -1;
        highlightRow = -1;
        // Check if mouse is over a cell
        for( int row=0; row<barXYWs.length; row++ )
            for( int col=0; col<barXYWs[row].length; col++ ){
                float
                        barX = barXYWs[row][col][0],
                        barY = barXYWs[row][col][1],
                        barW = barXYWs[row][col][2];
                if( mouseX >= barX && mouseY >= barY && mouseX <= barX + barW && mouseY <= barY + BARHEIGHT ){
                    highlightRow = row;
                    highlightCol = col;
                }
            }
        if( -1 == highlightRow ){ // No row is highlighted
            
            // Make sure parent does not highlight a column
            for( BNNodeSketch parent : parents.values() )
                parent.highlightCol = -1;
            
            // Check if mouse is over a value label
            if( mouseY >= valueTextY && mouseY <= valueTextY + VLABELSIZE ){
                int col =0;
                p.textSize( VLABELSIZE );
                for( String value : model.values() ){
                    float w = p.textWidth(value) / 2;
                    if( mouseX >= valueTextX[col] - w && mouseX <= valueTextX[col] + w )
                        highlightCol = col;
                    ++col;
                }
            }
        } else { // Row is highlighted
            
            // Highlight relevant parent columns
            for( Map.Entry<String,String> entry : visibleRows[ highlightRow ].parentAssignment().entrySet() ){
                BNNodeSketch parent = parents.get( entry.getKey() );
                parent.highlightCol = ArrayTools.firstIndexOf( entry.getValue(), parent.model.values() );
            }
        }
        
    }

    private void recomputePositions() {
        
        final int nValues = model.values().size();
        
        // Compute width
        {
            float valwidth = 10;
            for( String value : model.values() )
                valwidth = Math.max( valwidth, p.textWidth( value ) );
            width = nValues * (valwidth + SPACING);
        }

        float yc = y + TITLESIZE; // c for "cursor"
        
        // Node Title y position
        nodeTitleY = yc;

        yc += SPACING;

        // Compute CPT Bars
        {
            int r = 0;
            barXYWs = new float[model.activeCPTS().size()][nValues][];
            visibleRows = new CPTRow[barXYWs.length];
            for( CPTRow row : model.activeCPTS() ){
                visibleRows[r] = row;
                int c = 0;
                float xc = x;
                for( double cp : row ){ // cp is "conditional probability"

                    // Compute the bar
                    float w = (float) cp*width;
                    barXYWs[r][c] = new float[]{xc, yc, w};
                    xc += w;
                    ++c;
                }
                yc += BARHEIGHT + SPACING;
                ++r;
            }
        }

        // Compute value label and outgoing handle positions
        {
            yc += VLABELSIZE;
            valueTextY = yc;
            if( valueTextX == null || valueTextX.length != nValues )
                valueTextX = new float[nValues];
            float w = width/nValues, wc = w/2;
            int c = 0;
            for( String value : model.values() ){
                valueTextX[c++] = x+wc;
                outHandleXOs.put( value, wc );
                wc += w;
            }
        }

        height = yc-y;
    }

    @Override
    public void init() {
        recomputePositions();
    }

    @Override
    public float getMinX() {
        return x;
    }

    @Override
    public float getMinY() {
        return y;
    }

    @Override
    public float getMaxX() {
        return x + width;
    }

    @Override
    public float getMaxY() {
        return y + width;
    }
}

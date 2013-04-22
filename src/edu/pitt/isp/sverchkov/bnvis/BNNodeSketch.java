/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.bnvis;

import edu.pitt.isp.sverchkov.geometry.Ellipse;
import edu.pitt.isp.sverchkov.geometry.Point;
import edu.pitt.isp.sverchkov.geometry.LineTerminus;
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
    public static final float SQRT4THIRDS = (float) Math.sqrt( 4/3 );
    
    // Colors encoded as 0xAARRGGBB
    public static final int[]
            HIGHLIGHT_T_COLOR = new int[]{ 0xffa69a00, 0xffbfb530 },// 0xff4e4e00, 0xff808000 }, // Target highlight
            HIGHLIGHT_P_COLOR = new int[]{ 0xff216477, 0xff39aecf },// 0xff004e00, 0xff008000 }, // Primary highlight
            HIGHLIGHT_S_COLOR = new int[]{ 0xffbf3030, 0xffff4040 };// 0xff4e0000, 0xff800000 }; // Secondaty highlight
    public static final int PASSIVE_EDGE_COLOR = 0x30000000;
    // Greyscale encoded as 0xWW
    public static final int NODE_EDGE_COLOR = 0;
    public static final int NODE_COLOR = 0;
    public static final int[] NO_HIGHLIGHT_COLOR = new int[]{ 75, 125 };
    
    private final Map<String,BNNodeSketch> parents = new HashMap<>();
    private final Map<String,Float> outHandleXOs = new HashMap<>(); // X offset of outgoing handles
    private final BNNodeModel model;
    
    private float x,y,width = 50,height = 50;
    private float oldX, oldY;
    private boolean dragging = false;
    private boolean expanded = false;
    private boolean focusP = false;
    private boolean mousePressedP = false;
    
    private float nodeTitleY;
    private float[][][] barXYWs;
    private CPTRow[] visibleRows;
    private float[] valueTextX;
    private float valueTextY;
    private int highlightCol = -1, highlightRow = -1;
    private boolean myHighlight = false;
    
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
                
        p.textAlign( PApplet.CENTER );
            
        if( expanded ){

            // NodeTitle
            p.fill(0);
            p.textSize(TITLESIZE);
            p.text(model.name(), x + width/2, nodeTitleY );
            
            // Draw CPT Bars
            for( int r=0; r<barXYWs.length; r++ )
                for( int c=0; c<barXYWs[r].length; c++ ){
                    
                    // Draw the bar
                    setColorForCell( r, c, true, false );
                    p.rect( barXYWs[r][c][0], barXYWs[r][c][1], barXYWs[r][c][2], BARHEIGHT );
                    
                    // Link to parent
                    if( r == highlightRow ) setColorForCell( r, -1, false, true );
                    else p.stroke( PASSIVE_EDGE_COLOR );
                    
                    for( Map.Entry<String,String> entry: visibleRows[r].parentAssignment().entrySet() ){
                        // Determine if link should come from left or right
                        BNNodeSketch parent = parents.get(entry.getKey());
                        LineTerminus
                                source = parent.outHandle( entry.getValue() ),
                                dest = new Point( x + ( source.getLineTarget().x < x + width/2 ? -SPACING : width+SPACING ), barXYWs[r][c][1] + BARHEIGHT/2 );
                        DrawingHelpers.arrow(p, source, dest );
                    }
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
            p.ellipseMode( PApplet.CORNER );
            p.noFill();
            p.stroke(NODE_COLOR);
            p.ellipse(x, y, width, height);
            p.textSize(TITLESIZE);
            p.noStroke();
            p.fill(NODE_COLOR);
            p.text( model.name(), x+width/2, y+height/2+TITLESIZE/2);
            for( BNNodeSketch parent : parents.values() ){
                p.stroke(NODE_EDGE_COLOR);
                DrawingHelpers.arrow(p, parent.outHandle(null), new Ellipse( x, y, width, height ) );
            }        
        }        
    }

    @Override
    public void update(float mouseX, float mouseY, float pmouseX, float pmouseY, boolean mousePressed, int mouseButton ) {
        if( focus ){
            if ( dragging && mousePressedP && mousePressed && mouseButton == PApplet.LEFT ){
                x = oldX + mouseX - pmouseX;
                y = oldY + mouseY - pmouseY;            
            }
            oldX = x;
            oldY = y;
            dragging = mousePressed;
            
            // Expand/collapse node
            if( mousePressedP && !mousePressed && mouseButton == PApplet.RIGHT )
                expanded = !expanded;
        }
        
        // Only recompute positions & highlighting on demand
        if( focus || focusP ) recomputeDrawing( mouseX, mouseY );
        
        focusP = focus;
        
        mousePressedP = mousePressed && focus;
    }
    
    private void setColorForCell( final int row, final int col, final boolean fill, final boolean stroke ){
        
        final boolean
                rowHighlight = row != -1 && row == highlightRow,
                colHighlight = col != -1 && col == highlightCol;
        final int color = (
            rowHighlight ?
                ( colHighlight ? HIGHLIGHT_T_COLOR : HIGHLIGHT_S_COLOR ) :
                ( colHighlight ? ( myHighlight ? HIGHLIGHT_P_COLOR : HIGHLIGHT_S_COLOR ) :
                    NO_HIGHLIGHT_COLOR ) )[ col>0 ? col%2 : 0 ];
        
        /*
        if( col != -1 && col == highlightCol )
            if( row != -1 && row == highlightRow )
                color = [c];
            else color = ( myHighlight ? HIGHLIGHT_P_COLOR : HIGHLIGHT_S_COLOR )[c];
        
        
        int color = col < 0 ? 0 : (col%2)*50;
        if( ( col != -1 && col == highlightCol ) || ( row != -1 && row == highlightRow ) ){
            color = p.color(0,78+color,0);
        }
        */
        if( fill ) p.fill( color ); else p.noFill();
        if( stroke ) p.stroke( color ); else p.noStroke();
    }
    
    public LineTerminus outHandle( String value ){
        if( expanded && outHandleXOs.containsKey( value ) )
            return new Point( x + outHandleXOs.get( value ), y + height );
        return new Ellipse( x, y, width, height );
    }

    private void recomputeDrawing(final float mouseX, final float mouseY) {
        
        // Recompute positions of all elements
        recomputePositions();
        
        myHighlight = false;

        if( expanded ){
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
                    if( mouseX >= barX && mouseY >= barY-SPACING/2 && mouseX <= barX + barW && mouseY <= barY+SPACING/2+BARHEIGHT ){
                        highlightRow = row;
                        highlightCol = col;
                        myHighlight = true;
                    }
                }
            if( -1 == highlightRow ){ // No row is highlighted

                // Make sure parent does not highlight a column
                for( BNNodeSketch parent : parents.values() )
                    parent.highlightCol = -1;

                // Check if mouse is over a value label
                if( mouseY >= valueTextY-VLABELSIZE && mouseY <= valueTextY ){
                    int col = 0;
                    p.textSize( VLABELSIZE );
                    for( String value : model.values() ){
                        float w = p.textWidth(value) / 2;
                        if( mouseX >= valueTextX[col] - w && mouseX <= valueTextX[col] + w ){
                            highlightCol = col;
                            myHighlight = true;
                        }
                        ++col;
                    }
                }
            } else { // Row is highlighted

                // Highlight relevant parent columns
                for( Map.Entry<String,String> entry : visibleRows[ highlightRow ].parentAssignment().entrySet() ){
                    BNNodeSketch parent = parents.get( entry.getKey() );
                    parent.highlightCol = ArrayTools.firstIndexOf( entry.getValue(), parent.model.values() );
                    parent.myHighlight = false; // Might not be needed
                }
            }
        }
    }

    private void recomputePositions() {

        if( expanded ){

            final int nValues = model.values().size();

            // Compute width
            {
                float valwidth = 10;
                p.textSize( VLABELSIZE );
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
            
            yc += SPACING;

            height = yc-y;
        }else{
            p.textSize( TITLESIZE );
            width = SQRT4THIRDS*(p.textWidth( model.name() ) + 2*SPACING);
            height = 2*(TITLESIZE+2*SPACING);
        }        
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
    
    public String name(){
        return model.name();
    }
}

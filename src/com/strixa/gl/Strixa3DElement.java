/**
 * File:  Strixa2DElement.java
 * Date of Creation:  Jul 17, 2012
 */
package com.strixa.gl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import com.strixa.gl.StrixaPolygon.StrixaPolygonUpdateListener;
import com.strixa.gl.properties.Cuboid;
import com.strixa.util.Dimension2D;
import com.strixa.util.Point3D;


/**
 * Creates an object to be displayed on a 2D plane.
 *
 * @author Nicholas Rogé
 */
public abstract class Strixa3DElement extends StrixaGLElement implements StrixaPolygonUpdateListener{   
    private final List<StrixaPolygon> __components = new ArrayList<StrixaPolygon>();
    private final Point3D<Double>     __coordinates = new Point3D<Double>(0.0,0.0,0.0);
    
    private Cuboid __bounding_box;
    
    
    /*Begin Constructor*/
    /**
     * Constructs a basic Strixa3DElement.
     */
    public Strixa3DElement(){
        this._regenerateBoundingBox();
    }
    /*End Constructor*/
    
    /*Begin Getter/Setter Methods*/
    /**
     * Gets the box which completely and exactly encloses all of this element.
     * 
     * @return The box which completely and exactly encloses all of this element.
     */
    public Cuboid getBoundingBox(){
       return this.__bounding_box; 
    }
    
    protected void _regenerateBoundingBox(){
        final Point3D<Double> this_coordinates = this.getCoordinates();
        
        Point3D<Double> coordinates = null;
        double          depth = 0.0;
        double          height = 0.0;
        double          width = 0.0;
        
        
        if(!this.getComponents().isEmpty()){
            width = this_coordinates.getX();
            height = this_coordinates.getY();
            depth = this_coordinates.getZ();
            
            for(StrixaPolygon polygon:this.getComponents()){
                for(StrixaPoint point:polygon.getPoints()){
                    coordinates = point.getCoordinates();

                    width = Math.max(width,coordinates.getX());
                    height = Math.max(height,coordinates.getY());
                    depth = Math.max(depth,coordinates.getZ());
                }
            }
            
            width -= this_coordinates.getX();
            height -= this_coordinates.getY();
            depth -= this_coordinates.getZ();
        }
        
        this.__bounding_box = new Cuboid(
            new Point3D<Double>(this_coordinates),
            width,
            height,
            depth
        );
    }
    
    /**
     * Gets the list of components currently added to this element.
     * 
     * @return The list of components currently added to this element.
     */
    public List<StrixaPolygon> getComponents(){
        return this.__components;
    }
    
    /**
     * Gets this element's coordinates in the form of a Point object.
     * 
     * @return This element's coordinates in the form of a Point object.
     */
    public Point3D<Double> getCoordinates(){        
        return this.__coordinates;
    }
    
    /**
     * Sets this element's alpha.
     * 
     * @param alpha Alpha transparency this element should take on.  This should be a value between 0 and 1.
     */
    public void setAlpha(byte alpha){
        for(StrixaPolygon component:this.__components){
            component.setAlpha(alpha);
        }
    }
    
    /**
     * Sets this element's colour.
     * 
     * @param colour Colour this element should be set to.
     */
    public void setColour(Color colour){
        for(StrixaPolygon component:this.__components){
            component.setColour(colour);
        }
    }
    
    /**
     * Sets this element's colour.
     * 
     * @param red Red component of this element's colour.  This should be a value between 0 and 1.
     * @param green Green component of this element's colour.  This should be a value between 0 and 1.
     * @param blue Blue component of this element's colour.  This should be a value between 0 and 1.
     */
    public void setColour(float red,float green,float blue){
        for(StrixaPolygon component:this.__components){
            component.setColour(red,green,blue);
        }
    }
    
    /**
     * Sets this element's coordinates.
     * 
     * @param x X coordinate this object should be moved to.
     * @param y Y coordinate this object should be moved to.
     * @param z Z coordinate this object should be moved to.
     */
    public void setCoordinates(double x,double y,double z){
        this.getCoordinates().setPoint(x,y,z);
        
        this._regenerateBoundingBox();
    }
    /*End Getter/Setter Methods*/
    
    /*Begin Other Methods*/
    /**
     * Adds a polygon to this element.  If the polygon already exists within this element, it will not be added again.
     * 
     * @param polygon Polygon to add to this element.
     */
    public void addComponent(StrixaPolygon polygon){
        if(!this.__components.contains(polygon)){
            this.__components.add(polygon);
        }
        
        this._regenerateBoundingBox();
    }
    
    /**
     * Adds the polygons in the given list to this element.
     * 
     * @param polygon_list Polygons to be added.
     */
    public void addComponents(List<StrixaPolygon> polygons){
        for(StrixaPolygon polygon:polygons){
            if(!this.__components.contains(polygon)){
                this.__components.add(polygon);
            }
        }
        
        this._regenerateBoundingBox();
    }
    
    public void draw(GL2 gl){
        final Point3D<Double> this_coordinates = this.getCoordinates();
        
        
        gl.glPushMatrix();
        gl.glTranslated(this_coordinates.getX(),this_coordinates.getY(),this_coordinates.getZ());
        for(StrixaPolygon polygon:this.__components){
            polygon.draw(gl);
        }
        gl.glPopMatrix();
    }
    
    /**
     * Method to check for collision with another object.
     * 
     * @param element Element who you're trying to detect if this object is colliding with.
     * 
     * @return Returns true if this object is colliding with the given object, and false, otherwise. 
     */
    public boolean isColliding(Strixa3DElement element){
        for(StrixaPolygon our_polygon:this.getComponents()){
            for(StrixaPolygon their_polygon:element.getComponents()){
                if(our_polygon.isColliding(their_polygon)){
                    return true;
                }
            }
        }
        
        return false;
    }
    
    
    
    public boolean isVisible(StrixaGLContext context){
        return true;
    }
    
    public void onStrixaPolygonUpdate(StrixaPolygon polygon){
        this._regenerateBoundingBox();
    }
    /*End Other Methods*/
    
    /*Begin Abstract Methods*/
    public abstract Dimension2D<Double> getDimensions();
    /*End Abstract Methods*/
}

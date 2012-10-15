/**
 * File:  Strixa2DElement.java
 * Date of Creation:  Jul 17, 2012
 */
package com.strixa.gl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLContext;

import com.strixa.gl.StrixaPolygon.StrixaPolygonUpdateListener;
import com.strixa.gl.properties.Cuboid;
import com.strixa.util.Dimension2D;
import com.strixa.util.Dimension3D;
import com.strixa.util.Point3D;


/**
 * Creates an object to be displayed on a 2D plane.
 *
 * @author Nicholas Rogé
 */
public class Strixa3DElement extends StrixaGLElement implements StrixaPolygonUpdateListener{    
    private static int __assigned_list_count = 0;
    
    private final List<StrixaPolygon> __components = new ArrayList<StrixaPolygon>();
    private final Point3D<Double>     __coordinates = new Point3D<Double>(0.0,0.0,0.0);
    
    private Cuboid          __bounding_box;
    private Integer         __list_index;
    
    
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
    
    public Dimension3D<Double> getDimensions(){
        return this.__bounding_box.getDimensions();
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
        final List<StrixaPolygon> polygons = this.getComponents();
        final int                 polygon_count = polygons.size();
        
        
        for(int index = 0;index < polygon_count;index++){
            polygons.get(index).setAlpha(alpha);
        }
    }
    
    /**
     * Sets this element's colour.
     * 
     * @param colour Colour this element should be set to.
     */
    public void setColour(Color colour){
        final List<StrixaPolygon> polygons = this.getComponents();
        final int                 polygon_count = polygons.size();
        
        
        for(int index = 0;index < polygon_count;index++){
            polygons.get(index).setColour(colour);
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
        final List<StrixaPolygon> polygons = this.getComponents();
        final int                 polygon_count = polygons.size();
        
        
        for(int index = 0;index < polygon_count;index++){
            polygons.get(index).setColour(red,green,blue);
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
        int polygon_count = polygons.size();
        
        
        for(int index = 0;index < polygon_count;index++){
            if(!this.__components.contains(polygons.get(index))){
                this.__components.add(polygons.get(index));
            }
        }
        
        this._regenerateBoundingBox();
    }
    
    public void draw(GL2 gl){
        final Point3D<Double> this_coordinates = this.getCoordinates();
        
        if(this.__list_index == null){
            this.__list_index = gl.glGenLists(1);
            gl.glNewList(this.__list_index,GL2.GL_COMPILE);
            
            gl.glPushMatrix();
            gl.glTranslated(this_coordinates.getX(),this_coordinates.getY(),this_coordinates.getZ());        
            
            for(int index = 0,end_index = this.__components.size();index < end_index;index++){                
                this.__components.get(index).draw(gl);
            }
            
            gl.glPopMatrix();
            
            gl.glEndList();
        }
        
        
        gl.glCallList(this.__list_index);
    }
    
    /**
     * Method to check for collision with another object.
     * 
     * @param element Element who you're trying to detect if this object is colliding with.
     * 
     * @return Returns true if this object is colliding with the given object, and false, otherwise. 
     */
    public boolean isColliding(Strixa3DElement element){
        //TODO_HIGH:  Renivate this method
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
        final Cuboid          bounding_box = this.getBoundingBox();
        final Point3D<Double> bounding_box_coordinates = bounding_box.getCoordinates();
        final Cuboid          viewable_area = context.getViewableArea();
        
        
        if(viewable_area.isPointInside(bounding_box_coordinates)){
            return true;
        }else if(viewable_area.isPointInside(new Point3D<Double>(
            bounding_box_coordinates.getX() + bounding_box.getWidth(),
            bounding_box_coordinates.getY(),
            bounding_box_coordinates.getZ()
        ))){
            return true;
        }else if(viewable_area.isPointInside(new Point3D<Double>(
            bounding_box_coordinates.getX(),
            bounding_box_coordinates.getY() + bounding_box.getHeight(),
            bounding_box_coordinates.getZ()
        ))){
            return true;
        }else if(viewable_area.isPointInside(new Point3D<Double>(
            bounding_box_coordinates.getX(),
            bounding_box_coordinates.getY(),
            bounding_box_coordinates.getZ() + bounding_box.getDepth()
        ))){
            return true;
        }else if(viewable_area.isPointInside(new Point3D<Double>(
            bounding_box_coordinates.getX() + bounding_box.getWidth(),
            bounding_box_coordinates.getY() + bounding_box.getHeight(),
            bounding_box_coordinates.getZ()
        ))){
            return true;
        }else if(viewable_area.isPointInside(new Point3D<Double>(
            bounding_box_coordinates.getX(),
            bounding_box_coordinates.getY() + bounding_box.getHeight(),
            bounding_box_coordinates.getZ() + bounding_box.getDepth()
        ))){
            return true;
        }else if(viewable_area.isPointInside(new Point3D<Double>(
            bounding_box_coordinates.getX() + bounding_box.getWidth(),
            bounding_box_coordinates.getY(),
            bounding_box_coordinates.getZ() + bounding_box.getDepth()
        ))){
            return true;
        }else if(viewable_area.isPointInside(new Point3D<Double>(
            bounding_box_coordinates.getX() + bounding_box.getWidth(),
            bounding_box_coordinates.getY() + bounding_box.getHeight(),
            bounding_box_coordinates.getZ() + bounding_box.getDepth()
        ))){
            return true;
        }else{        
            return false;
        }
    }
    
    public void onStrixaPolygonUpdate(StrixaPolygon polygon){
        this._regenerateBoundingBox();
    }
    
    protected void _regenerateBoundingBox(){
        final List<StrixaPolygon> polygons = this.getComponents();
        final int                 polygon_count = polygons.size();
        final Point3D<Double>     this_coordinates = this.getCoordinates();
        
        Point3D<Double>   coordinates = null;
        double            depth = 0.0;
        double            height = 0.0;
        int               point_count = 0;
        List<StrixaPoint> points = null;
        double            width = 0.0;
        
        
        if(!this.getComponents().isEmpty()){
            width = this_coordinates.getX();
            height = this_coordinates.getY();
            depth = this_coordinates.getZ();
            
            for(int polygon_index = 0;polygon_index < polygon_count;polygon_index++){
                points = polygons.get(polygon_index).getPoints();
                point_count = points.size();
                
                for(int point_index = 0;point_index < point_count;point_index++){
                    coordinates = points.get(point_index).getCoordinates();

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
    /*End Other Methods*/
    
    /*Begin Static Methods*/
    public static int getNewListIndex(){
        final int index = Strixa3DElement.__assigned_list_count;
        
        
        Strixa3DElement.__assigned_list_count++;
        
        return index;
    }
    /*End Static Methods*/
}

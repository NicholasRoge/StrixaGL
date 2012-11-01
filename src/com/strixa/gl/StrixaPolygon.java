/**
 * File:  StrixaPolygon.java
 * Date of Creation:  Jul 29, 2012
 */
package com.strixa.gl;

import java.util.ArrayList;
import java.util.List;

import com.strixa.gl.properties.Cuboid;
import com.strixa.gl.util.Vertex;
import com.strixa.util.Point3D;

/**
 * Describes any useful information about a polygon.
 *
 * @author Nicholas Rogé
 */
public class StrixaPolygon{    
    private final Point3D<Double>       __coordinates = new Point3D<Double>(0.0,0.0,0.0);
    private final List<Vertex>          __normal_points = new ArrayList<Vertex>();
    private final List<Vertex>          __points = new ArrayList<Vertex>();
    private final List<Vertex>          __texture_points = new ArrayList<Vertex>();
    private final List<Strixa3DElement> __parents = new ArrayList<Strixa3DElement>();
    
    private Cuboid         __bounding_box;
    
    
    /*Begin Constructors*/
    /**
     * Constructs the polygon.
     */
    public StrixaPolygon(){
    }
    /*End Constructors*/
    
    /*Begin Getter/Setter Methods*/
    /**
     * Gets the box which completely and exactly encloses all of this polygon.
     * 
     * @return The box which completely and exactly encloses all of this polygon.
     */
    public Cuboid getBoundingBox(){
        return this.__bounding_box;
    }
    
    /**
     * Gets the list of normal points associated with this object.<br />
     * 
     * @return The list of normal points.
     */
    public List<Vertex> getNormalPoints(){
        return this.__normal_points;
    }
    
    /**
     * Gets the list of coordinate points associated with this object.<br />
     * 
     * @return The list of coordinate points.
     */
    public List<Vertex> getPoints(){
        return this.__points;
    }
    
    /**
     * Gets the list of texture points associated with this object.<br />
     * <strong>Note:</strong>  It's possible for this object to have no texture coordinates, in which case the list returned by this method will be empty.
     * 
     * @return The list of texture points.
     */
    public List<Vertex> getTexturePoints(){
        return this.__texture_points;
    }
    
    /**
     * Gets this polygon's current location.
     * 
     * @return This polygons's current location.
     */
    public Point3D<Double> getCoordinates(){
        return this.__coordinates;
    }
    
    /**
     * Gets an array of {@link Strixa3DElement} to which this polygon is associated.
     * 
     * @return An array of {@link Strixa3DElement} to which this polygon is associated.
     */
    public Strixa3DElement[] getParents(){
        return this.__parents.toArray(new Strixa3DElement[this.__parents.size()]);
    }
    
    /**
     * Changes this polygon's location.
     * 
     * @param coordinates Coordinates to move this polygon to.
     */
    public void setCoordinates(Point3D<Double> coordinates){
        this.setCoordinates(coordinates.getX(),coordinates.getY(),coordinates.getZ());
    }
    
    /**
     * Changes this polygon's location.
     * 
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z coordinate.
     */
    public void setCoordinates(double x,double y,double z){
        this.__coordinates.setPoint(x,y,z);
    }
    /*End Getter/Setter Methods*/
    
    /*Begin Other Methods*/
    /**
     * Adds a normal points to this polygon.
     * 
     * @param point Normal point to be added.
     */
    public void addNormalPoint(Vertex point){
        this.__normal_points.add(point);
    }
    
    /**
     * Adds a list of normal points to this polygon. 
     * 
     * @param normal_points List of normal points to be added.
     */
    public void addNormalPoints(List<Vertex> normal_points){
        this.__normal_points.addAll(normal_points);
    }
    
    /**
     * Adds a {@link Strixa3DElement} to this polygon, enabling it to communicate with it's parent.<br />
     * <strong>Note:</strong>  This method should typically only be called internally by Strixa3DElement.
     * 
     * @param parent Parent element.
     */
    public void addParent(Strixa3DElement parent){
        if(!this.__parents.contains(parent)){
            this.__parents.add(parent);
        }
    }
    
    /**
     * Adds a point to this polygon.
     * 
     * @param point Point to be added.
     */
    public void addPoint(Vertex point){
        this.__points.add(point);
        
        this.invalidate();
    }
    
    /**
     * Adds a list of points to this polygon.
     * 
     * @param points Point list to be added.
     */
    public void addPoints(List<Vertex> points){
        this.__points.addAll(points);
        
        this.invalidate();
    }
    
    /**
     * Adds a texture point to this polygon.
     * 
     * @param texture_point Texture point to be added.
     */
    public void addTexturePoint(Vertex texture_point){
        this.__texture_points.add(texture_point);
    }
    
    /**
     * Adds a list of texture points to this polygon.
     * 
     * @param texture_points Texture points to be added.
     */
    public void addTexturePoints(List<Vertex> texture_points){
        this.__texture_points.addAll(texture_points);
    }
    
    /**
     * Indicates that something about this element has changed, and that it should be recreated.
     */
    public void invalidate(){
        this._regenerateBoundingBox();
        
        for(int index = 0,end_index = this.__parents.size() - 1;index <= end_index;index++){
            this.__parents.get(index).invalidate();
        }
    }
    
    /**
     * Simple check to determine whether this polygon is visible in the current context.
     * 
     * @param context The context in which the StrixaGL application is currently being run.
     * 
     * @return Returns true if this polygon is visible and should be drawn, and false, otherwise.
     */
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
    
    /**
     * Regenerates the element's bounding box.
     */
    protected void _regenerateBoundingBox(){
        final List<Vertex>      points = this.getPoints();
        final int               point_count = points.size();
        final Point3D<Double>   this_coordinates = this.getCoordinates();
        
        Vertex coordinates = null;
        double          depth = 0.0;
        double          height = 0.0;
        double          width = 0.0;
        
        
        if(!this.getPoints().isEmpty()){
            width = this_coordinates.getX();
            height = this_coordinates.getY();
            depth = this_coordinates.getZ();
            
            for(int index = 0;index < point_count;index++){
                coordinates = points.get(index);
                
                
                width = Math.max(width,coordinates.getX());
                height = Math.max(height,coordinates.getY());
                depth = Math.max(depth,coordinates.getZ());
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
     * Removes the given point from this polygon.
     * 
     * @param point Point to remove.
     */
    public void removePoint(Vertex point){
        if(this.__points.contains(point)){
            this.__points.remove(point);
            
            this.invalidate();
        }
    }
    
    /**
     * Removes the requested parent from the polygon.
     * 
     * @param parent Parent to be removed.
     *///See also:  PPA, the Polygon Protection Agency.
    public void removeParent(Strixa3DElement parent){
        if(this.__parents.contains(parent)){
            this.__parents.remove(parent);
        }
    }

    /**
     * Sets the list of points for this polygon to draw.
     * 
     * @param points The list of points for this polygon to draw.
     */
    protected void _setPoints(List<Vertex> points){
        this.__points.clear();
        this.__points.addAll(points);
    }
    /*End Abstract Methods*/
    
    /*Begin Static Methods*/
    /**
     * By checking to see if any of this polygon's lines are intersecting with the second polygon's lines, this method determines if the given element is colliding with this one.<br />
     * <strong>Note:</strong>  An element whose entire being is within this element is not considered to be colliding.
     * 
     * @param element Element who you're trying to detect if this object is colliding with.
     * 
     * @return Returns true if this object is colliding with the given object, and false, otherwise. 
     */
    public boolean isColliding(StrixaPolygon element){  //TODO_HIGH:  This method needs heavy optimization.  Rather than creating a bunch of new objects, a list could be created, for example.        
        //TODO_HIGH:  Code temporarily removed from this method
        
        return false;
    }
    /*End Static Methods*/
}

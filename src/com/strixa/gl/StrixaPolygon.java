/**
 * File:  StrixaPolygon.java
 * Date of Creation:  Jul 29, 2012
 */
package com.strixa.gl;

import java.util.ArrayList;
import java.util.List;

import com.strixa.gl.StrixaPoint.StrixaPointLocationUpdateListener;
import com.strixa.gl.properties.Cuboid;
import com.strixa.util.Line;
import com.strixa.util.Point2D;
import com.strixa.util.Point3D;

/**
 * TODO:  Write Class Description
 *
 * @author Nicholas Rogé
 */
public class StrixaPolygon implements StrixaPointLocationUpdateListener{
    /**
     * Classes wishing to receive updates from this polygon should implement this interface.
     *
     * @author Nicholas Rogé
     */
    public interface StrixaPolygonUpdateListener{
        /**
         * Called when a polygon performs an update.
         * 
         * @param polygon Polygon which has been updated.
         */
        public void onStrixaPolygonUpdate(StrixaPolygon polygon);
    }
    
    private final Point3D<Double>                   __coordinates = new Point3D<Double>(0.0,0.0,0.0);
    private final List<Point3D<Double>>             __normal_points = new ArrayList<Point3D<Double>>();
    private final List<StrixaPoint>                 __points = new ArrayList<StrixaPoint>();
    final List<Point2D<Double>>                     __texture_points = new ArrayList<Point2D<Double>>();
    private final List<StrixaPolygonUpdateListener> __update_listeners = new ArrayList<StrixaPolygonUpdateListener>();
    
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
    public List<Point3D<Double>> getNormalPoints(){
        return this.__normal_points;
    }
    
    /**
     * Gets the list of coordinate points associated with this object.<br />
     * 
     * @return The list of coordinate points.
     */
    public List<StrixaPoint> getPoints(){
        return this.__points;
    }
    
    /**
     * Gets the list of texture points associated with this object.<br />
     * <strong>Note:</strong>  It's possible for this object to have no texture coordinates, in which case the list returned by this method will be empty.
     * 
     * @return The list of texture points.
     */
    public List<Point2D<Double>> getTexturePoints(){
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
        
        this._notifiyStrixaPolygonUpdateListeners();
    }
    /*End Getter/Setter Methods*/
    
    /*Begin Other Methods*/
    /**
     * Adds a normal points to this polygon.
     * 
     * @param point Normal point to be added.
     */
    public void addNormalPoint(Point3D<Double> point){
        this.__normal_points.add(point);
        
        this._notifiyStrixaPolygonUpdateListeners();
    }
    
    /**
     * Adds a list of normal points to this polygon. 
     * 
     * @param normal_points List of normal points to be added.
     */
    public void addNormalPoints(List<Point3D<Double>> normal_points){
        this.__normal_points.addAll(normal_points);
        
        this._notifiyStrixaPolygonUpdateListeners();
    }
    
    /**
     * Adds a point to this polygon.
     * 
     * @param point Point to be added.
     */
    public void addPoint(StrixaPoint point){
        this.__points.add(point);
        
        this._notifiyStrixaPolygonUpdateListeners();
    }
    
    /**
     * Adds a list of points to this polygon.
     * 
     * @param points Point list to be added.
     */
    public void addPoints(List<StrixaPoint> points){
        this.__points.addAll(points);
        
        this._notifiyStrixaPolygonUpdateListeners();
    }
    
    /**
     * Adds a texture point to this polygon.
     * 
     * @param texture_point Texture point to be added.
     */
    public void addTexturePoint(Point2D<Double> texture_point){
        this.__texture_points.add(texture_point);
        
        this._notifiyStrixaPolygonUpdateListeners();
    }
    
    /**
     * Adds a list of texture points to this polygon.
     * 
     * @param texture_points Texture points to be added.
     */
    public void addTexturePoints(List<Point2D<Double>> texture_points){
        this.__texture_points.addAll(texture_points);
        
        this._notifiyStrixaPolygonUpdateListeners();
    }
    
    /**
     * Adds a listener to be called if anything happens to update this polygon.
     * 
     * @param listener Listener to be called.
     */
    public void addStrixaPolygonUpdateListener(StrixaPolygonUpdateListener listener){
        if(!this.__update_listeners.contains(listener)){
            this.__update_listeners.add(listener);
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
     * Notifies any listeners that there has been an update to this polygon.
     */
    protected void _notifiyStrixaPolygonUpdateListeners(){
        for(StrixaPolygonUpdateListener listener:StrixaPolygon.this.__update_listeners){
            listener.onStrixaPolygonUpdate(StrixaPolygon.this);
        }
    }
    
    public void onStrixaPointLocationUpdate(StrixaPoint point){
        this._regenerateBoundingBox();
        
        this._notifiyStrixaPolygonUpdateListeners();
    }
    
    /**
     * Regenerates the element's bounding box.
     */
    protected void _regenerateBoundingBox(){
        final List<StrixaPoint> points = this.getPoints();
        final int               point_count = points.size();
        final Point3D<Double>   this_coordinates = this.getCoordinates();
        
        Point3D<Double> coordinates = null;
        double          depth = 0.0;
        double          height = 0.0;
        double          width = 0.0;
        
        
        if(!this.getPoints().isEmpty()){
            width = this_coordinates.getX();
            height = this_coordinates.getY();
            depth = this_coordinates.getZ();
            
            for(int index = 0;index < point_count;index++){
                coordinates = points.get(index).getCoordinates();
                
                
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
    public void removePoint(StrixaPoint point){
        if(this.__points.contains(point)){
            this.__points.remove(point);
            
            this._notifiyStrixaPolygonUpdateListeners();
        }
    }
    
    /**
     * Removes the requested listener from the update list.
     * 
     * @param listener Listener to be removed.
     */
    public void removeStrixaPolygonUpdateListener(StrixaPolygonUpdateListener listener){
        if(this.__update_listeners.contains(listener)){
            this.__update_listeners.remove(listener);
        }
    }

    /**
     * Sets the list of points for this polygon to draw.
     * 
     * @param points The list of points for this polygon to draw.
     */
    protected void _setPoints(List<StrixaPoint> points){
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
        final int this_point_count = this.__points.size();
        final int element_point_count = element.__points.size();
        
        Point3D<Double> adjusted_point_one = null;
        Point3D<Double> adjusted_point_two = null;
        Line polygon_one_line = null;
        Line polygon_two_line = null;
        
        
        for(int index=0;index<this_point_count;index++){
            /*Set up the first point*/
            if(index==0){
                adjusted_point_one = new Point3D<Double>(this.__points.get(this_point_count-1).getCoordinates());
            }else{
                adjusted_point_one = new Point3D<Double>(this.__points.get(index-1).getCoordinates());
                
            }
            adjusted_point_one.setX(adjusted_point_one.getX()+this.getCoordinates().getX());
            adjusted_point_one.setY(adjusted_point_one.getY()+this.getCoordinates().getY());
            
            /*Set up the second point*/
            adjusted_point_two = new Point3D<Double>(this.__points.get(index).getCoordinates());
            adjusted_point_two.setX(adjusted_point_two.getX()+this.getCoordinates().getX());
            adjusted_point_two.setY(adjusted_point_two.getY()+this.getCoordinates().getY());
            
            /*Create teh first line*/
            polygon_one_line = new Line(adjusted_point_one,adjusted_point_two);
            
            for(int sub_index=0;sub_index<element_point_count;sub_index++){
                if(sub_index==0){
                    adjusted_point_one = new Point3D<Double>(element.__points.get(element_point_count-1).getCoordinates());
                }else{
                    adjusted_point_one = new Point3D<Double>(element.__points.get(sub_index-1).getCoordinates());
                }
                adjusted_point_one.setX(adjusted_point_one.getX()+element.getCoordinates().getX());
                adjusted_point_one.setY(adjusted_point_one.getY()+element.getCoordinates().getY());
                
                adjusted_point_two = new Point3D<Double>(element.__points.get(sub_index).getCoordinates());
                adjusted_point_two.setX(adjusted_point_two.getX()+element.getCoordinates().getX());
                adjusted_point_two.setY(adjusted_point_two.getY()+element.getCoordinates().getY());
                
                polygon_two_line = new Line(adjusted_point_one,adjusted_point_two);
                
                if(Line.getIntersectionPoint(polygon_one_line,polygon_two_line)!=null){
                    return true;
                }
            }
            
            /*Now check for 3-Dimensional collision*/
            //TODO_HIGH:  Check for 3-Dimensional collision...  Rofl.
        }
        
        return false;
    }
    /*End Static Methods*/
}

/**
 * File:  StrixaPolygon.java
 * Date of Creation:  Jul 29, 2012
 */
package com.strixa.gl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

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
public abstract class StrixaPolygon implements StrixaPointLocationUpdateListener{
    public interface StrixaPolygonUpdateListener{
        public void onStrixaPolygonUpdate(StrixaPolygon polygon);
    }
    
    private final Point3D<Double>                   __coordinates = new Point3D<Double>(0.0,0.0,0.0);
    private final List<StrixaPoint>                 __points = new ArrayList<StrixaPoint>();
    private final List<StrixaPolygonUpdateListener> __update_listeners = new ArrayList<StrixaPolygonUpdateListener>();
    
    private Cuboid __bounding_box;
    
    
    /*Begin Constructors*/
    /**
     * Constructs the polygon
     */
    public StrixaPolygon(){
        super();
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
     * Gets the list of StrixaPoints which this polygon is made up of.
     * 
     * @return The list of StrixaPoints which this polygon is made up of.
     */
    public List<StrixaPoint> getPoints(){
        return this.__points;
    }
    
    /**
     * Sets the alpha property of this polygon.
     * 
     * @param alpha Visibility of the polygon.  This should be a number between 0 (not visible) and 255 (fully visible).
     */
    public void setAlpha(byte alpha){
        for(StrixaPoint point:this.__points){
            point.setAlpha(alpha);
        }
    }
    
    /**
     * Sets this polygon's colour.
     * 
     * @param red Red component of this element's colour.  This should be a value between 0 and 1.
     * @param green Green component of this element's colour.  This should be a value between 0 and 1.
     * @param blue Blue component of this element's colour.  This should be a value between 0 and 1.
     */
    public void setColour(float red,float green,float blue){
        this.setColour(new Color(red,green,blue));
    }
    
    /**
     * Sets the polygon's colour.
     * 
     * @param colour Colour the polygon should be.
     */
    public void setColour(Color colour){        
        for(StrixaPoint point:this.__points){
            point.setColour(colour);
        }
    }
    
    public Point3D<Double> getCoordinates(){
        return this.__coordinates;
    }
    
    public void setCoordinates(Point3D<Double> coordinates){
        this.setCoordinates(coordinates.getX(),coordinates.getY(),coordinates.getZ());
    }
    
    public void setCoordinates(double x,double y,double z){
        this.__coordinates.setPoint(x,y,z);
        
        this._notifiyStrixaPolygonUpdateListeners();
    }
    /*End Getter/Setter Methods*/
    
    /*Begin Other Methods*/
    public void addPoint(StrixaPoint point){
        if(!this.__points.contains(point)){
            this.__points.add(point);
            
            this._notifiyStrixaPolygonUpdateListeners();
        }
    }
    
    public void addStrixaPolygonUpdateListener(StrixaPolygonUpdateListener listener){
        if(!this.__update_listeners.contains(listener)){
            this.__update_listeners.add(listener);
        }
    }
    
    public void draw(GL2 gl){
        final Point3D<Double> this_coordinates = this.getCoordinates();
        
        Point3D<Double> coordinates = null;
        
        gl.glPushMatrix();
        gl.glTranslated(this_coordinates.getX(),this_coordinates.getY(),this_coordinates.getZ());
        
        switch(this.__points.size()){
            case 0:
            case 1:
            case 2:
                throw new RuntimeException("You must add at least 3 points to a "+this.getClass().getName()+" in order for it to be drawn.");
            case 3:
                gl.glBegin(GL2.GL_TRIANGLES);
                break;
            case 4:
                gl.glBegin(GL2.GL_QUADS);
                break;
            default:
                gl.glBegin(GL2.GL_POLYGON);
                break;
        }
        
        for(StrixaPoint point : this.__points){
            coordinates = point.getCoordinates();
            
            gl.glColor3fv(point.getColour().getColorComponents(new float[3]),point.getAlpha());
            gl.glVertex3d(coordinates.getX(),coordinates.getY(),coordinates.getZ());
        }
        gl.glEnd();
        gl.glPopMatrix();
    }
    
    protected void _notifiyStrixaPolygonUpdateListeners(){
        new Thread(new Runnable(){
            public void run(){
                for(StrixaPolygonUpdateListener listener:StrixaPolygon.this.__update_listeners){
                    listener.onStrixaPolygonUpdate(StrixaPolygon.this);
                }
            }
        }).start();
    }
    
    private void __regenerateBoundingBox(){
        final Point3D<Double> this_coordinates = this.getCoordinates();
        
        Point3D<Double> coordinates = null;
        double          depth = 0.0;
        double          height = 0.0;
        double          width = 0.0;
        
        
        if(!this.getPoints().isEmpty()){
            width = this_coordinates.getX();
            height = this_coordinates.getY();
            depth = this_coordinates.getZ();
            
            for(StrixaPoint point:this.getPoints()){
                coordinates = point.getCoordinates();
                
                
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
    
    public void removePoint(StrixaPoint point){
        if(this.__points.contains(point)){
            this.__points.remove(point);
            
            this._notifiyStrixaPolygonUpdateListeners();
        }
    }
    
    public void removeStrixaPolygonUpdateListener(StrixaPolygonUpdateListener listener){
        if(this.__update_listeners.contains(listener)){
            this.__update_listeners.remove(listener);
        }
    }
    /*End Other Methods*/
    
    /*Begin Abstract Methods*/
    /**
     * Sets the list of points for this polygon to draw.
     * 
     * @param points The list of points for this polygon to draw.
     */
    protected void _setPoints(List<StrixaPoint> points){
        this.__points.clear();
        this.__points.addAll(points);
    }
    
    public void onStrixaPointLocationUpdate(StrixaPoint point){
        this.__regenerateBoundingBox();
        
        this._notifiyStrixaPolygonUpdateListeners();
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
    public boolean isColliding(StrixaPolygon element){  //TODO_HIGH:  This method needs heavy optimization.  Rather than creating a bunch of new objects, a list could be crated, for example.
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

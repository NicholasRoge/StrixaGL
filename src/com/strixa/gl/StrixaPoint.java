/**
 * File:  StrixaPoint.java
 * Date of Creation:  Sep 22, 2012
 */
package com.strixa.gl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.strixa.util.Point3D;

/**
 * TODO:  Write Class Description
 *
 * @author Nicholas Rogé
 */
public class StrixaPoint{
    public interface StrixaPointLocationUpdateListener{
        public void onStrixaPointLocationUpdate(StrixaPoint point);
    }
    
    private final List<StrixaPointLocationUpdateListener> __location_update_listeners = new ArrayList<StrixaPointLocationUpdateListener>();
    
    private final Point3D<Double> __coordinates  = new Point3D<Double>(0.0,0.0,0.0);
    
    private Byte  __alpha; 
    private Color __colour;
    

    /*Begin Constructors*/
    /**
     * Constructs the point with the specified properties.
     * 
     * @param x_coordinate X coordinate of the point.
     * @param y_coordinate Y coordinate of the point.
     * @param z_coordinate Z coordinate of the point.
     * @param colour Colour of the point.
     * @param alpha Visibility of the point.  This should be a number between 0 (not visible) and 255 (fully visible).
     */
    public StrixaPoint(double x_coordinate,double y_coordinate,double z_coordinate,Color colour,byte alpha){
        this(new Point3D<Double>(x_coordinate,y_coordinate,z_coordinate),colour,alpha);
    }
    
    /**
     * Constructs the point with the specified properties.
     * 
     * @param coordinates Point in three-dimensional space this point should be located at.
     * @param colour Colour of the point.
     * @param alpha Visibility of the point.  This should be a number between 0 (not visible) and 255 (fully visible).
     */
    public StrixaPoint(Point3D<Double> coordinates,Color colour,Byte alpha){        
        this.setAlpha(alpha);
        this.setColour(colour);
        this.setCoordinates(coordinates);
    }
    
    /**
     * Copy constructor.
     * 
     * @param point Point whose properties are to be copied.
     */
    public StrixaPoint(StrixaPoint point){
        final Point3D<Double> coordinates = point.getCoordinates();
        
        
        this.setAlpha(point.getAlpha());
        this.setColour(point.getColour());
        this.setCoordinates(coordinates.getX(),coordinates.getY(),coordinates.getZ());
    }
    /*End Constructors*/
    
    /*Begin Getter/Setters*/
    /**
     * Gets the alpha property of this Point.
     * 
     * @return Returns the alpha property of this Point.
     */
    public Byte getAlpha(){
        return this.__alpha;
    }
    
    /**
     * Gets the colour property of this Point.
     * 
     * @return Returns the colour property of this Point.
     */
    public Color getColour(){
        return this.__colour;
    }
    
    /**
     * Gets the coordinates of this Point.
     * 
     * @return Returns the coordinates of this Point.
     */
    public Point3D<Double> getCoordinates(){
        return this.__coordinates;
    }
    
    /**
     * Sets the alpha property of this point.
     * 
     * @param alpha Visibility of the point.  This should be a number between 0 (not visible) and 255 (fully visible).
     */
    public void setAlpha(Byte alpha){
        if(alpha == null){
            throw new IllegalArgumentException("Argument 'alpha' must not be null.");
        }
        
        this.__alpha = alpha;
    }
    
    /**
     * Sets the colour property of this point.
     * 
     * @param colour Colour of the point.
     */
    public void setColour(Color colour){
        if(colour == null){
            throw new IllegalArgumentException("Argument 'colour' must not be null.");
        }
        
        this.__colour = colour;
    }
    
    /**
     * Sets the coordinates of this point.
     * 
     * @param coordinates Point in three-dimensional space this point should be located at.
     */
    public void setCoordinates(Point3D<Double> coordinates){
        if(coordinates == null){
            throw new IllegalArgumentException("Argument 'coordinates' must not be null.");
        }
        
        this.setCoordinates(coordinates.getX(),coordinates.getY(),coordinates.getZ());
    }
    
    /**
     * Sets the coordinates of this point.
     * 
     * @param x_coordinate X coordinate of the point.
     * @param y_coordinate Y coordinate of the point.
     * @param z_coordinate Z coordinate of the point.
     */
    public void setCoordinates(double x_coordinate,double y_coordinate,double z_coordinate){
        this.__coordinates.setPoint(x_coordinate,y_coordinate,z_coordinate);
        
        synchronized(StrixaPoint.this.__location_update_listeners){
            for(StrixaPointLocationUpdateListener listener:StrixaPoint.this.__location_update_listeners){
                listener.onStrixaPointLocationUpdate(StrixaPoint.this);
            }
        }
    }
    /*End Getter/Setters*/
    
    /*Begin Other Methods*/
    public void addLocationUpdateListener(StrixaPointLocationUpdateListener listener){
        if(!this.__location_update_listeners.contains(listener)){
            this.__location_update_listeners.add(listener);
        }
    }
    
    public void removeLocationUpdateListener(StrixaPointLocationUpdateListener listener){
        if(this.__location_update_listeners.contains(listener)){
            this.__location_update_listeners.remove(listener);
        }
    }
    /*End Other Methods*/
}

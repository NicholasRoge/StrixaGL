/**
 * File:  Cuboid.java
 * Date of Creation:  Jul 19, 2012
 */
package com.strixa.gl.properties;

import java.util.ArrayList;
import java.util.List;

import com.strixa.util.Point3D;

/**
 * Contains the vertices for a Cuboid object.
 *
 * @author Nicholas Rogé
 */
public class Cuboid{
    private Point3D<Double> __coordinates;
    private double          __depth;
    private double          __height;
    private double          __width;
    
    
    /*Begin Constructors*/
    /**
     * Constructs a cuboid with all vertices at (0,0,0).
     */
    public Cuboid(Point3D<Double> coordinates,double width,double height,double depth){
        this.__coordinates = coordinates;
        this.__width = width;
        this.__height = height;
        this.__depth = depth;
    }
    /*End Constructors*/
    
    /*Begin Getter/Setter Methods*/
    /**
     * Gets the coordinates of the left (most negative X), bottom (most negative Y), front (most negative Z) corner of the cuboid.
     * 
     * @return The coordinates of the left (most negative X), bottom (most negative Y), front (most negative Z) corner of the cuboid.
     */
    public Point3D<Double> getCoordinates(){
        return this.__coordinates;
    }
    
    public double getDepth(){
        return this.__depth;
    }
    
    public double getHeight(){
        return this.__height;
    }
    
    public double getWidth(){
        return this.__width;
    }
    /*End Getter/Setter Methods*/
}

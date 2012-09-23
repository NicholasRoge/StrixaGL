/**
 * File:  Point1D.java
 * Date of Creation:  Jul 17, 2012
 */
package com.strixa.util;

/**
 * Generic point class which allows for any type of numeric to be used.  
 *
 * @author Nicholas Rogé
 */
public class Point1D<T extends Number> extends Point<T>{
    private T __x;
    
    
    /*Begin Constructors*/
    /**
     * Constructs a Point1D object whose coordinates are at the given points.
     * 
     * @param x X coordinate.
     */
    public Point1D(T x){
        this.setX(x);
    }
    
    /**
     * Constructs a copy of the given Point1D object.
     * 
     * @param copy Point1D object whose data should be copied.
     */
    public Point1D(Point1D<T> copy){
        this.setX(copy.getX());
    }
    /*End Constructors*/
    
    /*Begin Getter/Setter Methods*/
    /**
     * Gets this point's X coordinate.
     * 
     * @return This point's X coordinate.
     */
    public T getX(){
        return this.__x;
    }
    
    /**
     * Sets this point's X coordinate.
     * 
     * @param x X coordinate.
     */
    public void setX(T x){
        this.__x = x;
    }
    /*End Getter/Setter Methods*/
    
    /*Begin Other Methods*/
    /**
     * Performs a check to see if the given point is at the same location as this one.
     * 
     * @param point Point to compare against.
     * 
     * @return Returns true if the given point is at the same location as this one, and false, otherwise.
     */
    public boolean equals(Point1D<T> point){
        if(this.getX()==point.getX()){
            return true;
        }
        
        return false;
    }
    
    public String toString(){
        return "("+this.getX()+")";
    }
    /*End Other Methods*/
}

/**
 * File:  vertex3D.java
 * Date of Creation:  Jul 23, 2012
 */
package com.strixa.gl.util;

import com.strixa.util.Point3D;

/**
 * Generic vertex class which allows for any type of numeric to be used.
 *
 * @author Nicholas Rogé
 */
public class Vertex extends Point3D<Double>{
    private double __weight;
    
    
    /*Begin Constructors*/
    /**
     * Constructs a vertex whose coordinates are at the given vertices and has the given weight.
     * 
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z coordinate.
     * @param weight Weight parameter.
     */
    public Vertex(double x,double y,double z,double weight){
        super(x,y,z);
        this.__weight = weight;
    }
    
    /**
     * Constructs a copy of the given Vertex.
     * 
     * @param copy Vertex whose data should be copied.
     */
    public Vertex(Vertex copy){
        super(copy.getX(),copy.getY(),copy.getZ());
        this.__weight = copy.getWeight();
    }
    /*End Constructors*/
    
    /*Begin Getter/Setter Methods*/    
    /**
     * Gets this vertex's weight.
     * 
     * @return This vertex's weight.
     */
    public double getWeight(){
        return this.__weight;
    }
    
    /**
     * Sets this vertex's coordinates and weight.
     * 
     * @param x X coordinate of the vertex.
     * @param y Y coordinate of the vertex.
     * @param z Z coordinate of the vertex.
     * @param weight Weight parameter.
     */
    public void setData(double x,double y,double z,double weight){
        this.setX(x);
        this.setY(y);
        this.setZ(z);
        this.setWeight(weight);
    }
    
    /**
     * Sets this weight.
     * 
     * @param weight This vertex's weight.
     */
    public void setWeight(double weight){
        this.__weight = weight;
    }
    /*End Getter/Setter Methods*/
    
    /*Begin Other Methods*/
    /**
     * Performs a check to see if the given vertex is at the same location as this one.
     * 
     * @param vertex vertex to compare against.
     * 
     * @return Returns true if the given vertex is at the same location as this one, and false, otherwise.
     */
    public boolean equals(Vertex vertex){
        if(
            super.equals(new Point3D<Double>(vertex.getX(),vertex.getY(),vertex.getZ()))
            &&
            this.getWeight() == vertex.getWeight()
        ){
            return true;
        }
        
        return false;
    }
    
    public String toString(){
        return "Vertex at location ("+this.getX()+","+this.getY()+","+this.getZ()+") with weight:  " + this.getWeight();
    }
    /*End Other Methods*/
}

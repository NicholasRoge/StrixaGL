/**
 * File:  Rectangle.java
 * Date of Creation:  Jul 17, 2012
 */
package com.strixa.gl.shapes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.strixa.gl.StrixaPoint;
import com.strixa.gl.StrixaPolygon;
import com.strixa.util.Dimension2D;
import com.strixa.util.Point2D;

/**
 * A rectangle whose handle is in the top left corner.
 *
 * @author Nicholas Rogé
 */
public class Rectangle extends StrixaPolygon{
    private Dimension2D<Double> __dimensions;
    
    
    /*Begin Constructors*/
    /**
     * Constructs the rectangle with the given dimensions. 
     * 
     * @param rectangle_width Width of the rectangle.
     * @param rectangle_height Height of the rectangle.
     */
    public Rectangle(double rectangle_width,double rectangle_height){        
        this.setDimensions(rectangle_width,rectangle_height);
        
        this._updateProfile();
        
    }
    /*End Constructors*/
    
    /*Begin Getter/Setter Methods*/
    public Dimension2D<Double> getDimensions(){
        if(this.__dimensions == null){
            this.__dimensions = new Dimension2D<Double>(0.0,0.0);  //We just made a new dimension...?  (*  ).(  *)  *derp*
        }
        
        return this.__dimensions;
    }
    
    /**
     * Sets the dimensions of the rectangle.
     * 
     * @param width Width of the rectangle.
     * @param height Height of the rectangle.
     */
    public void setDimensions(double width,double height){
        this.getDimensions().setDimensions(width,height);
        
        this._updateProfile();
    }
    
    /**
     * Sets the height of the rectangle.
     * 
     * @param height Height of the rectangle.
     */
    public void setHeight(double height){
        this.getDimensions().setDimensions(this.getDimensions().getWidth(),height);
    }
    
    /**
     * Sets the width of the rectangle.
     * 
     * @param width Width of the rectangle.
     */
    public void setWidth(double width){
        this.getDimensions().setDimensions(width,this.getDimensions().getHeight());
    }
    /*End Getter/Setter Methods*/
    
    /*Begin Other Methods*/
    protected void _updateProfile(){        
        final double                half_height = this.getDimensions().getHeight()/2;
        final double                half_width = this.getDimensions().getWidth()/2;
        final List<StrixaPoint> points = new ArrayList<StrixaPoint>();
        
        
        /*Update Points*/
        points.add(new StrixaPoint(-half_width,half_height,0,Color.WHITE,(byte)255));
        points.add(new StrixaPoint(half_width,half_height,0,Color.WHITE,(byte)255));
        points.add(new StrixaPoint(half_width,-half_height,0,Color.WHITE,(byte)255));
        points.add(new StrixaPoint(-half_width,-half_height,0,Color.WHITE,(byte)255));
        
        this._setPoints(points);
    }
    /*End Other Methods*/
}

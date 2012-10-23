/**
 * File:  Strixa2DElement.java
 * Date of Creation:  Jul 17, 2012
 */
package com.strixa.gl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLContext;

import com.strixa.gl.StrixaPolygon.StrixaPolygonUpdateListener;
import com.strixa.gl.properties.Cuboid;
import com.strixa.util.Dimension3D;
import com.strixa.util.Point2D;
import com.strixa.util.Point3D;


/**
 * Creates an object to be displayed on a 2D plane.
 *
 * @author Nicholas Rogé
 */
public class Strixa3DElement extends StrixaGLElement implements StrixaPolygonUpdateListener{    
    private final List<StrixaPolygon>   __components = new ArrayList<StrixaPolygon>();
    private final Point3D<Double>       __coordinates = new Point3D<Double>(0.0,0.0,0.0);
    
    private Cuboid         __bounding_box;
    private Integer        __list_index;
    private StrixaMaterial __material;
    
    
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
    
    /**
     * Gets the list of components currently added to this element.
     * 
     * @return The list of components currently added to this element.
     */
    public List<StrixaPolygon> getComponents(){
        return this.__components;
    }
    
    public Point3D<Double> getCoordinates(){        
        return this.__coordinates;
    }
    
    public Dimension3D<Double> getDimensions(){
        return this.__bounding_box.getDimensions();
    }
    
    /**
     * Gets the material currently being used while drawing this object.
     * 
     * @return Returns the material currently being used.
     */
    public StrixaMaterial getMaterial(){
        return this.__material;
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
    
    /**
     * Sets the material this element should be using while being drawn.
     * 
     * @param material Material to be used.
     */
    public void setMaterial(StrixaMaterial material){
        this.__material = material;
    }
    /*End Getter/Setter Methods*/
    
    /*Begin Other Methods*/
    /**
     * Adds a polygon to this element.  If the polygon already exists within this element, it will not be added again.<br />
     * <strong>Note:</strong>  The {@link Strixa3DElement#addComponents(List)} method is the preferred method to use when adding multiple components to this element. 
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
        if(this.__list_index == null){
            //this.__list_index = gl.glGenLists(1);
            //gl.glNewList(this.__list_index,GL2.GL_COMPILE);
            
            this._drawComponents(this.getComponents());
            
            //gl.glEndList();
        }
        
        
       // gl.glCallList(this.__list_index);
    }
    
    /**
     * Draws the requested component.
     * 
     * @param component Component to be drawn.
     */
    protected void _drawComponent(StrixaPolygon component){
        final List<StrixaPoint>     coordinate_points = component.getPoints();
        final GL2                   gl = GLContext.getCurrentGL().getGL2();
        final List<Point3D<Double>> normal_points = component.getNormalPoints();
        final List<Point2D<Double>> texture_points = component.getTexturePoints();
        
        
        gl.glPushMatrix();
        gl.glTranslated(component.getCoordinates().getX(),component.getCoordinates().getY(),component.getCoordinates().getZ());
        
        switch(component.getPoints().size()){
            case 0:
            case 1:
            case 2:
                throw new RuntimeException("You must add at least 3 points to a polygon in order for it to be drawn.");
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
        
        for(int point_index = 0,point_end_index = component.getPoints().size();point_index < point_end_index;point_index++){            
            if(!texture_points.isEmpty()){
                gl.glTexCoord2d(
                    texture_points.get(point_index).getX(),
                    texture_points.get(point_index).getY()
                );
            }
            if(!normal_points.isEmpty()){
                gl.glNormal3d(
                    normal_points.get(point_index).getX(),
                    normal_points.get(point_index).getY(),
                    normal_points.get(point_index).getZ()
                );
            }
            gl.glVertex3d(
                coordinate_points.get(point_index).getCoordinates().getX(),
                coordinate_points.get(point_index).getCoordinates().getY(),
                coordinate_points.get(point_index).getCoordinates().getZ()
            );
        }
            
        gl.glEnd();
        gl.glPopMatrix();
    }
    
    /**
     * Draws the requested components.
     * 
     * @param components Components to be drawn.
     */
    protected void _drawComponents(List<StrixaPolygon> components){
        final GL2             gl = GLContext.getCurrentGL().getGL2();
        final Point3D<Double> this_coordinates = this.getCoordinates();
        
        
        gl.glPushMatrix();
        gl.glTranslated(this_coordinates.getX(),this_coordinates.getY(),this_coordinates.getZ());     
        
        if(this.__material.hasTexture()){
            if(!this.__material.isTextureLoaded()){
                try{
                    this.__material.loadTexture();
                }catch(IOException e){
                    System.out.println("Error:  Could not load requested texture.");
                }
            }

            if(this.__material.isTextureLoaded()){  //We're adding a second if here to make sure that if the material for some reason couldn't be loaded, we don't try to bind to it still.
                this.__material.getTexture().bind(gl);
                this.__material.getTexture().enable(gl);
                
                gl.glTexEnvf(GL2.GL_TEXTURE_ENV,GL2.GL_TEXTURE_ENV_MODE,GL2.GL_MODULATE);
                gl.glTexParameterf(GL2.GL_TEXTURE_2D,GL2.GL_TEXTURE_WRAP_S,GL2.GL_REPEAT);
                gl.glTexParameterf(GL2.GL_TEXTURE_2D,GL2.GL_TEXTURE_WRAP_T,GL2.GL_REPEAT);
            }
        }
        
        if(this.__material.getAmbientColor() != null){
            gl.glMaterialfv(GL2.GL_FRONT,GL2.GL_AMBIENT,this.__material.getAmbientColor(),0);
        }
        if(this.__material.getDiffuseColor() != null){
            gl.glMaterialfv(GL2.GL_FRONT,GL2.GL_DIFFUSE,this.__material.getDiffuseColor(),0);
        }
        if(this.__material.getSpecularColor() != null){
            gl.glMaterialfv(GL2.GL_FRONT,GL2.GL_DIFFUSE,this.__material.getSpecularColor(),0);
        }        
        
        for(int component_index = 0,component_end_index = components.size();component_index < component_end_index;component_index++){
            this._drawComponent(components.get(component_index));
        }
        
        if(this.__material.isTextureLoaded()){
            this.__material.getTexture().disable(gl);
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
        List<StrixaPolygon> element_components = element.getComponents();
        List<StrixaPolygon> this_components = this.getComponents(); 
        
        
        for(int this_index = 0,this_end_index = this_components.size() - 1;this_index <= this_end_index;this_index++){
            for(int element_index = 0,element_end_index = element_components.size();element_index <= element_end_index;element_index++){
                if(this.__components.get(this_index).isColliding(element_components.get(element_index))){
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Simple check to determine whether this element is visible in the current context.
     * 
     * @param context The context in which the StrixaGL application is currently being run.
     * 
     * @return Returns true if this element is visible and should be drawn, and false, otherwise.
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
    
    public void onStrixaPolygonUpdate(StrixaPolygon polygon){
        this.__list_index = null;
        
        this._regenerateBoundingBox();
    }
    
    /**
     * Regenerates the element's bounding box.
     */
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
}

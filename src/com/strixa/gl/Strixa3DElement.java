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
import javax.media.opengl.GLException;

import com.strixa.gl.properties.Cuboid;
import com.strixa.gl.util.Vertex;
import com.strixa.util.Dimension3D;
import com.strixa.util.Log;
import com.strixa.util.Point3D;


/**
 * Creates an object to be displayed on a 3D plane.
 *
 * @author Nicholas Rogé
 */
public class Strixa3DElement extends StrixaGLElement{ 
    public enum CollisionDetectionMethod{
        BOUNDING_BOX
    }
    
    private final List<StrixaPolygon>   __components = new ArrayList<StrixaPolygon>();
    
    private Cuboid         __bounding_box;
    private Integer        __list_index;
    private StrixaMaterial __material;
    
    
    /*Begin Constructor*/
    /**
     * Constructs a basic Strixa3DElement.
     */
    public Strixa3DElement(){
        this.__material = new StrixaMaterial();
        this.__bounding_box = new Cuboid(
            new Point3D<Double>(0.0,0.0,0.0),
            0,0,0
        );
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
        return this.getBoundingBox().getCoordinates();
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
        
        this.invalidate();
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
            polygon.setParent(this);
            this.__components.add(polygon);
        }
        
        this.invalidate();
    }
    
    /**
     * Adds the polygons in the given list to this element.
     * 
     * @param polygon_list Polygons to be added.
     */
    public void addComponents(List<StrixaPolygon> polygons){
        for(int index = 0,end_index = polygons.size() - 1;index <= end_index;index++){
            if(!this.__components.contains(polygons)){
                polygons.get(index).setParent(this);
                this.__components.add(polygons.get(index));
            }
        }
        
        this.invalidate();
    }
    
    public void draw(GL2 gl){        
        if(this.__material.hasTexture()){
            if(!this.__material.isTextureLoaded()){
                try{
                    this.__material.loadTexture();
                }catch(IOException e){
                    Log.logEvent(Log.Type.WARNING,"Texture could not be loaded, and will not be displayed.");
                }
            }
        }
        
        if(this.__list_index == null){
            this.__list_index = gl.glGenLists(1);
            gl.glNewList(this.__list_index,GL2.GL_COMPILE);
            
            this._drawComponents(gl,this.getComponents(),this.__material);
            
            gl.glEndList();
        }
        
        
        gl.glCallList(this.__list_index);
    }
    
    /**
     * Draws the requested component.
     * 
     * @param component Component to be drawn.
     */
    protected void _drawComponent(GL2 gl,StrixaPolygon component){
        /*Begin Parameter Verification*/
        if(gl == null){
            try{
                gl = GLContext.getCurrentGL().getGL2();
            }catch(GLException e){
                Log.logEvent(Log.Type.WARNING,"Attempt was made to draw a polygon with no GLContext available.");
                
                return;
            }
        }
        
        if(component == null){
            Log.logEvent(Log.Type.WARNING,"Attempt was made to draw empty polygon.");
            
            return;
        }
        /*End Parameter Verification*/
        
        
        final List<Vertex> coordinate_points = component.getPoints();
        final List<Vertex> normal_points = component.getNormalPoints();
        final List<Vertex> texture_points = component.getTexturePoints();
        
        
        gl.glPushMatrix();
        gl.glTranslated(component.getCoordinates().getX(),component.getCoordinates().getY(),component.getCoordinates().getZ());
        
        switch(coordinate_points.size()){
            case 0:
            case 1:
            case 2:
                Log.logEvent(Log.Type.WARNING,"You must add at least 3 points to a polygon in order for it to be drawn.");
                return;
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
                gl.glTexCoord3d(
                    texture_points.get(point_index).getX(),     //U
                    texture_points.get(point_index).getY(),     //V
                    texture_points.get(point_index).getWeight() //W
                );
            }
            if(!normal_points.isEmpty()){
                gl.glNormal3d(
                    normal_points.get(point_index).getX(),
                    normal_points.get(point_index).getY(),
                    normal_points.get(point_index).getZ()
                );
            }
            gl.glVertex4d(
                coordinate_points.get(point_index).getX(),
                coordinate_points.get(point_index).getY(),
                coordinate_points.get(point_index).getZ(),
                coordinate_points.get(point_index).getWeight()
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
    protected void _drawComponents(GL2 gl,List<StrixaPolygon> components,StrixaMaterial material){
        final Point3D<Double> this_coordinates = this.getCoordinates();
        
        
        gl.glPushMatrix();
        gl.glTranslated(this_coordinates.getX(),this_coordinates.getY(),this_coordinates.getZ());     
        
        if(material.isTextureLoaded()){  //We're adding a second if here to make sure that if the material for some reason couldn't be loaded, we don't try to bind to it still.
            material.getTexture().bind(gl);
            material.getTexture().enable(gl);
            
            gl.glTexEnvf(GL2.GL_TEXTURE_ENV,GL2.GL_TEXTURE_ENV_MODE,GL2.GL_MODULATE);
            gl.glTexParameterf(GL2.GL_TEXTURE_2D,GL2.GL_TEXTURE_WRAP_S,GL2.GL_REPEAT);
            gl.glTexParameterf(GL2.GL_TEXTURE_2D,GL2.GL_TEXTURE_WRAP_T,GL2.GL_REPEAT);
        }
        
        if(gl.glIsEnabled(GL2.GL_LIGHTING)){
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK,GL2.GL_AMBIENT,material.getAmbientColor(),0);
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK,GL2.GL_DIFFUSE,material.getDiffuseColor(),0);
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK,GL2.GL_EMISSION,material.getEmissionColor(),0);
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK,GL2.GL_SPECULAR,material.getSpecularColor(),0);
            gl.glMaterialfv(GL2.GL_FRONT_AND_BACK,GL2.GL_SHININESS,new float[]{material.getSpecularCoefficient()},0);
        }else{
            gl.glColor3fv(material.getDiffuseColor(),0);  //This is just a temporary set up for right now.
        }
        
        for(int component_index = 0,component_end_index = components.size();component_index < component_end_index;component_index++){
            this._drawComponent(gl,components.get(component_index));
        }
        
        if(material.isTextureLoaded()){
            material.getTexture().disable(gl);
        }
        
        gl.glPopMatrix();
    }
    
    /**
     * Indicates that something about this element has changed, and that it should be recreated.
     */
    public void invalidate(){
        this.__list_index = null;
        this._regenerateBoundingBox();
    }
    
    /**
     * Method to check for collision with another object.
     * 
     * @param element Element who you're trying to detect if this object is colliding with.
     * 
     * @return Returns true if this object is colliding with the given object, and false, otherwise. 
     */
    public boolean isColliding(Strixa3DElement element){        
        return this.isColliding(element,CollisionDetectionMethod.BOUNDING_BOX);
    }
    
    /**
     * Method to check for collision with another object.
     * 
     * @param element Element who you're trying to detect if this object is colliding with.
     * @param method Method of collision detection to use.
     * 
     * @return Returns true if this object is colliding with the given object, and false, otherwise. 
     */
    public boolean isColliding(Strixa3DElement element,CollisionDetectionMethod method){        
        List<StrixaPolygon> element_components = element.getComponents();
        List<StrixaPolygon> this_components = this.getComponents(); 
        
        
        switch(method){
            case BOUNDING_BOX:
                double e1_min_x = this.getBoundingBox().getCoordinates().getX();
                double e1_min_y = this.getBoundingBox().getCoordinates().getY();
                double e1_min_z = this.getBoundingBox().getCoordinates().getZ();
                double e1_max_x = this.getBoundingBox().getCoordinates().getX() + this.getBoundingBox().getWidth();
                double e1_max_y = this.getBoundingBox().getCoordinates().getY() + this.getBoundingBox().getHeight();
                double e1_max_z = this.getBoundingBox().getCoordinates().getZ() + this.getBoundingBox().getDepth();
                double e2_min_x = element.getBoundingBox().getCoordinates().getX();
                double e2_min_y = element.getBoundingBox().getCoordinates().getY();
                double e2_min_z = element.getBoundingBox().getCoordinates().getZ();
                double e2_max_x = element.getBoundingBox().getCoordinates().getX() + element.getBoundingBox().getWidth();
                double e2_max_y = element.getBoundingBox().getCoordinates().getY() + element.getBoundingBox().getHeight();
                double e2_max_z = element.getBoundingBox().getCoordinates().getZ() + element.getBoundingBox().getDepth();
                
                
                if(
                    (
                        (e1_max_x >= e2_min_x && e1_max_x <= e2_max_x)
                        ||
                        (e2_max_x >= e1_min_x && e2_max_x <= e1_max_x)
                    )
                    &&
                    (
                        (e1_max_y >= e2_min_y && e1_max_y <= e2_max_y)
                        ||
                        (e2_max_y >= e1_min_y && e2_max_y <= e1_max_y)
                    )
                    &&
                    (
                        (e1_max_z >= e2_min_z && e1_max_z <= e2_max_z)
                        ||
                        (e2_max_z >= e1_min_z && e2_max_z <= e1_max_z)
                    )
                ){
                    return true;
                }else{
                    return false;
                }
            default:
                for(int this_index = 0,this_end_index = this_components.size() - 1;this_index <= this_end_index;this_index++){
                    for(int element_index = 0,element_end_index = element_components.size();element_index <= element_end_index;element_index++){
                        if(this.__components.get(this_index).isColliding(element_components.get(element_index))){
                            return true;
                        }
                    }
                }
                
                return false;
        }
    }
    
    /**
     * Simple check to determine whether this element is visible in the current context.
     * 
     * @param context The context in which the StrixaGL application is currently being run.
     * 
     * @return Returns true if this element is visible and should be drawn, and false, otherwise.
     */
    public boolean isVisible(StrixaGLContext context){
        if(!super.isVisible(context)){
            return false;
        }
        
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
        final List<StrixaPolygon> polygons = this.getComponents();
        final int                 polygon_count = polygons.size();
        final Point3D<Double>     this_coordinates = this.getCoordinates();
        
        double            depth = 0.0;
        double            height = 0.0;
        List<Vertex>      points = null;
        double            width = 0.0;
        
        
        if(!this.getComponents().isEmpty()){
            width = this_coordinates.getX();
            height = this_coordinates.getY();
            depth = this_coordinates.getZ();
            
            for(int polygon_index = 0;polygon_index < polygon_count;polygon_index++){
                points = polygons.get(polygon_index).getPoints();
                
                for(int index = 0,end_index = points.size() - 1;index < end_index;index++){
                    width = Math.max(width,points.get(index).getX());
                    height = Math.max(height,points.get(index).getY());
                    depth = Math.max(depth,points.get(index).getZ());
                }
            }
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

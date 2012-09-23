/**
 * File:  Strixa2DCanvas.java
 * Date of Creation:  Jul 19, 2012
 */
package com.strixa.gl;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.glu.GLU;

import com.strixa.gl.properties.Cuboid;
import com.strixa.util.Dimension2D;
import com.strixa.util.Point2D;
import com.strixa.util.Point3D;

/**
 * TODO:  Write Class Description
 *
 * @author Nicholas Rogé
 */
public abstract class Strixa3DCanvas extends StrixaGLCanvas implements MouseMotionListener{
    /** Field needed for the serialization of this object. */
    private static final long serialVersionUID = 7940290686156245285L;
    
    private final Point3D<Double> __camera_location = new Point3D<Double>(0.0,0.0,0.0);
    
    private double                __camera_pitch;
    private double                __camera_rotation;
    private double                __camera_tilt;
    private List<Strixa3DElement> __children;
    private double                __render_distance;
 
    
    /*Begin Constructors*/
    /**
     * Constructs the object with the given capabilities.
     * 
     * @param capabilities Capabilities this canvas should have.
     * @param aspect_ratio Ratio of the width of this canvas, to it's height. (width/height)
     */
    public Strixa3DCanvas(GLCapabilities capabilities,double aspect_ratio){
        super(capabilities,aspect_ratio);
        
        
      //this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.setCamera(0,0,0);
        
        this._refreshViewableArea();
    }
    /*End Constructors*/
    
    /*Begin Overridden Methods*/    
    @Override public void reshape(GLAutoDrawable drawable,int x,int y,int width,int height){
        super.reshape(x,y,width,height);
        
        this.setAspectRatio((double)width/(double)height);
        this._refreshViewableArea();
    }
    /*End Overridden Methods*/
    
    /*Begin Getter/Setter Methods*/    
    public double getCameraPitch(){
        return this.__camera_pitch;
    }
    
    public double getCameraRotation(){
        return this.__camera_rotation;
    }
    
    public double getCameraTilt(){
        return this.__camera_tilt;
    }
    
    public double getRenderDistance(){
        return this.__render_distance;
    }
    
    /**
     * Gets this object's Strixa3DElement children.
     * 
     * @return This object's Strixa3DElement children.
     */
    public List<Strixa3DElement> getChildren(){
        if(this.__children==null){
            this.__children = new ArrayList<Strixa3DElement>();
        }
        
        return this.__children;
    }
    
    public void setCamera(double pitch,double rotation,double tilt){
        this.setCameraPitch(pitch);
        this.setCameraRotation(rotation);
        this.setCameraTilt(tilt);
    }
    
    public void setCameraPitch(double pitch){
        this.__camera_pitch = pitch;
    }
    
    public void setCameraRotation(double rotation){
        this.__camera_rotation = rotation;
    }
    
    public void setCameraTilt(double tilt){
        this.__camera_tilt = tilt;
    }
    
    /**
     * Sets the number of units to allow rendering in any given direction.
     * 
     * @param num_units The number of units to allow rendering in any given direction.
     */
    public void setRenderDistance(double num_units){
        this.__render_distance = num_units;
        
        this._refreshViewableArea();
    }
    /*End Getter/Setter Methods*/
    
    /*Begin Other Essential Methods*/
    /**
     * Adds a child to this canvas.<br />
     * <strong>Note:</strong>  A child may not be added to the canvas more than once.
     * 
     * @param child Child to be added to the canvas.
     */
    public void addChild(Strixa3DElement child){
        final List<Strixa3DElement> children = this.getChildren();
        
        
        if(!children.contains(child)){
            children.add(child);
        }
    }
    
    protected void _drawChildren(GL2 gl){
        if(this.getChildren().size()==0){
            return;
        }
        
        final List<Strixa3DElement> children = this.getChildren();
        final GLU                   glu = new GLU();        
        
        
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        
        glu.gluPerspective(
            90,
            this.getAspectRatio(),
            this.__render_distance,
            this.__render_distance
        );
        glu.gluLookAt(
            //Camera Location
            this.__camera_location.getX(),
            this.__camera_location.getY(),
            this.__camera_location.getZ(),
            //Looking at what?
            this.__camera_location.getX()+(Math.cos((this.getCameraRotation()*Math.PI)/180)*Math.sin((this.getCameraPitch()*Math.PI)/360)),
            this.__camera_location.getY()+(Math.sin((this.getCameraRotation()*Math.PI)/180)*Math.cos((this.getCameraPitch()*Math.PI)/360)),
            this.__camera_location.getX()+Math.cos((this.getCameraPitch()*Math.PI)/360),
            //Where is up?
            0,
            1,
            0
        );
        
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        
        /*Draw the models!*/
        synchronized(children){
            for(Strixa3DElement child:children){
                if(child.isVisible(this.getStrixaGLContext())){
                    gl.glPushMatrix();                    
                        child.draw(gl);
                    gl.glPopMatrix();
                }
            }
        }
        
        this.swapBuffers();
    }
    
    public void mouseDragged(MouseEvent event){
        System.out.println("Mouse Dragged");
    }
    
    public void mouseMoved(MouseEvent event){
        System.out.println("Mouse Moved");
    }
    
    protected void _refreshViewableArea(){        
        final double half_render_distance = this.__render_distance/2;
        
        
        this.getStrixaGLContext().setViewableArea(new Cuboid(
            new Point3D<Double>(
                this.__camera_location.getX() - half_render_distance,
                this.__camera_location.getY() - half_render_distance,
                this.__camera_location.getZ() - half_render_distance
            ),
            half_render_distance,
            half_render_distance,
            half_render_distance
        ));
    }
    
    /**
     * Removes a child from this canvas.
     * 
     * @param child Child to be removed from the canvas.
     */
    public void removeChild(StrixaGLElement child){
        final List<Strixa3DElement> children = this.getChildren();
        
        
        if(children.contains(child)){
            children.remove(child);
        }
    }
    
    /**
     * Moves the viewing area to the 
     * 
     * @param x_modification The number of units x which the viewing area should be shifted left or right.
     * @param y_modification The number of units y which the viewing area should be shifted up or down.
     */
    public void shiftViewingArea(double x_modification,double y_modification,double z_modification){
        this.__camera_location.setX(this.__camera_location.getX() - x_modification);
        this.__camera_location.setY(this.__camera_location.getY() - y_modification);
        this.__camera_location.setY(this.__camera_location.getZ() - z_modification);
        
        this._refreshViewableArea();
    }
    /*Begin Other Essential Methods*/
}

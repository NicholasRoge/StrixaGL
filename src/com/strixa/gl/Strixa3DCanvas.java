/**
 * File:  Strixa2DCanvas.java
 * Date of Creation:  Jul 19, 2012
 */
package com.strixa.gl;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.glu.GLU;

import com.strixa.gl.properties.Cuboid;
import com.strixa.util.Point3D;

/**
 * TODO:  Write Class Description
 *
 * @author Nicholas Rog�
 */
public abstract class Strixa3DCanvas extends StrixaGLCanvas implements MouseMotionListener,MouseListener{
    /** Field needed for the serialization of this object. */
    private static final long serialVersionUID = 7940290686156245285L;
    
    private final Point3D<Double> __camera_location = new Point3D<Double>(0.0,0.0,0.0);
    private final Point3D<Double> __camera_looking_at_point = new Point3D<Double>(0.0,0.0,1.0);
    
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
        
        
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.setCamera(0,0,0);
        this.setRenderDistance(100);
        
        this._refreshViewableArea();
        this._refreshCamera();
    }
    /*End Constructors*/
    
    /*Begin Overridden Methods*/ 
    @Override public void init(GLAutoDrawable drawable){
        super.init(drawable);
        
        drawable.getGL().getGL2().glEnable(GL2.GL_DEPTH_TEST);
        drawable.getGL().getGL2().glDepthFunc(GL2.GL_LESS);
        
        drawable.getGL().getGL2().glShadeModel(GL2.GL_SMOOTH);
        drawable.getGL().getGL2().glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT,GL2.GL_NICEST);
        drawable.getGL().getGL2().glClearColor(0f,0f,0f,1f);
    }
    
    @Override public void reshape(GLAutoDrawable drawable,int x,int y,int width,int height){
        super.reshape(x,y,width,height);
        
        this.setAspectRatio((double)width/(double)height);
        this._refreshViewableArea();
        this._refreshCamera();
    }
    /*End Overridden Methods*/
    
    /*Begin Getter/Setter Methods*/
    /**
     * Gets the camera's pitch value.
     * 
     * @return The camera's pitch value.  This will always be a positive number between 0 and 360.
     */
    public double getCameraPitch(){
        return this.__camera_pitch;
    }
    
    /**
     * Gets the camera's rotation value.
     * 
     * @return The camera's rotation value.  This will always be a positive number between 0 and 360.
     */
    public double getCameraRotation(){
        return this.__camera_rotation;
    }
    
    /**
     * Gets the camera's tilt value.
     * 
     * @return The camera's tilt value.  This will always be a positive number between 0 and 360.
     */
    public double getCameraTilt(){
        return this.__camera_tilt;
    }
    
    /**
     * Gets the number of units in the z direction that the canvas will render elements.
     * 
     * @return The number of units in the z direction that the canvas will render elements.
     */
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
    
    /**
     * Sets various aspects of the camera.
     * 
     * @param pitch Amount of pitch the camera should take on.
     * @param rotation Amount the camera should be rotated.
     * @param tilt Amount the camera should be tilted.
     */
    public void setCamera(double pitch,double rotation,double tilt){
        this.__camera_pitch = pitch;
        this.__camera_rotation = rotation;
        this.__camera_tilt = tilt;
        
        this._refreshCamera();
    }
    
    
    /**
     * Rotates the camera around the Y axis.
     * 
     * @param pitch  Amount of pitch the camera should take on.<br />
     * <strong>Note:</strong>  If this value is less than 0, its value will become (360 + (360 % pitch)), and if the pitch value exceeds 360, it will become (360 % pitch).
     */
    public void setCameraPitch(double pitch){
        pitch = pitch % 360;  //This ensures the value I get is between 0 and 360
        if(pitch < 0){
            pitch = 360 + pitch;
        }
        
        this.setCamera(pitch,this.getCameraRotation(),this.getCameraTilt());
    }
    
    /**
     * Rotates the camera around the Y axis.
     * 
     * @param rotation Amount the camera should be rotated.<br />
     * <strong>Note:</strong>  If this value is less than 0, its value will become (360 + (360 % rotation)), and if the rotation value exceeds 360, it will become (360 % rotation).
     */
    public void setCameraRotation(double rotation){
        rotation = rotation % 360;  //This ensures the value I get is between 0 and 360.
        if(rotation < 0){
            rotation = 360 + rotation;  //And this ensures the value is positive.
        }
        
        
        this.setCamera(this.getCameraPitch(),rotation,this.getCameraTilt());
    }
    
    /**
     * Tilts the camera, rotating it around the around the Z axis.  (Relative to where you're looking) 
     * 
     * @param tilt Amount the camera should be tilted.<br />
     * <strong>Note:</strong>  If this value is less than 0, its value will become (360 + (360 % tilt)), and if the tilt value exceeds 360, it will become (360 % tilt).
     */
    public void setCameraTilt(double tilt){
        tilt = tilt % 360;  //This ensures the value I get is between 0 and 360.
        if(tilt < 0){
            tilt = 360 + tilt;  //And this ensures the value is positive.
        }
        
        this.setCamera(this.getCameraPitch(),this.getCameraRotation(),tilt);
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
    
    public void advanceCamera(double units){
        if(units==0){
            return;
        }
        
        this.shiftViewingArea(
            units * Math.sin((this.getCameraRotation() * Math.PI) / 180),
            0,
            units * Math.cos((this.getCameraRotation() * Math.PI) / 180)
        );
    }
    
    protected void _drawChildren(GL2 gl){
        final List<Strixa3DElement> children = this.getChildren();      
        final int                   child_count = children.size();
        final GLU                   glu = new GLU(); 
        
                
        if(child_count == 0){
            return;
        }        
        
        gl.glLightfv(GL2.GL_LIGHT0,GL2.GL_AMBIENT,new float[]{0f,0f,0f,1f},0);
        gl.glLightfv(GL2.GL_LIGHT0,GL2.GL_DIFFUSE,new float[]{1f,1f,1f,1f},0);
        gl.glLightfv(GL2.GL_LIGHT0,GL2.GL_SPECULAR,new float[]{1f,1f,1f,1f},0);
        gl.glLightfv(GL2.GL_LIGHT0,GL2.GL_POSITION,new float[]{2f,2f,-2f,0f},0);
        
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_LIGHTING);
        
        /*gl.glMaterialfv(GL2.GL_FRONT,GL2.GL_AMBIENT,new float[]{.3f,.5f,.1f,1f},0);
        gl.glMaterialfv(GL2.GL_FRONT,GL2.GL_SPECULAR,new float[]{.3f,.5f,.1f,1f},0);
        gl.glMaterialf(GL2.GL_FRONT,GL2.GL_SHININESS,.5f);*/
        
        
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        
        glu.gluPerspective(
            90,
            this.getAspectRatio(),
            this.getRenderDistance(),
            1
        );
        glu.gluLookAt(
            //Camera Location
            this.__camera_location.getX(),
            this.__camera_location.getY(),
            this.__camera_location.getZ(),
            //Looking at what?
            this.__camera_looking_at_point.getX(),
            this.__camera_looking_at_point.getY(),
            this.__camera_looking_at_point.getZ(),
            //Where is up?
            0,
            1,
            0
        );

        
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        
        
        
        /*Draw the models!*/
        synchronized(children){
            for(int index = 0;index<child_count;index++){
                if(children.get(index).isVisible(this.getStrixaGLContext())){
                    gl.glPushMatrix();                    
                        children.get(index).draw(gl);
                    gl.glPopMatrix();
                }
            }
        }
    }
    
    public void mouseClicked(MouseEvent event){}
    
    public void mouseDragged(MouseEvent event){}
    
    public void mouseEntered(MouseEvent event){}
    
    public void mouseExited(MouseEvent event){}
    
    public void mouseMoved(MouseEvent event){}
    
    public void mousePressed(MouseEvent event){}
    
    public void mouseReleased(MouseEvent event){}
    
    protected void _refreshViewableArea(){        
        final double half_render_distance = this.__render_distance/2;
        
        
        this.getStrixaGLContext().setViewableArea(new Cuboid(
            new Point3D<Double>(
                this.__camera_location.getX() - half_render_distance,
                this.__camera_location.getY() - half_render_distance,
                this.__camera_location.getZ() - half_render_distance
            ),
            this.__render_distance,
            this.__render_distance,
            this.__render_distance
        ));
    }
    
    protected void _refreshCamera(){
        //TODO_HIGH:  Get this method hooked up with some actual measurements...
        
        this.__camera_looking_at_point.setX(this.__camera_location.getX()+Math.sin((this.getCameraRotation()*Math.PI)/180));
        this.__camera_looking_at_point.setY(0.0);
        this.__camera_looking_at_point.setZ(this.__camera_location.getZ()+Math.cos((this.getCameraRotation()*Math.PI)/180));
    }
    
    /**
     * Removes a child from this canvas.
     * 
     * @param child Child to be removed from the canvas.
     */
    public void removeChild(Strixa3DElement child){
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
     * @param z_modification The number of units z which the viewing area should be shifted forward or backwards.
     */
    public void shiftViewingArea(double x_modification,double y_modification,double z_modification){
        this.__camera_location.setX(this.__camera_location.getX() + x_modification);
        this.__camera_location.setY(this.__camera_location.getY() + y_modification);
        this.__camera_location.setZ(this.__camera_location.getZ() + z_modification);
        
        this._refreshViewableArea();
        this._refreshCamera();
    }
    
    public void strafeCamera(double units){
        if(units==0){
            return;
        }
        
        this.shiftViewingArea(
            units * Math.sin(((this.getCameraRotation() + 90) * Math.PI) / 180),
            0,
            units * Math.cos(((this.getCameraRotation() + 90) * Math.PI) / 180)
        );
    }
    /*End Other Essential Methods*/
}

/**
 * File:  StrixaGLCanvas.java
 * Date of Creation:  Jul 16, 2012
 */
package com.strixa.gl;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.FPSAnimator;
import com.strixa.util.Dimension2D;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAnimatorControl;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawable;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;


/**
 * Creates an object which any Strixa elements should be drawn on.
 *
 * @author Nicholas Rogé
 */
public abstract class StrixaGLCanvas extends GLCanvas implements GLEventListener{
    /** Field needed for the serialization of this object. */
    private static final long serialVersionUID = -6426147154592668101L;
    
    private final FPSAnimator     __animator = new FPSAnimator(this,60);
    private final StrixaGLContext __context = new StrixaGLContext();
   
    private double  __aspect_ratio;
    private boolean __exiting;
    
    
    /*Begin Constructors*/
    /**
     * Constructs the objects with the given capabilities.
     * 
     * @param capabilities Capabilities GLCanvas should have.
     * @param aspect_ratio Ratio of the width of this canvas, to it's height. (width/height)
     */
    public StrixaGLCanvas(GLCapabilities capabilities,double aspect_ratio){
        super(capabilities);
        
        
        this.__exiting=false;
        this.setAspectRatio(aspect_ratio);        
        
        this.addGLEventListener(this);
    }
    /*End Constructors*/
    
    /*Begin Getter/Setter Methods*/
    public FPSAnimator getAnimator(){
        return this.__animator;
    }
    
    /**
     * Gets the aspect ratio of this canvas.
     * 
     * @return The aspect ratio of this canvas.
     */
    public double getAspectRatio(){
        return this.__aspect_ratio;
    }
    
    /**
     * Gets the maximum number of frames that may be displayed in a second.
     * 
     * @return The current FPS setting.
     */
    public int getFPS(){
        return this.__context.getCurrentFPS();
    }
    
    /**
     * Gets the canvas' current context.
     * 
     * @return The canvas' current context.
     */
    public StrixaGLContext getStrixaGLContext(){        
        return this.__context;
    }
    
    public void setAspectRatio(double aspect_ratio){
        this.__aspect_ratio = aspect_ratio;
    }
    /*End Getter/Setter Methods*/
    
    /*Begin Other Methods*/  
    public void display(GLAutoDrawable drawable){   
        this._performGameLogic(this.getStrixaGLContext());
        
        /*Clear everything up.*/
        drawable.getGL().glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        
        /*Draw everything that needs to be drawn.*/
        this._drawChildren(drawable.getGL().getGL2());
    }
    
    public void dispose(GLAutoDrawable drawable){
    }
    
    public void init(GLAutoDrawable drawable){
        final GL2 gl = (GL2)drawable.getGL();
        
        
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glClearColor(0f,0f,0f,1f);
        gl.setSwapInterval(1);
        
        
        /*Set up and start the game thread*/
        this.__animator.add(this);
        this.__animator.start();
    }
    
    public void reshape(GLAutoDrawable drawable,int x,int y,int width,int height){
        drawable.getGL().glViewport(x,y,width,height);
    }
    
    /**
     * This should be called when this canvas is to close and clean itself up.
     */
    public void triggerExiting(){
        this.__exiting = true;
        
        //TODO:  Call this this object's onExit listeners
    }
    /*End Other Methods*/
    
    /*Begin Abstract Methods*/
    /**
     * Draws this canvas' children.
     * 
     * @param gl GL2 canvas that the children should be drawn to.
     */
    protected abstract void _drawChildren(GL2 gl);
    
    /**
     * Define this method to implement your game or program's logic.
     * 
     * @param context This is the context in which the game or program is currently running.
     */
    protected abstract void _performGameLogic(StrixaGLContext context);
    /*End Abstract Methods*/
}

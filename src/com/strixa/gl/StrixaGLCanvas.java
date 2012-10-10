/**
 * File:  StrixaGLCanvas.java
 * Date of Creation:  Jul 16, 2012
 */
package com.strixa.gl;

import com.strixa.util.Dimension2D;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;


/**
 * Creates an object which any Strixa elements should be drawn on.
 *
 * @author Nicholas Rogé
 */
public abstract class StrixaGLCanvas extends GLCanvas implements GLEventListener,Runnable{
    /** Field needed for the serialization of this object. */
    private static final long serialVersionUID = -6426147154592668101L;
    
    private final StrixaGLContext __context = new StrixaGLContext();
    
    private double  __aspect_ratio;
    private boolean __exiting;
    private Thread  __gui_thread;
    
    
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
        this.__gui_thread = new Thread(this);
        this.setAspectRatio(aspect_ratio);
        this.setFPS(30);
        
        
        this.addGLEventListener(this);
    }
    /*End Constructors*/
    
    /*Begin Getter/Setter Methods*/
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
     * Returns the thread that the GUI is running in.
     * 
     * @return The thread that the GUI is running in.
     */
    public Thread getGUIThread(){        
        return this.__gui_thread;
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
    
    /**
     * Sets the maximum number of frames that are displayed in a second.
     * 
     * @param fps The frames to be displayed in a second.
     */
    public void setFPS(int fps){
        this.__context.setCurrentFPS(fps);
    }
    /*End Getter/Setter Methods*/
    
    /*Begin Other Methods*/  
    public void display(GLAutoDrawable drawable){        
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
        
        
        /*Set up and start the game thread*/
        this.getGUIThread().start();
    }
    
    public void reshape(GLAutoDrawable drawable,int x,int y,int width,int height){
        drawable.getGL().glViewport(x,y,width,height);
    }
    
    public void run(){
        long period = -1;
        long sleep_time = -1;
        long start_time = -1;
        long time_taken = -1;
        
       
        while(!this.__exiting){
            start_time = System.currentTimeMillis();
            period = 1000 / this.getFPS();
            
            this._performGameLogic(this.getStrixaGLContext());
            this.display();

            time_taken = System.currentTimeMillis() - start_time;
            sleep_time = period-time_taken;
            
            try{
                if(sleep_time>0){
                    Thread.sleep(sleep_time);  //If sleep time is less than or equal to 0, we may as well not even sleep.
                }
            }catch(InterruptedException e){
                System.out.println("Thread interrupted for some reason.");  //TODO:  Oh yeah...  That's a really helpful message you have there.  What do you THINK the todo is for?
            }
        }
        
        return;
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

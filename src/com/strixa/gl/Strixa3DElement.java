/**
 * File:  Strixa2DElement.java
 * Date of Creation:  Jul 17, 2012
 */
package com.strixa.gl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import com.strixa.gl.StrixaPolygon.StrixaPolygonUpdateListener;
import com.strixa.gl.properties.Cuboid;
import com.strixa.util.Dimension2D;
import com.strixa.util.Dimension3D;
import com.strixa.util.Point3D;


/**
 * Creates an object to be displayed on a 2D plane.
 *
 * @author Nicholas Rogé
 */
public class Strixa3DElement extends StrixaGLElement implements StrixaPolygonUpdateListener{
    /**
     * Runnable object which draws a certain number of polygons.  This object is meant to be used in a thread.
     *
     * @author Nicholas Rogé
     */
    public static class PolygonDrawer implements Runnable{
        private List<StrixaPolygon> __components;
        private boolean             __current_draw_finished;
        private GL2                 __pipeline;
        private int                 __num_polygons;
        private int                 __start_index;
        
        
        /*Begin Constructors*/
        /**
         * Creates a new runnable object which draws the given components.
         * 
         * @param pipeline Pipeline to which the components should be drawn.
         * @param components Components to be drawn.
         * @param start_index Index in the components list to start drawing from.
         */
        public PolygonDrawer(List<StrixaPolygon> components,int start_index){
            if(components == null){
                throw new NullPointerException("Argument 'components' must not be null.");
            }else if(start_index >= components.size()){
                throw new IllegalArgumentException("Argument 'start_index' must not be larger than the number of elements in the 'components' argument.");
            }
            
            
            this.__pipeline = null;
            this.__components = components;
            this.__start_index = start_index;
            this.__num_polygons = Math.min(Strixa3DElement.POLYGON_PER_THREAD,this.__components.size() - this.__start_index - 1);
            this.__current_draw_finished = false;
        }
        /*End Constructors*/
        
        /*Begin Other Essential Methods*/
        public boolean currentDrawFinished(){
            return this.__current_draw_finished;
        }
        
        public void run(){
            final int end_index = this.__start_index + this.__num_polygons;
            
            
            try{
                synchronized(this){
                    this.wait();  //This is needed so that a seperate clause isn't needed in the Strixa3DElement#draw method to start the this thread.
                }
            }catch(InterruptedException e){
                
            }
            while(true){
                this.__current_draw_finished = false;
                
                for(int index = this.__start_index;index <= end_index;index++){                
                    this.__components.get(index).draw(this.__pipeline);
                }
                
                this.__current_draw_finished = true;
                try{
                    synchronized(this){
                        this.wait();
                    }
                }catch(InterruptedException e){
                    
                }
            }
        }
        
        public void updatePipeline(GL2 pipeline){
            if(pipeline == null){
                throw new NullPointerException("Argument 'pipeline' must not be null.");
            }
            
            this.__pipeline = pipeline;
        }
        /*End Other Essential Methods*/
    }
    
    public final static int POLYGON_PER_THREAD = 100000;
    
    private final List<StrixaPolygon> __components = new ArrayList<StrixaPolygon>();
    private final Point3D<Double>     __coordinates = new Point3D<Double>(0.0,0.0,0.0);
    
    private Cuboid          __bounding_box;
    private PolygonDrawer[] __polygon_drawers;
    private Thread[]        __polygon_drawer_threads;
    
    
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
    
    public Dimension3D<Double> getDimensions(){
        return this.__bounding_box.getDimensions();
    }
    
    /**
     * Gets the list of components currently added to this element.
     * 
     * @return The list of components currently added to this element.
     */
    public List<StrixaPolygon> getComponents(){
        return this.__components;
    }
    
    /**
     * Gets this element's coordinates in the form of a Point object.
     * 
     * @return This element's coordinates in the form of a Point object.
     */
    public Point3D<Double> getCoordinates(){        
        return this.__coordinates;
    }
    
    /**
     * Sets this element's alpha.
     * 
     * @param alpha Alpha transparency this element should take on.  This should be a value between 0 and 1.
     */
    public void setAlpha(byte alpha){
        final List<StrixaPolygon> polygons = this.getComponents();
        final int                 polygon_count = polygons.size();
        
        
        for(int index = 0;index < polygon_count;index++){
            polygons.get(index).setAlpha(alpha);
        }
    }
    
    /**
     * Sets this element's colour.
     * 
     * @param colour Colour this element should be set to.
     */
    public void setColour(Color colour){
        final List<StrixaPolygon> polygons = this.getComponents();
        final int                 polygon_count = polygons.size();
        
        
        for(int index = 0;index < polygon_count;index++){
            polygons.get(index).setColour(colour);
        }
    }
    
    /**
     * Sets this element's colour.
     * 
     * @param red Red component of this element's colour.  This should be a value between 0 and 1.
     * @param green Green component of this element's colour.  This should be a value between 0 and 1.
     * @param blue Blue component of this element's colour.  This should be a value between 0 and 1.
     */
    public void setColour(float red,float green,float blue){
        final List<StrixaPolygon> polygons = this.getComponents();
        final int                 polygon_count = polygons.size();
        
        
        for(int index = 0;index < polygon_count;index++){
            polygons.get(index).setColour(red,green,blue);
        }
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
    /*End Getter/Setter Methods*/
    
    /*Begin Other Methods*/
    /**
     * Adds a polygon to this element.  If the polygon already exists within this element, it will not be added again.
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
        this._regenerateDrawerThreads();
    }
    
    public void draw(GL2 gl){
        long start_time = System.nanoTime();
        
        final Point3D<Double> this_coordinates = this.getCoordinates();
        
        
        gl.glPushMatrix();
        gl.glTranslated(this_coordinates.getX(),this_coordinates.getY(),this_coordinates.getZ());        
        for(int index = 0;index < this.__polygon_drawer_threads.length;index++){
            this.__polygon_drawers[index].updatePipeline(gl);
            synchronized(this.__polygon_drawers[index]){
                this.__polygon_drawers[index].notify();
            }
        }
        
        for(int index = 0;index < this.__polygon_drawer_threads.length;index++){
            while(!this.__polygon_drawers[index].currentDrawFinished()){
                try{
                    Thread.sleep(0,10);
                }catch(InterruptedException e){
                    
                }
            }
        }
        gl.glPopMatrix();
        
        System.out.println("Frame draw time:  " + (System.nanoTime() - start_time) + "ns");
    }
    
    /**
     * Method to check for collision with another object.
     * 
     * @param element Element who you're trying to detect if this object is colliding with.
     * 
     * @return Returns true if this object is colliding with the given object, and false, otherwise. 
     */
    public boolean isColliding(Strixa3DElement element){
        //TODO_HIGH:  Renivate this method
        for(StrixaPolygon our_polygon:this.getComponents()){
            for(StrixaPolygon their_polygon:element.getComponents()){
                if(our_polygon.isColliding(their_polygon)){
                    return true;
                }
            }
        }
        
        return false;
    }
    
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
        this._regenerateBoundingBox();
    }
    
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
    
    protected void _regenerateDrawerThreads(){
        final List<StrixaPolygon> polygons = this.getComponents();
        final int                 polygon_count = polygons.size();
        final int                 thread_count = (int)Math.ceil(((double)polygon_count) / ((double)Strixa3DElement.POLYGON_PER_THREAD));
        
        
        this.__polygon_drawers = new PolygonDrawer[thread_count];
        this.__polygon_drawer_threads = new Thread[thread_count];
        for(int index = 0;index < thread_count;index++){
            this.__polygon_drawers[index] = new PolygonDrawer(polygons,index * Strixa3DElement.POLYGON_PER_THREAD);
            this.__polygon_drawer_threads[index] = new Thread(this.__polygon_drawers[index],"polygon_draw_thread_"+index);
            
            this.__polygon_drawer_threads[index].start();
        }
    }
    /*End Other Methods*/
}

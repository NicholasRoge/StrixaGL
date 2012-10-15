/**
 * File:  BlendReader.java
 * Date of Creation:  Sep 24, 2012
 */
package com.strixa.gl.util;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import com.strixa.gl.Strixa3DElement;
import com.strixa.gl.StrixaPoint;
import com.strixa.gl.StrixaPolygon;


/**
 * Reads in a .blend file.
 *
 * @author Nicholas Rogé
 */
public class WavefrontReader implements Runnable{
    private final List<PercentLoadedUpdateListener> __percent_loaded_listeners = new ArrayList<PercentLoadedUpdateListener>();
    private final Thread                            __read_thread = new Thread(this,"WavefrontReader_read_thread");
    
    private String                __file_location;
    private boolean               __file_read;
    private List<Strixa3DElement> __objects;
    private double                __update_step;
    
    
    /*Begin Constructor*/
    public WavefrontReader(String file_location){
        this(file_location,1);
    }
    
    public WavefrontReader(String file_location,double update_step){
        if(file_location == null || file_location.equals("")){
            throw new IllegalArgumentException("Argument 'file_location' must not be null or empty.");
        }
        
        this.__file_location = file_location;
        this.__file_read = false;
        this.__update_step = update_step;
    }
    /*End Constructor*/
    
    /*Begin Other Essential Methods*/
    public void addPercentLoadedUpdateListener(PercentLoadedUpdateListener listener){
        if(!this.__percent_loaded_listeners.contains(listener)){
            this.__percent_loaded_listeners.add(listener);
        }
    }
    
    protected void _alertPercentLoadedUpdateListeners(double amount_loaded){
        for(int index = 0;index < this.__percent_loaded_listeners.size();index++){
            this.__percent_loaded_listeners.get(index).onPercentLoadedUpdate(amount_loaded);
        }
    }
    
    public void read(){
        this.__read_thread.start();
    }
    
    protected String _readLine(FileInputStream file) throws IOException{
        String line = null;
        int character = 0x00;
        
        
        if(file == null){
            throw new IllegalArgumentException("Argument 'file' must not be null.");
        }
        
        line = new String();
        do{
            character = file.read();
            
            line += (char)character;
        }while(character != '\n' && character != -1);
        
        if(line.charAt(line.length() - 1) == (char)-1){
            line = line.substring(0,line.length()-1);  //This kills the end character
            if(line.isEmpty()){
                line = null;
            }
        }else if(line.charAt(line.length() - 1) == '\n'){
            line = line.substring(0,line.length() - 1);  //This kills the newline
        }
        
        return line;
    }
    
    public void removePercentLoadedUpdateListener(PercentLoadedUpdateListener listener){
        if(this.__percent_loaded_listeners.contains(listener)){
            this.__percent_loaded_listeners.remove(listener);
        }else{
            throw new RuntimeException("This object does not have the given PercentLoadedUpdateListener registered to it.");
        }
    }
    
    public void run(){
        String[]            face_split;
        FileInputStream     file = null;
        double              last_update = 0;
        String              line = null;
        int                 line_number = 0;
        Strixa3DElement     object =  null;
        String              object_name = null;
        List<StrixaPoint>   object_points = null;
        List<StrixaPolygon> object_polygons = null;
        double              percent_loaded = 0;
        StrixaPolygon       polygon = null;
        double              total_bytes = 0;
        String[]            split = null;
        
        
        try{
            file = new FileInputStream(this.__file_location);
            total_bytes = file.available();
            
            object_points = new ArrayList<StrixaPoint>(1000);
            object_polygons = new ArrayList<StrixaPolygon>(1000);
            this.__objects = new ArrayList<Strixa3DElement>(100);
            while((line = this._readLine(file)) != null){
                line_number++;
                
                if(line.trim().equals("")){
                    continue;  //Skip empty lines
                }
                
                split = line.split(" ");
                if(split[0].equals("#")){
                    continue;  //This is just a comment.  We ignore those.
                }else if(split[0].equals("o")){
                    if(split.length < 2){
                        throw new RuntimeException("Object line incorrectly formatted!  Line number:  " + line_number);
                    }
                    
                    object_name = split[1];
                    if(object == null){ //as it would when this is teh first time an 'o' line is reached
                        object = new Strixa3DElement();
                    }else{ //This is teh case whenever we have finished with one object and moved onto the next.
                        object.addComponents(object_polygons);
                        this.__objects.add(object);
                        
                        object = new Strixa3DElement();
                    }
                }else if(split[0].equals("v")){
                    if(split.length < 4){
                        throw new RuntimeException("Object line incorrectly formatted!  Line number:  " + line_number);
                    }else{
                        object_points.add(new StrixaPoint(Double.parseDouble(split[1]),Double.parseDouble(split[2]),Double.parseDouble(split[3]),Color.WHITE,(byte)1));
                    }
                }else if(split[0].equals("f")){  //This shouldn't occur until all the vertices have been given.
                    if(split.length < 4){  //A face must have at least 3 points
                        throw new RuntimeException("Object line incorrectly formatted!  Line number:  " + line_number);
                    }else{
                        polygon = new StrixaPolygon();
                        for(int index = 1;index < split.length; index++){                            
                            try{
                                face_split = split[index].split("/");
                                
                                polygon.addPoint(object_points.get(Integer.parseInt(face_split[0]) - 1));  //The verticies for the faces start at 1
                            }catch(IndexOutOfBoundsException e){
                                throw new RuntimeException("Given vertex was not found!  Requested vertex:  " + split[index] + ".  Line number:  " + line_number + ".");
                            }
                        }
                        object_polygons.add(polygon);
                    }
                }
                
                
                percent_loaded = ((total_bytes - file.available()) / total_bytes) * 100;
                if((percent_loaded - last_update) > this.__update_step && percent_loaded < 100){  //We want to reserve the 100% loaded update for when this method completes its run
                    this._alertPercentLoadedUpdateListeners(percent_loaded);
                    
                    last_update = percent_loaded;
                }
            }
            
            if(!object_polygons.isEmpty()){ //This ensures that the last object gets added to the list.
                object.addComponents(object_polygons);
                this.__objects.add(object);
            }
        }catch(FileNotFoundException e){
            throw new RuntimeException("No such file was found in the given path:  "+this.__file_location);
        }catch(IOException e){
            RuntimeException exception = null; 
            
            
            exception = new RuntimeException(e.getMessage());
            exception.setStackTrace(e.getStackTrace());
            throw exception;  //We have to turn any IOExceptions into RuntimeExceptions 
        }finally{
            try{
                file.close();
            }catch(IOException e){
                throw new RuntimeException("Could not close file properly.");
            }
        }
        
        this.__file_read = true;
        this._alertPercentLoadedUpdateListeners(100);
    }
    
    public Strixa3DElement[] getElements(){
        if(!this.__file_read){
            throw new RuntimeException("You must first call read on this object to read from the file.");
        }
        
        return this.__objects.toArray(new Strixa3DElement[this.__objects.size()]);
    }
}

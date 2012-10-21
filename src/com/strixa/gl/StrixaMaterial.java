/**
 * File:  StrixaMaterial.java
 * Date of Creation:  Oct 15, 2012
 */
package com.strixa.gl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLException;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.strixa.util.Point2D;
import com.strixa.util.Point3D;

/**
 * TODO:  Write Class Description
 *
 * @author Nicholas Rogé
 */
public class StrixaMaterial{    
    float                 __alpha;
    float[]               __ambient_color;
    float[]               __diffuse_color;
    String                __material_name;
    float[]               __specular_color;
    float                 __specular_coefficient;
    Texture               __texture;
    String                __texture_file_location;
    
    
    /*Begin Constructors*/
    public StrixaMaterial(String material_name){
        if(material_name == null){
            throw new NullPointerException("Argument 'material_name' must not be null.");
        }
        
        this.__material_name = material_name;
    }
    /*End Constructors*/
    
    /*Begin Getter/Setter Methods*/
    public float[] getAbientColor(){
        return this.__ambient_color;
    }
    
    public float getAlpha(){
        return this.__alpha;
    }
    
    public float[] getDiffuseColor(){
        return this.__diffuse_color;
    }
    
    public String getMaterialName(){
        return this.__material_name;
    }
    
    public float[] getSpecularColor(){
        return this.__specular_color;
    }
    
    public float getSpecularCoefficient(){
        return this.__specular_coefficient;
    }
    
    public Texture getTexture(){
        return this.__texture;
    }
    
    public void setAmbientColor(float[] color){
        this.__ambient_color = color;
    }
    
    public void setAlpha(float alpha){
        this.__alpha = alpha;
    }
    
    public void setDiffuseColor(float[] color){
        this.__diffuse_color = color;
    }
    
    public void setSpecularColor(float[] color){
        this.__specular_color = color;
    }
    
    public void setSpecularCoefficient(float specular_coefficient){
        this.__specular_coefficient = specular_coefficient;
    }
    
    public void setTexture(String file_location){    
        this.__texture_file_location = file_location;
    }
    
    public void setTexture(Texture texture){
        this.__texture = texture;
    }
    
    public void setTransparency(float transparency){
        this.__alpha = transparency;
    }
    /*End Getter/Setter Methods*/
    
    /*Begin Other Methods*/        
    public boolean hasTexture(){
        if(this.__texture_file_location == null){
            return false;
        }else{
            return true;
        }
    }
    
    public boolean isTextureLoaded(){
        if(this.__texture  == null){
            return false;
        }else{
            return true;
        }
    }
    
    public void loadTexture() throws IOException{
        GL2 gl = null;
        
        
        if(this.__texture_file_location == null){
            throw new IOException("You must call either loadTexture(String) or setTexture before attempting to call this method.");
        }
        
        
        try{
            gl = GLContext.getCurrentGL().getGL2();
        }catch(GLException e){
            throw new RuntimeException("This method must be called from a thread with an active GLContext.");
        }
        
        if(this.__texture != null){
            this.__texture.dispose(gl);
        }
        this.__texture = TextureIO.newTexture(new File(this.__texture_file_location),false);
        this.__texture.setTexParameteri(gl,GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
        this.__texture.setTexParameteri(gl,GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
    }
    
    public void loadTexture(String file_location) throws IOException{
        this.setTexture(file_location);
        
        this.loadTexture();
    }
    /*End Other Methods*/
}

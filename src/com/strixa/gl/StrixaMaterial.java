/**
 * File:  StrixaMaterial.java
 * Date of Creation:  Oct 15, 2012
 */
package com.strixa.gl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLException;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.strixa.util.Point2D;
import com.strixa.util.Point3D;

/**
 * Describes the material a StrixaElement is made of.
 *
 * @author Nicholas Rogé
 */
public class StrixaMaterial{
	private static final Map<String,StrixaMaterial> __material_map = new HashMap<String,StrixaMaterial>(); 
	
    float                 __alpha;
    float[]               __ambient_color;
    float[]               __diffuse_color;
    String                __name;
    float[]               __specular_color;
    float                 __specular_coefficient;
    Texture               __texture;
    String                __texture_file_location;
    
    
    /*Begin Constructors*/
    /**
     * Constructs a new anonymous material.  This material will not be able to be retrieved if all handles to this object are lost.
     *///TODO:  Reword that.  I don't like how it's worded.  :/
    public StrixaMaterial(){
    	this.__name = "";
    }
    
    /**
     * Constructs a material with the given name, allowing it to be retrieved in the future using {@link StrixaMaterial#getMaterialByName(String)}.
     * 
     * @param material_name Name this material should have.
     */
    public StrixaMaterial(String material_name){
        if(material_name == null){
            throw new NullPointerException("Argument 'material_name' must not be null.");
        }
        
        this.__name = material_name;
    }
    /*End Constructors*/
    
    /*Begin Getter/Setter Methods*/
    /**
     * Gets the material's ambient color. 
     * 
     * @return The material's ambient color as a float array with length 3.  Each element represents a red, green, or blue component of the color where a value of 0 indicates no intensity, and 1 indicates full intensity.
     */
    public float[] getAmbientColor(){
        return this.__ambient_color;
    }
    
    /**
     * Gets the materia's alpha value. 
     * 
     * @return The material's alpha value.  A value of 1 indicates a fully opaque material, and a value of 0 indicates a fully transparent material.
     */
    public float getAlpha(){
        return this.__alpha;
    }
    
    /**
     * Gets the material's diffuse color. 
     * 
     * @return The material's diffuse color as a float array with length 3.  Each element represents a red, green, or blue component of the color where a value of 0 indicates no intensity, and 1 indicates full intensity.
     */
    public float[] getDiffuseColor(){
        return this.__diffuse_color;
    }
    
    /**
     * Gets this material's name.
     * 
     * @return A string representing this materials name.
     */
    public String getName(){
        return this.__name;
    }
    
    /**
     * Gets the material's specular color. 
     * 
     * @return The material's specular color as a float array with length 3.  Each element represents a red, green, or blue component of the color where a value of 0 indicates no intensity, and 1 indicates full intensity.
     */
    public float[] getSpecularColor(){
        return this.__specular_color;
    }
    
    /**
     * Gets this material's specular coefficient.
     * 
     * @return The material's specular coefficient.  This will be a value between 0 and 1000 which represents this material's "shininess".
     */
    public float getSpecularCoefficient(){
        return this.__specular_coefficient;
    }
    
    /**
     * Gets the texture currently registered to this material.
     * 
     * @return The texture currently assigned to this material.  This may be null if no texture has been assigned to this material.
     */
    public Texture getTexture(){
        return this.__texture;
    }
    
    /**
     * Sets the material's ambient color. 
     * 
     * @param color The material's ambient color as a float array with length 3.  Each element represents a red, green, or blue component of the color where a value of 0 indicates no intensity, and 1 indicates full intensity.
     */
    public void setAmbientColor(float[] color){
        this.__ambient_color = color;
    }
    
    /**
     * Sets this material's alpha value.
     * 
     * @param alpha The material's alpha value.  A value of 1 indicates a fully opaque material, and a value of 0 indicates a fully transparent material.
     */
    public void setAlpha(float alpha){
        this.__alpha = alpha;
    }
    
    /**
     * Sets the material's diffuse color. 
     * 
     * @param color The material's diffuse color as a float array with length 3.  Each element represents a red, green, or blue component of the color where a value of 0 indicates no intensity, and 1 indicates full intensity.
     */
    public void setDiffuseColor(float[] color){
        this.__diffuse_color = color;
    }
    
    /**
     * Sets the material's specular color. 
     * 
     * @param color The material's specular color as a float array with length 3.  Each element represents a red, green, or blue component of the color where a value of 0 indicates no intensity, and 1 indicates full intensity.
     */
    public void setSpecularColor(float[] color){
        this.__specular_color = color;
    }
    
    /**
     * Sets this material's specular coefficient.
     * 
     * @param specular_coefficient The material's specular coefficient.  This will be a value between 0 and 1000 which represents this material's "shininess".
     */
    public void setSpecularCoefficient(float specular_coefficient){
        this.__specular_coefficient = specular_coefficient;
    }
    
    /**
     * Sets the location for the texture of this material.  Using this method, you must call {@link StrixaMaterial#loadTexture()} from a later point in the program.
     * 
     * @param file_location Location of the image for this material's texture.
     */
    public void setTexture(String file_location){    
        this.__texture_file_location = file_location;
    }
    
    /**
     * Sets this material's texture.
     * 
     * @param texture Texture the material should take on.
     */
    public void setTexture(Texture texture){
        this.__texture = texture;
    }
    /*End Getter/Setter Methods*/
    
    /*Begin Other Methods*/
    /**
     * Check to determine whether this material has a texture or not.
     * <strong>Note:</strong>  Just because an this method returns true does not necessarily mean the texture has been loaded into memory.
     * 
     * @return Returns true, if this material has been assigned a texture, or false, otherwise.
     */
    public boolean hasTexture(){
    	if(this.__texture != null || this.__texture_file_location != null){
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * Check to determine whether this material's texture has been loaded into memory or not.
     * 
     * @return Returns true if the texture is loaded and available to be used, and false, otherwise.
     */
    public boolean isTextureLoaded(){
        if(this.__texture  == null){
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * Loads the requested texture into memory.
     */
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
    
    /**
     * Loads the requested texture into memory.
     * 
     * @param file_location Path to the image for the texture that should be loaded into memory.
     */
    public void loadTexture(String file_location) throws IOException{
        this.setTexture(file_location);
        
        this.loadTexture();
    }
    /*End Other Methods*/
    
    /*Begin Static Methods*/
    /**
     * Retrieves a given material based on its name.
     * 
     * @param material_name Name of the material to be retrieved.
     * 
     * @return Material whose name matches the argument given.
     */
    public static StrixaMaterial getMaterialByName(String material_name){
    	return StrixaMaterial.__material_map.get(material_name);
    }
    
    /**
     * Registers a material to be eligible to be retrieved using the {@link StrixaMaterial#getMaterialByName(String)} method.
     * 
     * @param material Material to register.
     * 
     * @throws IllegalArgumentException Thrown if the given material has no name.
     */
    protected static void _registerMaterial(StrixaMaterial material){
    	if(material.getName().isEmpty()){
    		throw new IllegalArgumentException();
    	}
    	
    	StrixaMaterial.__material_map.put(material.getName(),material);
    }
    /*End Static Methods*/
}

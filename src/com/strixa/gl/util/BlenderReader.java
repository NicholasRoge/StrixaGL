/**
 * File:  BlendReader.java
 * Date of Creation:  Sep 24, 2012
 */
package com.strixa.gl.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;


/**
 * Reads in a .blend file.<br />
 * <strong>IMPORTANT NOTE:</strong>  This class is only in the first stages of being implemented and currently doesn't work.
 * 
 * @author Nicholas Rogé
 */
public class BlenderReader{
    /**
     * Describes the contents of the Header of a blender file.  This is a read-only structure.
     *
     * @author Nicholas Rogé
     */
    protected static class HeaderBlock{
        private boolean __big_endian;
        private String  __identifier;
        private int     __major_version;
        private int     __minor_version;
        private int     __pointer_size;
        
        
        /*Begin Constructor*/
        public HeaderBlock(String identifier,int pointer_size,boolean big_endian,int major_version,int minor_version){
            this.__identifier = identifier;
            this.__pointer_size = pointer_size;
            this.__big_endian = big_endian;
            this.__major_version = major_version;
            this.__minor_version = minor_version;
        }
        /*End constructor*/
        
        /*Begin Getter Methods*/
        /**
         * Gets the identifier associated with this file.  In almost all cases, this will be "BLENDER"
         * 
         * @return The identifier associated with this file.
         */
        public String getIdentifier(){
            return new String(this.__identifier);
        }
        
        /**
         * Gets the size of the pointers associated with this file.
         * 
         * @return The size of the pointers associated with this file.
         */
        public int getPointerSize(){
            return this.__pointer_size;
        }
        
        public int getMajorVersion(){
            return this.__major_version;
        }
        
        public int getMinorVersion(){
            return this.__minor_version;
        }
        
        /**
         * Returns the endianness of the bytes in this file.
         * 
         * @return Returns true if the bytes are big-endian, and false, otherwise.
         */
        public boolean isBigEndian(){
            return this.__big_endian;
        }
        /*End Getter Methods*/
    }
    
    protected static class FileBlock{
        public static class Structure{
            
        }
        
        private byte[] __block_data;
        private String __identifier;
        private int    __sdna_structure_index;
        private int    __structure_count;
        
        
        /*Begin Constructor*/
        public FileBlock(String identifier,int sdna_structure_index,int structure_count,byte[] block_data){
            this.__identifier = identifier;
            this.__sdna_structure_index = sdna_structure_index;
            this.__structure_count = structure_count;
            this.__block_data = block_data;
        }
        /*End Constructor*/
        
        /*Begin Getters*/
        public String getIdentifier(){
            return new String(this.__identifier);
        }
        /*End Getters*/
    }
    
    private String          __file_location;
    private boolean         __file_read;
    private HeaderBlock     __header_block;
    private List<FileBlock> __file_blocks;
    
    
    /*Begin Constructor*/
    /**
     * Constructs the object, making it ready to read from the given file.
     * 
     * @param file_location Location on the file system where the blender file is located.
     */
    public BlenderReader(String file_location){
        if(file_location == null || file_location.equals("")){
            throw new IllegalArgumentException("Argument 'file_location' must not be null or empty.");
        }
        
        this.__file_location = file_location;
        this.__file_read = false;
    }
    /*End Constructor*/
    
    public void read() throws IOException{
        FileInputStream blend_file = null;
        FileBlock       file_block = null;
        byte[]          read_array = null;
        String          tmp_string = null;
        
        
        try{
            blend_file = new FileInputStream(this.__file_location);
            
            //Confirm the file we're openign is a blender file.
            this.__header_block = this.readHeaderBlock(blend_file);
            if(!this.__header_block.getIdentifier().equals("BLENDER")){
                throw new IOException("File given is not a blender file!");
            }
            
            this.__file_blocks = new ArrayList<FileBlock>();
            while((file_block = this.readNextFileBlock(blend_file))!=null){
                this.__file_blocks.add(file_block);
            }
            System.out.println("TEST");
        }catch(FileNotFoundException e){
            throw new FileNotFoundException("No such file was found in the given path:  "+this.__file_location);
        }catch(IOException e){
            throw e;
        }finally{
            blend_file.close();
        }
        
        this.__file_read = true;
    }
    
    protected HeaderBlock readHeaderBlock(FileInputStream file) throws IOException{
        boolean big_endian = false;
        String  identifier = null;
        int     major_version = 0;
        int     minor_version = 0;
        int     pointer_size = 0;
        byte[]  read_array = null;
        
        
        read_array = new byte[7];
        file.read(read_array);
        identifier = new String(read_array);
        
        read_array = new byte[1];
        file.read(read_array);
        switch((char)read_array[0]){
            case '_':
                pointer_size = 4;
                break;
            case '-':
                pointer_size = 8;
                break;
            default:
                throw new IOException("Something, somewhere has gone horribly wrong.  The pointer size byte isn't what we were expecting at all!");
        }
        
        file.read(read_array);
        switch((char)read_array[0]){
            case 'v':
                big_endian = false;
                break;
            case 'V':
                big_endian = true;
                break;
            default:
                throw new IOException("Something, somewhere has gone horribly wrong.  The endianness byte isn't what we were expecting at all!");
        }
        
        read_array = new byte[3];
        file.read(read_array);
        major_version = read_array[0] - '0';
        minor_version = ((read_array[1] - '0')*10)+(read_array[2] - '0');
        
        return new HeaderBlock(identifier,pointer_size,big_endian,major_version,minor_version);
    }
    int data_count = 0;
    protected FileBlock readNextFileBlock(FileInputStream file) throws IOException{
        String block_identifier = null;
        int    data_length = 0;
        byte[] read_array = null;
        int    sdna_structure_index = 0;
        int    structure_count = 0;
        
        
        
        //Get the block identifier
        read_array = new byte[4];
        file.read(read_array);
        block_identifier = new String(read_array);
        if(block_identifier.equals("ENDB")){
            return null;
        }else if(block_identifier.equals("DATA")){
            data_count++;
        }
        
        //Get the block data length.
        file.read(read_array);
        data_length = (read_array[3] << 24) + (read_array[2] << 16) + (read_array[1] << 8) + read_array[0];
        
        //Skip over the old memory address.
        file.skip(this.__header_block.getPointerSize());
        
        //Get the block SDNA index.
        file.read(read_array);
        if(this.__header_block.isBigEndian()){
            sdna_structure_index = (read_array[0] << 24) + (read_array[1] << 16) + (read_array[2] << 8) + read_array[3];
        }else{
            sdna_structure_index = (read_array[3] << 24) + (read_array[2] << 16) + (read_array[1] << 8) + read_array[0];
        }
        
        //Get the block structure count.
        file.read(read_array);
        structure_count = (read_array[3] << 24) + (read_array[2] << 16) + (read_array[1] << 8) + read_array[0];
        
        if(data_length>0){
            read_array = new byte[data_length];
            file.read(read_array);
        }else{
            read_array = null;
        }
        
        return new FileBlock(block_identifier,sdna_structure_index,structure_count,read_array);
    }
}
